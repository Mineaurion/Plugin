package com.mineaurion.EconomySpongeMaven.event;

import java.math.BigDecimal;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import com.mineaurion.EconomySpongeMaven.Main;
import com.mineaurion.EconomySpongeMaven.Mysql.MySQLEngine;

public class EventManager {

	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
		Main.getInstance();
		if (!Main.rootNode.getNode("Setup").getBoolean() && event.getTargetEntity().hasPermission("*")) {
			Main.getInstance();
			Main.sendmessage(
					"{{DARK_RED}}Configure le plugin (pense a changer le Setup en true) puis fait /economieadmin reload",
					event.getTargetEntity().getName());
		}

		if (Main.rootNode.getNode("Setup").getBoolean()) {
			Player player = event.getTargetEntity();
			if (!MySQLEngine.accountExist(player.getUniqueId().toString())) {
				if(MySQLEngine.NameExist(player.getName(),player.getUniqueId().toString())){
					Main.sendmessage(
							"{{DARK_RED}}UUID"+player.getUniqueId().toString()+" add for"+player.getName(),
							"console");
				}else{
				MySQLEngine.createaccount(player.getUniqueId().toString(), player.getName(),
						Main.getInstance().getHoldings());
				}
			} else {
				MySQLEngine.updateUsername(player.getName(), player.getUniqueId().toString());
				Main.getInstance().getAccountManager().getOrCreateAccount(player.getUniqueId()).get().setBalance(
						Main.getInstance().getDefaultCurrency(),
						new BigDecimal(MySQLEngine.getBalance(player.getUniqueId().toString(),true)),
						Cause.of(EventContext.empty(),Main.getInstance().getPlugin()));
			}
		}

	}
}
