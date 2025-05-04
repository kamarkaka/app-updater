package com.kamarkaka.appupdater;

import com.kamarkaka.commons.BaseCliApp;
import com.kamarkaka.commons.Mailer;
import com.kamarkaka.jooq.model.tables.AppHeaders;
import com.kamarkaka.jooq.model.tables.AppPatterns;
import com.kamarkaka.jooq.model.tables.Apps;

import kotlin.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunApp extends BaseCliApp {
    private static final Logger logger = LoggerFactory.getLogger(RunApp.class);

    public static void main(String[] args) throws Exception {
        AppOptions appOptions = (AppOptions) init(new AppOptions(), args);

        String outputDir = appOptions.getOutputDir();
        boolean sendEmail = appOptions.isSendEmail();

        List<String> results = new ArrayList<>();

        Result<Record> appsResult;
        DSLContext db = DSLContextFactory.get();
        var sql = db.select().from(Apps.APPS);

        if (appOptions.getAppId() != null) {
            appsResult = sql.where(Apps.APPS.APP_ID.eq(UUID.fromString(appOptions.getAppId()))).fetch();
        } else {
            appsResult = sql.orderBy(Apps.APPS.APP_NAME).fetch();
        }

        int count = 0;
        int size = appsResult.size();

        for (Record appRecord : appsResult) {
            UUID appId = appRecord.get(Apps.APPS.APP_ID, UUID.class);
            String appName = appRecord.get(Apps.APPS.APP_NAME, String.class);
            String urlBase = appRecord.get(Apps.APPS.URL_BASE, String.class);

            logger.info("Updating [{}] ({}/{})...", appName, ++count, size);

            String urlBegin = "";
            if (appRecord.get(Apps.APPS.URL_BEGIN) != null) {
                urlBegin = appRecord.get(Apps.APPS.URL_BEGIN, String.class);
            }

            String filename = "";
            if (appRecord.get(Apps.APPS.FILENAME) != null) {
                filename = appRecord.get(Apps.APPS.FILENAME, String.class);
            }

            String md5 = "";
            if (appRecord.get(Apps.APPS.HASH_MD5) != null) {
                md5 = appRecord.get(Apps.APPS.HASH_MD5, String.class);
            }

            List<String> patterns = new ArrayList<>();
            Result<Record> patternsResult = db.select()
                                              .from(AppPatterns.APP_PATTERNS)
                                              .where(AppPatterns.APP_PATTERNS.APP_ID.eq(appId))
                                              .orderBy(AppPatterns.APP_PATTERNS.PATTERN_ID)
                                              .fetch();
            for (Record patternRecord : patternsResult) {
                String pattern = patternRecord.get(AppPatterns.APP_PATTERNS.PATTERN, String.class);
                patterns.add(pattern);
            }

            boolean forceDownload = appRecord.get(Apps.APPS.FORCE_DOWNLOAD, Boolean.class);

            Map<String, List<Pair<String, String>>> headerMap = new HashMap<>();
            Result<Record> headersResult = db.select()
                                             .from(AppHeaders.APP_HEADERS)
                                             .where(AppHeaders.APP_HEADERS.APP_ID.eq(appId))
                                             .fetch();
            for (Record headerRecord : headersResult) {
                String type = headerRecord.get(AppHeaders.APP_HEADERS.TYPE, String.class);
                String key = headerRecord.get(AppHeaders.APP_HEADERS.KEY, String.class);
                String value = headerRecord.get(AppHeaders.APP_HEADERS.VALUE, String.class);
                headerMap.putIfAbsent(type, new ArrayList<Pair<String, String>>());
                headerMap.get(type).add(new Pair<>(key, value));
            }

            AppInfo appInfo = new AppInfo(appId, appName, urlBase, urlBegin, patterns, headerMap, filename, md5, forceDownload);

            AppUpdater updater = new AppUpdater(appInfo, db);
            String result = updater.runUpdate(outputDir);
            if (result != null) results.add(result);
        }

        if (results.isEmpty()) {
            results.add("All apps are up to date.");
        }

        results.forEach(logger::info);

        db.parsingConnection().close();

        if (sendEmail) {
            Mailer mailer = new Mailer("App Updater");
            mailer.send(
                new String[] {"caomeng1208@gmail.com"},
                "App Update Status",
                String.join("<br/>", results)
            );
        }

    }
}
