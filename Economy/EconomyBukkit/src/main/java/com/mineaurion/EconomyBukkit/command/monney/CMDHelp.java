package com.mineaurion.EconomyBukkit.command.monney;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mineaurion.EconomyBukkit.Main;

public class CMDHelp {
	public CMDHelp(CommandSender sender, String[] args) {
		if(sender instanceof Player)
		{
			if(sender.hasPermission("economie.money.player"))
			{
				Main.sendmessage("/money -> donne la balance du joueur qui a executer la commande",
					sender.getName());
				Main.sendmessage("/money balance <player> -> donne la balance du joueur en parametre",
						sender.getName());
				Main.sendmessage("/money help -> affiche l'aide",
						sender.getName());
				
				Main.sendmessage("/money top -> affiche le top 10",
						sender.getName());
			}
			if(sender.hasPermission("economie.money.admin"))
			{
				Main.sendmessage("/money give/take/set <player> <montant> ->Bas donne retire ou defini le montant du joueur",
						sender.getName());
				Main.sendmessage("/economieadmin reload -> permet de recharger le plugin",
						sender.getName());
				Main.sendmessage("/economieadmin check <player> <montant> <commande> -> execute la/les commande si le joueurs a l'argen ex:/economieadmin check Yann151924 100 /say GG/say CC",
						sender.getName());
			}
			
		}else{
			Main.sendmessage("/money -> donne la balance du joueur qui a executer la commande",
					sender.getName());
				Main.sendmessage("/money balance <player> -> donne la balance du joueur en parametre",
						sender.getName());
				Main.sendmessage("/money help -> affiche l'aide",
						sender.getName());
				
				Main.sendmessage("/money top -> affiche le top 10",
						sender.getName());
				Main.sendmessage("/money give/take/set <player> <montant> ->Bas donne retire ou defini le montant du joueur",
						sender.getName());
				Main.sendmessage("/economieadmin reload -> permet de recharger le plugin",
						sender.getName());
				Main.sendmessage("/economieadmin check <player> <montant> <commande> -> execute la/les commande si le joueurs a l'argen ex:/economieadmin check Yann151924 100 /say GG/say CC",
						sender.getName());
		}
	}
}
