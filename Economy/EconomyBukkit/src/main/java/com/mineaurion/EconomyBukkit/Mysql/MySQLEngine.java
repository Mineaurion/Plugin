package com.mineaurion.EconomyBukkit.Mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.bukkit.command.CommandSender;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.mineaurion.EconomyBukkit.Cause;
import com.mineaurion.EconomyBukkit.LogInfo;
import com.mineaurion.EconomyBukkit.Main;
import com.mineaurion.EconomyBukkit.Mysql.Table.AccountTable;
import com.mineaurion.EconomyBukkit.Mysql.Table.LogTable;

public class MySQLEngine {
	public String address;
	public static String port;
	public static String databaseName;
	public static String user;
	public static String password;
	public static String prefix;
	private static Connection connection;

	public static AccountTable accountTable;
	public static LogTable logTable;

	public MySQLEngine(Main main) throws ClassNotFoundException, SQLException {
		address = main.config.getString("Database.Address");
		port = main.config.getString("Database.Port");
		databaseName = main.config.getString("Database.Db");
		user = main.config.getString("Database.Username");
		password = main.config.getString("Database.Password");
		prefix = main.config.getString("Database.Prefix");

		
		if (connection != null && !connection.isClosed()) {
	        return;
	    }
		
		synchronized (main) {
	        if (connection != null && !connection.isClosed()) {
	            return;
	        } 
	        Class.forName("com.mysql.jdbc.Driver");
	        connection = DriverManager.getConnection("jdbc:mysql://" + address+ ":" + port + "/" + databaseName, user, password);
	    }


		accountTable = new AccountTable(prefix);
		logTable = new LogTable(prefix);
        setupDatabase();

	}

	
	public void setupDatabase() {
		try {
			connection.prepareStatement(accountTable.createTableMySQL).execute();
			connection.prepareStatement(logTable.createTableMySQL).execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
			Main.sendmessage("{{DARK_RED}}Error creating table source!", "console");
		}
	}

	public static boolean accountExist(String uuid) {
		try {
			PreparedStatement statement = null;
			statement = connection.prepareStatement(accountTable.selectEntryUUID);
			statement.setString(1, uuid);
			ResultSet set = statement.executeQuery();
			if (set.isBeforeFirst()) {
				statement.close();
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
			PreparedStatement statement = null;
			statement = connection.prepareStatement(accountTable.selectEntrywithName);
			statement.setString(1, name);
			ResultSet set = statement.executeQuery();
			if (set.isBeforeFirst()) {
				
				PreparedStatement statementName = null;
				statementName = connection.prepareStatement(MySQLEngine.accountTable.updateUuidByName);
				statementName.setString(1, uuid);
				statementName.setString(2, name);
				statementName.executeUpdate();
				statementName.close();
				statement.close();
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
			PreparedStatement statement = null;
			statement = connection.prepareStatement(accountTable.selectEntrywithName);
			statement.setString(1, name);
			ResultSet set = statement.executeQuery();
			if (set.next()) {
				if (!set.getBoolean("infiniMoney")) {
					double balance = set.getDouble("balance");
					statement.close();
					
					return balance;
				} else {
					if(conection){
						double balance = set.getDouble("balance");
						statement.close();
						
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
			PreparedStatement statement = null;
			statement = connection.prepareStatement(accountTable.selectEntryUUID);
			statement.setString(1, uuid);
			ResultSet set = statement.executeQuery();
			if (set.next()) {
				if (!set.getBoolean("infiniMoney")) {
					double balance = set.getDouble("balance");
					statement.close();
					
					return balance;
				} else {
					if(conection){
						double balance = set.getDouble("balance");
						statement.close();
						
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
			
			PreparedStatement statement = null;
			statement = connection.prepareStatement(accountTable.updatebalance);
			statement.setDouble(1, amount);
			statement.setString(2, uuid);
			int rowsAffected = 0;
			rowsAffected = statement.executeUpdate();
			statement.close();
			
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

	public static void saveLog(String player, LogInfo info, Cause cause,
			DateTime dateTime, double amount) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Timestamp timeStamp = new Timestamp(formatter.parseDateTime(dateTime.toString()).getMillis());

		try {
			
			PreparedStatement statement = null;
			statement = connection.prepareStatement(MySQLEngine.logTable.insertEntry);
			statement.setString(1, player);
			statement.setString(2, info.toString());
			statement.setTimestamp(3, timeStamp);
			statement.setDouble(4, amount);
			statement.executeUpdate();
			statement.close();
			
		} catch (SQLException e) {
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
			e.printStackTrace();
		}

	}

	public static void createaccount(String uuid, String name, Double balance) {
		try {
			
			PreparedStatement statement = null;
			statement = connection.prepareStatement(accountTable.inserNewPlayer);
			statement.setString(1, uuid);
			statement.setString(2, name);
			statement.setDouble(3, balance);
			statement.executeUpdate();
			statement.close();
			
		} catch (SQLException e) {
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
			e.printStackTrace();
		}
	}

	public static boolean setInfiniteMoney(String uuid, boolean infinite) {
		try {
			
			PreparedStatement statement = null;
			statement = connection.prepareStatement(accountTable.updateInfinitemoneyEntryUUID);
			statement.setBoolean(1, infinite);
			;
			statement.setString(2, uuid);
			statement.executeUpdate();
			statement.close();
			
			return true;
		} catch (SQLException e) {
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
			e.printStackTrace();
			return false;
		}

	}

	public static void updateUsername(String name, String uuid) {
		try {
			
			PreparedStatement statement = null;
			statement = connection.prepareStatement(MySQLEngine.accountTable.updateNameByUuid);
			statement.setString(1, name);
			statement.setString(2, uuid.toString());
			statement.executeUpdate();
			statement.close();
			
		} catch (SQLException e) {
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
			e.printStackTrace();
		}

	}

	public static ResultSet getLog(String querry) {
		try {
			
			PreparedStatement statement = null;
			statement = connection.prepareStatement(querry);
			ResultSet set = statement.executeQuery();			
			return set;

		} catch (SQLException e) {
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
			e.printStackTrace();
		}
		return null;

	}

	public static void Balancetop(CommandSender src) {
		int place = 1;
		try {
			
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
			
		} catch (SQLException e) {
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
			e.printStackTrace();
		}
	}

	public static void clearLog(DateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Timestamp timeStamp = new Timestamp(formatter.parseDateTime(dateTime.toString()).getMillis());

		try {
			
			PreparedStatement statement = null;
			statement = connection.prepareStatement(MySQLEngine.logTable.cleanEntry);
			statement.setTimestamp(1, timeStamp);
			statement.executeUpdate();
			statement.close();
			
		} catch (SQLException e) {
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
			e.printStackTrace();
		}
		
	}

}