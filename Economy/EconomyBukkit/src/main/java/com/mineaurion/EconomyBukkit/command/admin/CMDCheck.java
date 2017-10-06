package com.mineaurion.EconomyBukkit.command.admin;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.mineaurion.EconomyBukkit.Cause;
import com.mineaurion.EconomyBukkit.LogInfo;
import com.mineaurion.EconomyBukkit.Main;
import com.mineaurion.EconomyBukkit.Mysql.MySQLEngine;

import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class CMDCheck {
	OfflinePlayer playeroff;
	Player player;
	String Pseudo;
	UUID uuid;
	@SuppressWarnings("deprecation")
	public CMDCheck(CommandSender sender, String[] args) {
		if (args.length < 4) {
			Main.sendmessage("{{RED}}Use /economieadmin check <player> <montant> <command>", sender.getName());
			return;
		}
		
		player = Bukkit.getPlayerExact(args[1]);
		if(player == null){
			playeroff = Bukkit.getOfflinePlayer(args[1]);
			Pseudo = playeroff.getName();
			uuid = playeroff.getUniqueId();
		}else{
			Pseudo = player.getName();
			uuid = player.getUniqueId();
		}
		String amount = args[2];
		Pattern amountPattern = Pattern.compile("^[+]?(\\d*\\.)?\\d+$");
		Matcher m = amountPattern.matcher(amount);
		if (m.matches() && Double.parseDouble(amount) > 0) {
			String Playerbal = Main.getInstance().format(Double.parseDouble(amount));
			double balance = MySQLEngine.getBalance(uuid.toString(),false);
			if (balance >= Double.parseDouble(amount)) {
				
				String commands = "";
				int i =3;
				while(i<args.length){
					commands = commands+" "+args[i];
					i++;
				}
				
				
				if(commands.isEmpty()){
					Main.sendmessage("{{RED}}insert une commande  executer", sender.getName());
					return;
				}
				EconomyResponse result;
				//double newbalance = balance - Double.parseDouble(amount);
				if(balance== Double.MAX_VALUE){
					Main.sendmessage("{{RED}}le joueurs a le mode infini d'activé pas d'argent retiré", sender.getName());
					result = new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
				}else{
					
					 result =Main.getInstance().getVaultconnector().withdrawPlayer(Pseudo, Double.parseDouble(amount));
				}
				if (result.transactionSuccess()) {
					
					String[] parts = commands.split("/");
					for (i = 0; i < parts.length; i++) {
						String command = parts[i];
						Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("<username>", Pseudo));
					}
					
					DateTime dateTime = DateTime.now(DateTimeZone.forID("Europe/Paris"));
					Main.getInstance().writeLog(Pseudo, LogInfo.CHECK, Cause.VAULT, dateTime,
							Double.parseDouble(amount));
					Main.sendmessage("Tu as retiré {{RED}}" + Playerbal + "{{WHITE}} à {{YELLOW}}" + Pseudo,
							sender.getName());
					if (player!=null) {
						Main.sendmessage("Tu as dépensé {{RED}}" + Playerbal + "", player.getName());
					}
				} else {
					Main.sendmessage("{{RED}}une erreur est survenu", sender.getName());
				}
			} else {
				Main.sendmessage("{{RED}}You don't have monney", sender.getName());
			}

		} else {
			Main.sendmessage("{{RED}}Montant invalible ou <0", sender.getName());
		}
	}

}
