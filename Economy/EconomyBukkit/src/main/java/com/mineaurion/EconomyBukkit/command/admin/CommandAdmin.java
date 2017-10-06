package com.mineaurion.EconomyBukkit.command.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mineaurion.EconomyBukkit.Main;

public class CommandAdmin implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if (cmd.getName().equalsIgnoreCase("economieadmin")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if(!player.hasPermission("economie.money.admin")){
					Main.sendmessage("{{RED}}You don't have permission", sender.getName());
					return true;
				}
				if(args.length==0){
					Main.sendmessage("{{RED}}/economieadmin reload|check", sender.getName());
					return true;
				}
				
				String subCommand = args[0].toUpperCase();
				switch (subCommand) {
				case "RELOAD":
					new CMDReload(sender, args);
					return true;
				case "CHECK":
					new CMDCheck(sender, args);
					return true;
				}
			}else{
				if(args.length==0){
					Main.sendmessage("{{RED}}/economieadmin reload|check", sender.getName());
					return true;
				}
				
				String subCommand = args[0].toUpperCase();
				switch (subCommand) {
				case "RELOAD":
					new CMDReload(sender, args);
					return true;
				case "CHECK":
					new CMDCheck(sender, args);
					return true;
				}
				
				
				
			}
		}
		return false;
	}

}
