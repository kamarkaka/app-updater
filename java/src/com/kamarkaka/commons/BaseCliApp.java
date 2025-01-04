package com.kamarkaka.commons;

import com.beust.jcommander.JCommander;

/** base template for a cli app that will parse arguments */
public abstract class BaseCliApp {
    public static BaseAppOptions init(BaseAppOptions appOptions, String[] args) {
        JCommander jc = JCommander.newBuilder().addObject(appOptions).build();
        jc.parse(args);

        if (appOptions.isHelp()) {
            jc.usage();
            System.exit(0);
        }

        BaseInit.init(appOptions);
        return appOptions;
    }
}
