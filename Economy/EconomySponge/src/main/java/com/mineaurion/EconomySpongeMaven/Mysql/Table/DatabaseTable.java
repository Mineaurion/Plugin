package com.mineaurion.EconomySpongeMaven.Mysql.Table;

public class DatabaseTable {
	private String prefix;

	public DatabaseTable(String prefix) {
		this.prefix = prefix;
	}

	protected String getPrefix() {
		return this.prefix;
	}
}
