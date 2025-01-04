package com.kamarkaka.appupdater;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kamarkaka.commons.KProperties;

public class DSLContextFactory {
    private static final Logger logger = LoggerFactory.getLogger(DSLContextFactory.class);

    public static DSLContext get() {
        String dbHost = KProperties.getString("DB_HOST");
        int dbPort = KProperties.getInt("DB_PORT");
        String dbName = KProperties.getString("DB_NAME");
        String dbUser = KProperties.getString("DB_USER");
        String dbPassword = KProperties.getString("DB_PASSWORD");
        String dbUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;

        try {
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            DSLContext db = DSL.using(conn, SQLDialect.POSTGRES);
            return db;
        } catch (SQLException ex) {
            logger.error("Error connecting to database", ex);
        }

        throw new RuntimeException("Error connecting to database");
    }
}
