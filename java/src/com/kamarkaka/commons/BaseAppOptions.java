package com.kamarkaka.commons;

import com.beust.jcommander.Parameter;

/** base app cli options */
public abstract class BaseAppOptions {
    public static final String DEFAULT_CONFIG_PATH = "./config/config.properties";

    @Parameter(names = {"-h", "--help"}, description = "Show help", help = true)
    private boolean help = false;

    @Parameter(names = {"-c", "--config"}, description = "Config file path")
    private String configPath = DEFAULT_CONFIG_PATH;

    @Parameter(names = {"-s", "--send-email"}, description = "Send results via email")
    private boolean sendEmail = false;

    public boolean isHelp() {
        return help;
    }

    public String getConfigPath() {
        return configPath;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }
}
