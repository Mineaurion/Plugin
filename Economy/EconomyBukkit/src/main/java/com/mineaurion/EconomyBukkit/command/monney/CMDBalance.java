package com.mineaurion.EconomyBukkit.command.monney;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mineaurion.EconomyBukkit.Main;
import com.mineaurion.EconomyBukkit.Mysql.MySQLEngine;

public class CMDBalance {

	public CMDBalance(CommandSender sender, String[] args) {
		if(args.length==2){
			
			String Playerbal = Main.getInstance().format(MySQLEngine.getBalanceName(args[1], false));
			Main.sendmessage("Balance de {{CYAN}}" + args[1] + "{{WHITE}}: " + Playerbal,
					sender.getName());
		}else{
			if(sender instanceof Player){
				Player player = (Player) sender;
				String Playerbal = Main.getInstance().format(MySQLEngine.getBalance(player.getUniqueId().toString(), false));
				Main.sendmessage("Balance de {{CYAN}}" + player.getName() + "{{WHITE}}: " + Playerbal,
						sender.getName());
			}else{
				Main.sendmessage("La console na pas de monney, utilise /money balance <Player>", "console");
			}
		}
	}

}
