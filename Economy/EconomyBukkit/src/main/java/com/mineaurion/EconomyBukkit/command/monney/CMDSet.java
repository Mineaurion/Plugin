package com.mineaurion.EconomyBukkit.command.monney;

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
import net.milkbowl.vault.economy.EconomyResponse;

public class CMDSet {
	OfflinePlayer playeroff;
	Player player;
	String Pseudo;
	UUID uuid;
	@SuppressWarnings("deprecation")
	public CMDSet(CommandSender sender, String[] args) {
		if (args.length == 3) {
			
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
			if (m.matches() && Double.parseDouble(amount) >= 0) {
				String Playerbal = Main.getInstance().format(Double.parseDouble(amount));

				EconomyResponse result = Main.getInstance().getVaultconnector().withdrawPlayer(Pseudo, Main.getInstance().getVaultconnector().getBalance(Pseudo));
				EconomyResponse result1 = Main.getInstance().getVaultconnector().depositPlayer(Pseudo, Double.parseDouble(amount));

				//boolean result = MySQLEngine.setBalance(uuid.toString(), Double.parseDouble(amount));
				if (result.transactionSuccess()&&result1.transactionSuccess()) {
					DateTime dateTime = DateTime.now(DateTimeZone.forID("Europe/Paris"));
					Main.getInstance().writeLog(Pseudo, LogInfo.SET,
							Cause.VAULT, dateTime, Double.parseDouble(amount));
					Main.sendmessage("Balance set pour " + Pseudo + " à " + Playerbal, sender.getName());
				} else {
					Main.sendmessage("{{RED}}une erreur est survenu", sender.getName());
				}
			} else {
				Main.sendmessage("{{RED}}Montant invalible ou <0", sender.getName());
			}
		} else {
			Main.sendmessage("{{RED}}Use /money set <player> <montant>", sender.getName());
		}
	}

}
