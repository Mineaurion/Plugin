package com.mineaurion.tjk.MinecraftToDiscord;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.mrpowergamerbr.temmiewebhook.DiscordMessage;
import com.mrpowergamerbr.temmiewebhook.TemmieWebhook;

public class Thermos extends JavaPlugin implements Listener {
	public TemmieWebhook temmie;
	public boolean usePlaceholderApi;
	
	public void onEnable() {
		saveDefaultConfig();
		
		setup();
	}
	
	public void setup() {
		

		if (getConfig().getString("link").equals("NONE")) {
			getLogger().info("Hey, is this your first time using MinecraftToDiscord?");
			getLogger().info("If yes, then you need to add your Webhook URL to the");
			getLogger().info("config.yml!");
			getLogger().info("");
			getLogger().info("...so go there and do that.");
			getLogger().info("After doing that, use /discordreload");
			return;
		}
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && getConfig().getBoolean("UsePlaceholderAPI")) {
			usePlaceholderApi = true;
		}
		temmie = new TemmieWebhook(getConfig().getString("link"));
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("discordreload")) {
			if (sender.hasPermission("MinecraftToDiscord")) {
				reloadConfig();
				setup();
				sender.sendMessage("§aMinecraftToDiscord reloaded!");
			} else {
				sender.sendMessage("§cNo permission!");
			}
			return true;

		}
		else if (cmd.getName().equalsIgnoreCase("webhook")) {
			if (sender.hasPermission("MinecraftToDiscord")) {
				if(args.length==0){
					sender.sendMessage("§cuse /webhook (ton message)!");
				}else{
					String message = "";
					int i =0;
					while(i<args.length){
						message = message+" "+args[i];
						i++;
					}
					message = ChatColor.translateAlternateColorCodes('&', message);
					message = ChatColor.stripColor(message);
					message.replace("<player>", sender.getName());
					
					DiscordMessage dm = DiscordMessage.builder()
							.username("Minecraft Request") // Player's name
							.content(message) // Player's message
							.avatarUrl("https://www.residentadvisor.net/images/reviews/2012/specialrequest-ep1ep2.jpg") // Avatar
							.build();
					
					this.temmie.sendMessage(dm);
				}
			} else {
				sender.sendMessage("§cNo permission!");
			}
			return true;
		}
		return false;
	}

}


