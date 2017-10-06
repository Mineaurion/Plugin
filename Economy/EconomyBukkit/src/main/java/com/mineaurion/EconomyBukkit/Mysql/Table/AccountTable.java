package com.mineaurion.EconomyBukkit.Mysql.Table;

public class AccountTable extends DatabaseTable {
	public static final String TABLE_NAME = "account";

	public final String createTableMySQL = "CREATE TABLE IF NOT EXISTS `" + getPrefix() + "account` ( "
			+ "`id` INT NOT NULL AUTO_INCREMENT , "
			+ "`uuid` VARCHAR(50) NOT NULL , "
			+ "`name` VARCHAR(100) NOT NULL , "
			+ "`balance` DOUBLE DEFAULT NULL , "
			+ "`infiniMoney` BOOLEAN NOT NULL DEFAULT FALSE , "
			+ "PRIMARY KEY (`id`), UNIQUE (`uuid`)"
			+ ") ENGINE = InnoDB CHARSET=utf8;";

	public final String selectEntrywithName = "SELECT * FROM " + getPrefix() + "account" + " WHERE name=?";

	public final String selectEntryUUID = "SELECT * FROM " + getPrefix() + "account" + " WHERE uuid=?";
	
	public final String selectBalance = "SELECT `balance` FROM " + getPrefix() + "account" + " WHERE uuid=?";
	
	public final String selectinfini = "SELECT `infiniMoney` FROM " + getPrefix() + "account" + " WHERE uuid=?";
	
	public final String inserNewPlayer = "INSERT INTO " + getPrefix() + "account" + "(uuid,name,balance) VALUES(?,?,?)";

	public final String insertEntryAllInfo = "INSERT INTO " + getPrefix() + "account"
			+ "(uuid,name,balance,infiniMoney) VALUES(?,?,?,?)";

	public final String updateInfinitemoneyEntryUUID = "UPDATE " + getPrefix() + "account"
			+ " SET infiniMoney=? WHERE uuid=?";

	public final String updatebalance = "UPDATE " + getPrefix() + "account" + " SET balance=? WHERE uuid=?";

	public final String updateNameByUuid = "UPDATE " + getPrefix() + "account" + " SET name=? WHERE uuid=?";

	public final String updateUuidByName = " UPDATE " + getPrefix() + "account" + " SET uuid=? WHERE name=?";
	
	public final String deleteEntry = "DELETE FROM " + getPrefix() + "account" + " WHERE uuid=?";
	
	public final String top = "SELECT * FROM " + getPrefix() + "account" + " WHERE infiniMoney=false ORDER BY balance DESC LIMIT 10";
	
	public AccountTable(String prefix) {
		super(prefix);
	}
}