package com.kamarkaka.appupdater;

import com.kamarkaka.commons.HttpClient;
import com.kamarkaka.commons.ResponseData;
import com.kamarkaka.jooq.model.tables.Apps;

import kotlin.Pair;
import okhttp3.Request;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** checks an app for updates */
public class AppUpdater {
    private static final Logger logger = LoggerFactory.getLogger(AppUpdater.class);

    private final HttpClient client;
    private final AppInfo oldInfo;
    private final DSLContext db;

    /** constructor */
    public AppUpdater(AppInfo oldInfo, DSLContext db) {
        this.client = new HttpClient();
        this.oldInfo = oldInfo;
        this.db = db;
    }

    /** run update check for an app, if an output directory is specified, download the updated app there */
    public String runUpdate(String outputDir) {
        AppInfo newInfo = getAppInfo();
        if (newInfo == null) {
            return "Error parsing page: " + oldInfo.getUrlBase() + oldInfo.getUrlBegin();
        }

        logger.debug("app(name: {}, fileName: {}, link: {})", newInfo.getAppName(), newInfo.getFilename(), newInfo.getLink());

        boolean shouldDownload = false;

        if (newInfo.getForceDownload()) {
            shouldDownload = true;
        } else if (!newInfo.getFilename().equalsIgnoreCase(oldInfo.getFilename())) {
            // different file names, no need to download to know there is an update
            logger.info("Updates found for {} at {}", newInfo.getAppName(), newInfo.getLink());
            shouldDownload = true;
        }

        if (!shouldDownload) {
            logger.info("No updates found for {}", newInfo.getAppName());
            return null;
        }

        File file = downloadApp(newInfo, Paths.get(outputDir));
        if (file == null) {
            return "Error downloading " + newInfo.getAppName() + " from " + newInfo.getLink();
        }

        newInfo.setFile(file);

        logger.debug("existing file hash: {}", oldInfo.getMd5());
        logger.debug("downloaded file hash: {}", newInfo.getMd5());

        if (newInfo.getMd5().equalsIgnoreCase(oldInfo.getMd5())) {
            file.delete();

            logger.info("No updates found for {}", newInfo.getAppName());
            return null;
        }

        // update app in db
        db.update(Apps.APPS)
          .set(Apps.APPS.FILENAME, newInfo.getFilename())
          .set(Apps.APPS.HASH_MD5, newInfo.getMd5())
          .set(Apps.APPS.LAST_UPDATE, OffsetDateTime.now())
          .where(Apps.APPS.APP_ID.eq(newInfo.getAppId()))
          .execute();

        return "Update for " + newInfo.getAppName() + " downloaded: " + newInfo.getFilename() + " (" + newInfo.getMd5() + ")";
    }

    /** find app name, version, and download link */
    protected AppInfo getAppInfo() {
        AppInfo newInfo = new AppInfo(oldInfo);

        String link = newInfo.getUrlBase() + newInfo.getUrlBegin();

        for (String pattern : newInfo.getPatterns()) {
            link = getDownloadLink(link, pattern);

            if (link == null) {
                return null;
            }
        }
 
        int beginIndex = link.lastIndexOf("/") + 1;
        int endIndex = link.indexOf("?", beginIndex);

        String filename;
        if (beginIndex < endIndex) {
            filename = link.substring(beginIndex, endIndex);
        } else {
            filename = link.substring(beginIndex);
        }

        newInfo.setFilename(filename);
        newInfo.setLink(link);
        return newInfo;
    }

    /** get the content of a page, find link using a matching pattern */
    private String getDownloadLink(String url, String linkPattern) {
        Pattern pattern = Pattern.compile(linkPattern);

        List<Pair<String, String>> headers = new ArrayList<>();
        if (oldInfo.getHeaders() != null && oldInfo.getHeaders().containsKey("request")) {
            headers = oldInfo.getHeaders().get("request");
        }

        try {
            Request request = client.buildGetRequest(url, headers);
            logger.debug("url: {}", url);
            logger.debug("request headers: {}", request.headers());
            ResponseData response = client.execute(request);

            String responseBodyStr = response.getBody();
            Matcher matcher = pattern.matcher(responseBodyStr);

            if (matcher.find()) {
                String link = matcher.group(1).trim();
                // fucking asus bios/firmware site provides download url in unicode format
                // this is a temp hack which should work for the time being
                link = link.replace("\\u002F", "/");

                if (link.startsWith("/")) {
                    link = oldInfo.getUrlBase() + link;
                } else if (!link.startsWith("http")) {
                    link = oldInfo.getUrlBase() + "/" + link;
                }

                logger.debug("Successfully matched link: {}", link);
                return link;
            } else {
                logger.error("Error matching pattern {} in page {}", linkPattern, responseBodyStr);
                return null;
            }
        } catch (Exception ex) {
            logger.error("Error opening page {}", url, ex);
            return null;
        }
    }

    /** download app */
    protected File downloadApp(AppInfo appInfo, Path filePath) {
        List<Pair<String, String>> headers = new ArrayList<>();
        if (appInfo.getHeaders() != null && appInfo.getHeaders().containsKey("download")) {
            headers = appInfo.getHeaders().get("download");
        }

        try {
            Request request = client.buildGetRequest(appInfo.getLink(), headers);
            File file = client.downloadFile(request, filePath);
            return file;
        } catch (Exception ex) {
            logger.error("Error downloading file", ex);
        }

        return null;
    }
}
