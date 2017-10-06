// 
// Decompiled by Procyon v0.5.30
// 

package com.mineaurion.tjk.GAL;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.vexsoftware.votifier.model.Vote;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.util.logging.Logger;

public final class DB
{
    public Logger log;
    public GAL plugin;
    
    public DB(final GAL plugin) {
        this.plugin = plugin;
        this.log = plugin.getLogger();
    }
    
    public Connection getConnection() {
        if (this.plugin.dbMode.equalsIgnoreCase("mysql")) {
            try {
                Connection connection = null;
                final String url = "jdbc:mysql://" + this.plugin.dbHost + ":" + this.plugin.dbPort + "/" + this.plugin.dbName + "?autoReconnect=true";
                connection = DriverManager.getConnection(url, this.plugin.dbUser, this.plugin.dbPass);
                return connection;
            }
            catch (SQLException ex) {
                return null;
            }
        }
        try {
            Connection connection = null;
            final String url = "jdbc:sqlite:" + this.plugin.getDataFolder().getAbsolutePath() + File.separator + this.plugin.dbFile;
            connection = DriverManager.getConnection(url);
            return connection;
        }
        catch (SQLException ex2) {}
        return null;
    }
    
    public boolean initConnection() {
        Label_0491: {
            if (this.plugin.dbMode.equalsIgnoreCase("mysql")) {
                try {
                    Connection connection = null;
                    Class.forName("com.mysql.jdbc.Driver");
                    final String url = "jdbc:mysql://" + this.plugin.dbHost + ":" + this.plugin.dbPort + "?autoReconnect=true";
                    connection = DriverManager.getConnection(url, this.plugin.dbUser, this.plugin.dbPass);
                    this.log.info("Connection established!");
                    final List<String> list = new ArrayList<String>();
                    final Statement st = connection.createStatement();
                    final DatabaseMetaData meta = connection.getMetaData();
                    final ResultSet rs = meta.getCatalogs();
                    while (rs.next()) {
                        final String listofDatabases = rs.getString("TABLE_CAT");
                        list.add(listofDatabases);
                    }
                    if (list.contains(this.plugin.dbName)) {
                        this.log.info("Found database: " + this.plugin.dbName);
                    }
                    else {
                        this.log.info("Database: " + this.plugin.dbName + " was not found, attempting to create.");
                        st.executeUpdate("CREATE DATABASE " + this.plugin.dbName);
                        this.log.info("Successfully created " + this.plugin.dbName);
                    }
                    rs.close();
                    connection.setCatalog(this.plugin.dbName);
                    this.log.info("Using database: " + this.plugin.dbName);
                    connection.close();
                    return true;
                }
                catch (ClassNotFoundException e) {
                    this.log.severe("JDBC driver not found!");
                    break Label_0491;
                }
                catch (SQLException ex) {
                    break Label_0491;
                }
            }
            try {
                Connection connection = null;
                Class.forName("org.sqlite.JDBC");
                final File folder = new File(this.plugin.getDataFolder().getAbsolutePath());
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                final String url2 = "jdbc:sqlite:" + this.plugin.getDataFolder().getAbsolutePath() + File.separator + this.plugin.dbFile;
                connection = DriverManager.getConnection(url2);
                this.log.info("Connection established!");
                connection.close();
                return true;
            }
            catch (ClassNotFoundException e) {
                this.log.severe("JDBC driver not found!");
            }
            catch (SQLException ex2) {}
        }
        this.log.severe("############################################");
        this.log.severe("SQL connection failed.  Please check your");
        this.log.severe("database configuration in config.yml");
        this.log.severe("");
        this.log.severe("Type \"/gal reload\" to reload the config");
        this.log.severe("############################################");
        return false;
    }
    
    public void createTables() {
        if (this.plugin.dbMode.equalsIgnoreCase("mysql")) {
            if (!this.tableExists(String.valueOf(this.plugin.dbPrefix) + "GALTotals")) {
                this.modifyQuery("CREATE TABLE `" + this.plugin.dbPrefix + "GALTotals` (`IGN` varchar(16) NOT NULL, `votes` int(10) DEFAULT 0, `lastvoted` BIGINT(16) DEFAULT 0, PRIMARY KEY (`IGN`));");
            }
            else {
                final String query = "SELECT `lastvoted` FROM `" + this.plugin.dbPrefix + "GALTotals` LIMIT 1;";
                if (!this.checkQuery(query)) {
                    this.modifyQuery("ALTER TABLE `" + this.plugin.dbPrefix + "GALTotals` ADD  `lastvoted` BIGINT(16) DEFAULT 0 AFTER `votes`;");
                }
            }
            if (!this.tableExists(String.valueOf(this.plugin.dbPrefix) + "GALQueue")) {
                this.modifyQuery("CREATE TABLE `" + this.plugin.dbPrefix + "GALQueue` (" + "`IGN` varchar(16) NOT NULL,`service` varchar(64), `timestamp` varchar(32), `ip` varchar(32));");
            }
        }
        else {
            if (!this.tableExists(String.valueOf(this.plugin.dbPrefix) + "GALTotals")) {
                final String query = "CREATE TABLE `" + this.plugin.dbPrefix + "GALTotals` (`IGN` VARCHAR UNIQUE, `votes` INTEGER DEFAULT 0, `lastvoted` INTEGER DEFAULT 0);";
                this.modifyQuery(query);
            }
            else {
                final String query = "SELECT `lastvoted` FROM `" + this.plugin.dbPrefix + "GALTotals` LIMIT 1;";
                if (!this.checkQuery(query)) {
                    this.modifyQuery("ALTER TABLE `" + this.plugin.dbPrefix + "GALTotals` ADD `lastvoted` INTEGER DEFAULT 0;");
                }
            }
            if (!this.tableExists(String.valueOf(this.plugin.dbPrefix) + "GALQueue")) {
                final String query = "CREATE TABLE `" + this.plugin.dbPrefix + "GALQueue` (`IGN` VARCHAR, `service` VARCHAR, `timestamp` VARCHAR, `ip` VARCHAR);";
                this.modifyQuery(query);
            }
        }
    }
    
    public boolean tableExists(final String table) {
        final Connection connection = this.getConnection();
        if (connection == null) {
            return false;
        }
        try {
            final DatabaseMetaData dmd = connection.getMetaData();
            final ResultSet rs = dmd.getTables(null, null, table, null);
            if (rs.next()) {
                rs.close();
                connection.close();
                return true;
            }
            rs.close();
            connection.close();
            return false;
        }
        catch (Exception e) {
            try {
                connection.close();
            }
            catch (SQLException ex) {}
            return false;
        }
    }
    
    public void modifyQuery(final String query) {
        final Connection connection = this.getConnection();
        if (connection == null) {
            return;
        }
        try {
            final Statement stmt = connection.createStatement();
            stmt.execute(query);
            stmt.close();
        }
        catch (SQLException ex) {}
        finally {
            try {
                connection.close();
            }
            catch (SQLException ex2) {}
        }
        try {
            connection.close();
        }
        catch (SQLException ex3) {}
    }
    
    public boolean checkQuery(final String query) {
        final Connection connection = this.getConnection();
        boolean success = false;
        if (connection == null) {
            return false;
        }
        try {
            final Statement stmt = connection.createStatement();
            stmt.executeQuery(query);
            stmt.close();
            success = true;
        }
        catch (SQLException ex) {}
        finally {
            try {
                connection.close();
            }
            catch (SQLException ex2) {}
        }
        try {
            connection.close();
        }
        catch (SQLException ex3) {}
        return success;
    }
    
    public int getVotes(final String player) {
        int votes = 0;
        final Connection connection = this.getConnection();
        if (connection == null) {
            return votes;
        }
        try {
            final PreparedStatement stmt = connection.prepareStatement("SELECT votes FROM " + this.plugin.dbPrefix + "GALTotals WHERE LOWER(`IGN`) = ?;");
            stmt.setString(1, player);
            final ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                votes = rs.getInt(1);
            }
            rs.close();
            stmt.close();
        }
        catch (SQLException ex) {}
        finally {
            try {
                connection.close();
            }
            catch (SQLException ex2) {}
        }
        try {
            connection.close();
        }
        catch (SQLException ex3) {}
        return votes;
    }
    
    public Map<String, Integer> getVoteTop(final int limit) {
        final Map<String, Integer> votes = new LinkedHashMap<String, Integer>();
        final Connection connection = this.getConnection();
        if (connection == null) {
            return votes;
        }
        try {
            final PreparedStatement stmt = connection.prepareStatement("SELECT * FROM `" + this.plugin.dbPrefix + "GALTotals` ORDER BY `votes` DESC LIMIT ?;");
            stmt.setInt(1, limit);
            final ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                votes.put(rs.getString(1), rs.getInt(2));
            }
            rs.close();
            stmt.close();
        }
        catch (SQLException ex) {}
        finally {
            try {
                connection.close();
            }
            catch (SQLException ex2) {}
        }
        try {
            connection.close();
        }
        catch (SQLException ex3) {}
        return votes;
    }
    
    public ListMultimap<VoteType, GALReward> getQueuedVotes() {
        final ListMultimap<VoteType, GALReward> queuedVotes = ArrayListMultimap.create();
        final Connection connection = this.getConnection();
        if (connection == null) {
            return queuedVotes;
        }
        try {
            final Statement stmt = connection.createStatement();
            final ResultSet rs = stmt.executeQuery("SELECT * FROM " + this.plugin.dbPrefix + "GALQueue;");
            while (rs.next()) {
                final Vote vote = new Vote();
                final String player = rs.getString(1);
                final String service = rs.getString(2);
                final String time = rs.getString(3);
                final String ip = rs.getString(4);
                vote.setUsername(player);
                vote.setServiceName(service);
                vote.setAddress(ip);
                vote.setTimeStamp(time);
                queuedVotes.put(VoteType.NORMAL, new GALReward(VoteType.NORMAL, service, vote, true));
            }
            rs.close();
            stmt.close();
        }
        catch (SQLException ex) {}
        finally {
            try {
                connection.close();
            }
            catch (SQLException ex2) {}
        }
        try {
            connection.close();
        }
        catch (SQLException ex3) {}
        return queuedVotes;
    }
    
    public Table<String, Integer, Long> getTotals() {
        final Table<String, Integer, Long> totals = HashBasedTable.create();
        final Connection connection = this.getConnection();
        if (connection == null) {
            return totals;
        }
        try {
            final Statement stmt = connection.createStatement();
            final ResultSet rs = stmt.executeQuery("SELECT * FROM " + this.plugin.dbPrefix + "GALTotals;");
            while (rs.next()) {
                totals.put(rs.getString(1), rs.getInt(2), rs.getLong(3));
            }
            rs.close();
            stmt.close();
        }
        catch (SQLException ex) {}
        finally {
            try {
                connection.close();
            }
            catch (SQLException ex2) {}
        }
        try {
            connection.close();
        }
        catch (SQLException ex3) {}
        return totals;
    }
    
    public synchronized void setVotes(final String player, final int votes, final boolean increment) {
        final Long now = System.currentTimeMillis();
        final Connection connection = this.getConnection();
        if (connection == null) {
            return;
        }
        try {
            if (this.plugin.dbMode.equalsIgnoreCase("mysql")) {
                if (increment) {
                    final PreparedStatement stmt = connection.prepareStatement("INSERT INTO `" + this.plugin.dbPrefix + "GALTotals` (`IGN`, `votes`, `lastvoted`) VALUES (?, 1, ?) ON DUPLICATE KEY UPDATE `votes` = `votes` + 1, `lastvoted` = ?, `IGN` = ?;");
                    stmt.setString(1, player);
                    stmt.setLong(2, now);
                    stmt.setLong(3, now);
                    stmt.setString(4, player);
                    stmt.executeUpdate();
                    stmt.close();
                }
                else {
                    final PreparedStatement stmt = connection.prepareStatement("INSERT INTO `" + this.plugin.dbPrefix + "GALTotals` (`IGN`, `votes`, `lastvoted`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `votes` = ?, `lastvoted` = ?, `IGN` = ?;");
                    stmt.setString(1, player);
                    stmt.setInt(2, votes);
                    stmt.setLong(3, now);
                    stmt.setInt(4, votes);
                    stmt.setLong(5, now);
                    stmt.setString(6, player);
                    stmt.executeUpdate();
                    stmt.close();
                }
            }
            else if (increment) {
                PreparedStatement stmt = connection.prepareStatement("INSERT OR IGNORE INTO `" + this.plugin.dbPrefix + "GALTotals` (`IGN`, `votes`, `lastvoted`) VALUES (?, 0, ?);");
                stmt.setString(1, player);
                stmt.setLong(2, now);
                stmt.executeUpdate();
                stmt.close();
                stmt = connection.prepareStatement("UPDATE `" + this.plugin.dbPrefix + "GALTotals` SET `votes` = `votes` + 1, `lastvoted` = ?, `IGN` = ? WHERE LOWER(`IGN`) = ?;");
                stmt.setLong(1, now);
                stmt.setString(2, player);
                stmt.setString(3, player.toLowerCase());
                stmt.executeUpdate();
                stmt.close();
            }
            else {
                PreparedStatement stmt = connection.prepareStatement("INSERT OR IGNORE INTO `" + this.plugin.dbPrefix + "GALTotals` (`IGN`, `votes`, `lastvoted`) VALUES (?, ?, ?);");
                stmt.setString(1, player);
                stmt.setInt(2, votes);
                stmt.setLong(3, now);
                stmt.executeUpdate();
                stmt.close();
                stmt = connection.prepareStatement("UPDATE `" + this.plugin.dbPrefix + "GALTotals` SET `votes` = ?, `lastvoted` = ?, `IGN` = ? WHERE LOWER(`IGN`) = ?;");
                stmt.setInt(1, votes);
                stmt.setLong(2, now);
                stmt.setString(3, player);
                stmt.setString(4, player.toLowerCase());
                stmt.executeUpdate();
                stmt.close();
            }
        }
        catch (SQLException ex) {}
        finally {
            try {
                connection.close();
            }
            catch (SQLException ex2) {}
        }
        try {
            connection.close();
        }
        catch (SQLException ex3) {}
    }
}
