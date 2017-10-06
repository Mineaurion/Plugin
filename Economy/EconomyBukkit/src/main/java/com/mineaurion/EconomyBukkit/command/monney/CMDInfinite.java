package com.mineaurion.EconomyBukkit.command.monney;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.mineaurion.EconomyBukkit.Main;
import com.mineaurion.EconomyBukkit.Mysql.MySQLEngine;

public class CMDInfinite {
	OfflinePlayer playeroff;
	Player player;
	String Pseudo;
	UUID uuid;
	@SuppressWarnings("deprecation")
	public CMDInfinite(CommandSender sender, String[] args) {
		if(args.length==3){
			if(args[2].equalsIgnoreCase("true")||args[2].equalsIgnoreCase("false")){
				boolean bool = Boolean.valueOf(args[2]);
				
				player = Bukkit.getPlayerExact(args[1]);
				if(player == null){
					playeroff = Bukkit.getOfflinePlayer(args[1]);
					Pseudo = playeroff.getName();
					uuid = playeroff.getUniqueId();
				}else{
					Pseudo = player.getName();
					uuid = player.getUniqueId();
				}
				if(MySQLEngine.setInfiniteMoney(uuid.toString(), bool)){
					Main.sendmessage("Infiny monney pour "+Pseudo+" : {{RED}}"+String.valueOf(bool), sender.getName());
				}else{
					Main.sendmessage("une erreur est survenu check la console", sender.getName());	
				}
			}else{
				Main.sendmessage("{{RED}}Use /money infinite <player> <true|false>", sender.getName());
			}
		}else{
			Main.sendmessage("{{RED}}Use /money infinite <player> <true|false>", sender.getName());
		}
	}

}
