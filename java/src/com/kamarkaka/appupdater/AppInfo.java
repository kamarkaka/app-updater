package com.kamarkaka.appupdater;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kotlin.Pair;

public class AppInfo {
    private static final Logger logger = LoggerFactory.getLogger(AppUpdater.class);

    private final UUID appId;
    private final String appName;
    private final String urlBase;
    private final String urlBegin;
    private final List<String> patterns;
    private final Map<String, List<Pair<String, String>>> headers;
    private final boolean forceDownload;

    private String filename;
    private String md5;
    private String link;

    public AppInfo(UUID appId, String appName, String urlBase, String urlBegin, List<String> patterns, Map<String, List<Pair<String, String>>> headers, String filename, String md5, boolean forceDownload) {
        this.appId = appId;
        this.appName = appName;
        this.urlBase = urlBase;
        this.urlBegin = urlBegin;
        this.patterns = patterns;
        this.headers = headers;
        this.forceDownload = forceDownload;

        this.filename = filename;
        this.md5 = md5;
    }

    public AppInfo(AppInfo info) {
        this.appId = info.getAppId();
        this.appName = info.getAppName();
        this.urlBase = info.getUrlBase();
        this.urlBegin = info.getUrlBegin();
        this.patterns = info.getPatterns();
        this.headers = info.getHeaders();
        this.forceDownload = info.getForceDownload();
        this.filename = info.getFilename();
        this.md5 = info.getMd5();
    }

    public UUID getAppId() {
        return appId;
    }

    public String getAppName() {
        return appName;
    }

    public String getUrlBase() {
        return urlBase;
    }

    public String getUrlBegin() {
        return urlBegin;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public Map<String, List<Pair<String, String>>> getHeaders() {
        return headers;
    }

    public boolean getForceDownload() {
        return forceDownload;
    }

    public String getFilename() {
        return filename;
    }

    public String getMd5() {
        return md5;
    }

    public String getLink() {
        return link;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFile(File file) {
        this.filename = file.getName();
        this.md5 = getMd5Hash(file);
    }

    public void setLink(String link) {
        this.link = link;
    }

    /** compute MD5 of a file */
    public static String getMd5Hash(File file) {
        try (InputStream  inputStream = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
            return DigestUtils.md5Hex(inputStream);
        } catch (IOException ex) {
            logger.error("Error getting MD5 of file {}", file, ex);
            return "";
        }
    }
}
