package com.kamarkaka.commons;

/** provides a series of static methods for system initialization */
public class BaseInit {
    private BaseInit() {}

    /** system initialization with custom config, must be called before everything else */
    public static void init(BaseAppOptions appOptions) {
        KProperties.init(appOptions.getConfigPath());
    }

    public static void init() {
        KProperties.init(BaseAppOptions.DEFAULT_CONFIG_PATH);
    }
}
