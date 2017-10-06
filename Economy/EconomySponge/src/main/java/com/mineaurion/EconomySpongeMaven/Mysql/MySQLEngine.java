package com.mineaurion.EconomySpongeMaven.Mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.sql.SqlService;

import com.mineaurion.EconomySpongeMaven.LogInfo;
import com.mineaurion.EconomySpongeMaven.Main;
import com.mineaurion.EconomySpongeMaven.Mysql.Table.AccountTable;
import com.mineaurion.EconomySpongeMaven.Mysql.Table.LogTable;

public class MySQLEngine {
	public String serverName;
	public static String port;
	public static String databaseName;
	public static String user;
	public static String password;
	public static String prefix;
	public static DataSource dataSource;
	private SqlService sql;

	public static AccountTable accountTable;
	public static LogTable logTable;

	public MySQLEngine(Main main) {
		serverName = Main.rootNode.getNode("Database", "Address").getString();
		port = Main.rootNode.getNode("Database", "Port").getString();
		databaseName = Main.rootNode.getNode("Database", "Db").getString();
		user = Main.rootNode.getNode("Database", "Username").getString();
		password = Main.rootNode.getNode("Database", "Password").getString();
		prefix = Main.rootNode.getNode("Database", "Prefix").getString();

		try {
			dataSource = getDataSource("jdbc:mysql://" + serverName + ":" + port + "/" + databaseName + "?user=" + user
					+ "&password=" + password);
		} catch (SQLException e) {
			Main.sendmessage("{{DARK_RED}}Error getting data source!", "console");
			e.printStackTrace();
		}

		accountTable = new AccountTable(prefix);
		logTable = new LogTable(prefix);
	}

	public DataSource getDataSource(String jdbcUrl) throws SQLException {
		if (sql == null) {
			sql = Sponge.getServiceManager().provide(SqlService.class).get();
		}

		return sql.getDataSource(jdbcUrl);
	}

	public void setupDatabase() {
		try {
			Connection connection = dataSource.getConnection();
			connection.prepareStatement(accountTable.createTableMySQL).execute();
			connection.prepareStatement(logTable.createTableMySQL).execute();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Main.sendmessage("{{DARK_RED}}Error creating table source!", "console");
		}
	}

	public static boolean accountExist(String uuid) {
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = null;
			statement = connection.prepareStatement(accountTable.selectEntryUUID);
			statement.setString(1, uuid);
			ResultSet set = statement.executeQuery();
			if (set.isBeforeFirst()) {
				statement.close();
				connection.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
		}
		return false;
	}
	

	public static boolean NameExist(String name,String uuid) {
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = null;
			statement = connection.prepareStatement(accountTable.selectEntrywithName);
			statement.setString(1, name);
			ResultSet set = statement.executeQuery();
			if (set.isBeforeFirst()) {
				
				PreparedStatement statementName = null;
				statementName = connection.prepareStatement(MySQLEngine.accountTable.updateUuidByName);
				statementName.setString(1, name);
				statementName.setString(2, uuid);
				statementName.executeUpdate();
				statementName.close();
				statement.close();
				connection.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
		}
		return false;
	}
	public static double getBalanceName(String name,Boolean conection) {

		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = null;
			statement = connection.prepareStatement(accountTable.selectEntrywithName);
			statement.setString(1, name);
			ResultSet set = statement.executeQuery();
			if (set.next()) {
				if (!set.getBoolean("infiniMoney")) {
					double balance = set.getDouble("balance");
					statement.close();
					connection.close();
					return balance;
				} else {
					if(conection){
						double balance = set.getDouble("balance");
						statement.close();
						connection.close();
						return balance;
					}else{
					return Double.MAX_VALUE;}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
		}
		return 0.0D;
	}
	public static double getBalance(String uuid,Boolean conection) {

		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = null;
			statement = connection.prepareStatement(accountTable.selectEntryUUID);
			statement.setString(1, uuid);
			ResultSet set = statement.executeQuery();
			if (set.next()) {
				if (!set.getBoolean("infiniMoney")) {
					double balance = set.getDouble("balance");
					statement.close();
					connection.close();
					return balance;
				} else {
					if(conection){
						double balance = set.getDouble("balance");
						statement.close();
						connection.close();
						return balance;
					}else{
					return Double.MAX_VALUE;}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
		}
		return 0.0D;
	}

	public static boolean setBalance(String uuid, double amount) {
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = null;
			statement = connection.prepareStatement(accountTable.updatebalance);
			statement.setDouble(1, amount);
			statement.setString(2, uuid);
			int rowsAffected = 0;
			rowsAffected = statement.executeUpdate();
			statement.close();
			connection.close();
			if (rowsAffected > 0) {
				return true;
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
		}
		return false;
	}

	public static void saveLog(String player, LogInfo info, org.spongepowered.api.event.cause.Cause cause,
			DateTime dateTime, double amount) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Timestamp timeStamp = new Timestamp(formatter.parseDateTime(dateTime.toString()).getMillis());

		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = null;
			statement = connection.prepareStatement(MySQLEngine.logTable.insertEntry);
			statement.setString(1, player);
			statement.setString(2, info.toString());
			statement.setTimestamp(3, timeStamp);
			statement.setDouble(4, amount);
			statement.executeQuery();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
			e.printStackTrace();
		}

	}

	public static void createaccount(String uuid, String name, Double balance) {
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = null;
			statement = connection.prepareStatement(accountTable.inserNewPlayer);
			statement.setString(1, uuid);
			statement.setString(2, name);
			statement.setDouble(3, balance);
			statement.executeQuery();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
			e.printStackTrace();
		}
	}

	public static boolean setInfiniteMoney(String uuid, boolean infinite) {
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = null;
			statement = connection.prepareStatement(accountTable.updateInfinitemoneyEntryUUID);
			statement.setBoolean(1, infinite);
			;
			statement.setString(2, uuid);
			statement.executeQuery();
			statement.close();
			connection.close();
			return true;
		} catch (SQLException e) {
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
			e.printStackTrace();
			return false;
		}

	}

	public static void updateUsername(String name, String uuid) {
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = null;
			statement = connection.prepareStatement(MySQLEngine.accountTable.updateNameByUuid);
			statement.setString(1, name);
			statement.setString(2, uuid);
			statement.executeUpdate();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
			e.printStackTrace();
		}

	}

	public static ResultSet getLog(String querry) {
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = null;
			statement = connection.prepareStatement(querry);
			ResultSet set = statement.executeQuery();
			connection.close();
			return set;

		} catch (SQLException e) {
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
			e.printStackTrace();
		}
		return null;

	}

	public static void Balancetop(CommandSource src) {
		int place = 1;
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = null;
			statement = connection.prepareStatement(MySQLEngine.accountTable.top);
			ResultSet set = statement.executeQuery();
			while (set.next()) {
				String user = set.getString("name");
				double total = set.getDouble("balance");
				String message = String.valueOf(place) + " : " + user + " -> " + String.valueOf(total);
				Main.sendmessage(message, src.getName());
				place++;
			}
			statement.close();
			connection.close();
		} catch (SQLException e) {
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
			e.printStackTrace();
		}
	}

	public static void clearLog(DateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Timestamp timeStamp = new Timestamp(formatter.parseDateTime(dateTime.toString()).getMillis());

		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = null;
			statement = connection.prepareStatement(MySQLEngine.logTable.cleanEntry);
			statement.setTimestamp(1, timeStamp);
			statement.executeQuery();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
			e.printStackTrace();
		}
		
	}

	

}
