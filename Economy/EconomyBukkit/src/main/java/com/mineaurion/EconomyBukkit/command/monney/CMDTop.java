package com.mineaurion.EconomyBukkit.command.monney;

import org.bukkit.command.CommandSender;

import com.mineaurion.EconomyBukkit.Mysql.MySQLEngine;



public class CMDTop {

	public CMDTop(CommandSender sender, String[] args) {
		MySQLEngine.Balancetop(sender);
		
	}

}
