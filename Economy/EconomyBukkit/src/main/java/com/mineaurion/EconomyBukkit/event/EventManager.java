package com.mineaurion.EconomyBukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import com.mineaurion.EconomyBukkit.Main;
import com.mineaurion.EconomyBukkit.Mysql.MySQLEngine;

public class EventManager implements Listener {

	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent event){
		if(!Main.getInstance().getConfig().getBoolean("Setup")&&event.getPlayer().isOp()){
			Main.sendmessage(
					"{{DARK_RED}}Configure le plugin (pense a changer le Setup en true) puis fait /economieadmin reload",
					event.getPlayer().getName());
		}
		
		if(Main.getInstance().getConfig().getBoolean("Setup")){
			Player player = event.getPlayer();
			if (!MySQLEngine.accountExist(player.getUniqueId().toString())) {
				if(MySQLEngine.NameExist(player.getName(),player.getUniqueId().toString())){
					Main.sendmessage(
							"{{DARK_RED}}UUID"+player.getUniqueId().toString()+" add for"+player.getName(),
							"console");
				}else{
				MySQLEngine.createaccount(player.getUniqueId().toString(), player.getName(),
						Main.getInstance().getHoldings());
				}
			} 			
		}
	}
	
}
