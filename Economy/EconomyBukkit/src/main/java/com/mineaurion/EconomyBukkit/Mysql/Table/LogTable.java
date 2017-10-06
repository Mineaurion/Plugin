package com.mineaurion.EconomyBukkit.Mysql.Table;

public class LogTable extends DatabaseTable {
	public static final String TABLE_NAME = "log";

	public final String createTableMySQL = "CREATE TABLE IF NOT EXISTS `" + getPrefix() 
			+ "log` ( `ID` INT(11) NOT NULL AUTO_INCREMENT , "
			+ "`player` VARCHAR(100) NOT NULL , "
			+ "`type` VARCHAR(30) NOT NULL , "
			+ "`time` DATETIME NOT NULL , "
			+ "`amount` DOUBLE NULL DEFAULT NULL , "
			+ "PRIMARY KEY (`id`)) ENGINE = InnoDB CHARSET=utf8;";

	
	public final String insertEntry = "INSERT INTO " + getPrefix() + "log"
			+ "(player,type,time,amount)VALUES(?,?,?,?)";

	public final String selectEntry = "SELECT * FROM `" + getPrefix() + "log` ";

	public final String cleanEntry = "DELETE FROM " + getPrefix() + "log" + " WHERE TIME <= ?";

	public LogTable(String prefix) {
		super(prefix);
	}
}