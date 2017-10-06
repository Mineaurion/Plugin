package com.mineaurion.EconomyBukkit.command.admin;

import org.bukkit.command.CommandSender;

import com.mineaurion.EconomyBukkit.Main;

public class CMDReload {

	public CMDReload(CommandSender sender, String[] args) {
		Main.sendmessage("{{GREEN}}Reload du plugin", sender.getName());
		Main.getInstance().load(sender.getName());
	}
}
