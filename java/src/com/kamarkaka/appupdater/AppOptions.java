package com.kamarkaka.appupdater;

import com.beust.jcommander.Parameter;
import com.kamarkaka.commons.BaseAppOptions;

public class AppOptions extends BaseAppOptions {
    @Parameter(names = {"-i", "--app-id"}, description = "App ID", required = false)
    private String appId = null;

    @Parameter(names = {"-o", "--output"}, description = "Output directory")
    private String outputDir = "./output/";

    public String getAppId() {
        return appId;
    }

    public String getOutputDir() {
        return outputDir;
    }
}
