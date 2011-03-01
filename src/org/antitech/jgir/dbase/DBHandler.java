package org.antitech.jgir.dbase;
/**
 * This is our Database handler.
 * Keep it one connection for now.
 */
import java.sql.*;
import java.util.*;

import org.antitech.jgir.Config;
import org.antitech.jgir.logger.LogSystem;

public class DBHandler {

    private static Config conf = Config.getInstance();
    private LogSystem logger = LogSystem.getInstance();
    private Statement stmt = null;
    private Connection dbcon = null;
    
    public DBHandler() {
        this.createConnection();
    }

    private void createConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ce) { }
        String url = conf.readConfig("dburl");
        try {
            dbcon = DriverManager.getConnection(url, conf.readConfig("dbusername"),
                    conf.readConfig("dbpassword"));
            stmt = dbcon.createStatement();
        } catch (SQLException sqlE) {
            logger.log("Unable to Connect to Database");
        }
    }

    public Connection getdbConnection() {
        return dbcon;
    }

    public Statement getStatement() {
        return stmt;
    }
}

