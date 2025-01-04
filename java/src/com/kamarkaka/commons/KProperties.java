package com.kamarkaka.commons;

import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** system properties loaded from config file */
public class KProperties {
    private static final Logger logger = LoggerFactory.getLogger(KProperties.class);

    private static final Properties properties = new Properties();

    private KProperties() {}

    /** load system config from file */
    public static void init(String configPath) {
        logger.info("Reading config from {}...", configPath);
        try (FileInputStream input = new FileInputStream(configPath)) {
            properties.load(input);
        } catch (Exception ex) {
            logger.warn("Cannot load properties from config, using system environment variables", ex);
        }
    }

    /** get property value as a string */
    public static String getString(String key) {
        return properties.getProperty(key, System.getenv(key));
    }

    /** get property value as an integer */
    public static int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key, System.getenv(key)));
    }
}
