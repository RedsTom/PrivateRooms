/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.utils.sql;

import java.sql.*;

public class DatabaseManager {

    private Connection connection = null;

    public DatabaseManager() {
    }

    public boolean openConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:servers.db");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void createTableIfNotExists(String table, String arguments) {
        try {
            PreparedStatement q = connection.prepareStatement(String.format("CREATE TABLE IF NOT EXISTS %s (%s)", table, arguments));
            q.execute();
            q.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getData(String table, String dataToGet, String condition) throws SQLException {
        PreparedStatement q = connection.prepareStatement("SELECT " + dataToGet + " FROM " + table + " WHERE " + condition);
        ResultSet rs = q.executeQuery();
        Object toReturn = rs.getObject(1);
        q.close();
        rs.close();
        return toReturn;
    }

    public Connection getConnection() {
        return connection;
    }
}