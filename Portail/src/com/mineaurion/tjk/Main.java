package com.mineaurion.tjk;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	public static int holding = 6905;
	public static List<String> DIMBLacklist = Arrays.asList("DIM3","DIM1","DIM4");
	
	
	@Override
	public void onEnable(){
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPortalEnter(PlayerPortalEvent event){
		if(event.getPlayer().getItemInHand().getTypeId()==holding){
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "m_chat " + event.getPlayer().getName()+" &4[&6Portal-Bot&4] &cWelcome in Spectre World");
		}
		else if(!DIMBLacklist.contains(event.getFrom().getWorld().getName())){
			event.setCancelled(true);
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "m_chat " + event.getPlayer().getName()+" &4[&6Portal-Bot&4] &cUtilisez le /warp pour acceder aux differents mondes");
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "m_chat " + event.getPlayer().getName()+" &4[&6Portal-Bot&4] &cUse /warp to access to all worlds");
		}
	}
}
