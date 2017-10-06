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

public class CMDGive {

	OfflinePlayer playeroff;
	Player player;
	String Pseudo;
	UUID uuid;
	@SuppressWarnings("deprecation")
	public CMDGive(CommandSender sender, String[] args) {
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
			if (m.matches() && Double.parseDouble(amount) > 0) {
				String Playerbal = Main.getInstance().format(Double.parseDouble(amount));

				double balance = Main.getInstance().getVaultconnector().getBalance(Pseudo);
				if(balance== Double.MAX_VALUE){
					Main.sendmessage("{{RED}}le joueurs a le mode infini d'activé", sender.getName());
					return;
				}
				//double newbalance = balance + Double.parseDouble(amount);
				//boolean result = MySQLEngine.setBalance(uuid.toString(), newbalance);
				EconomyResponse result =Main.getInstance().getVaultconnector().depositPlayer(Pseudo, Double.parseDouble(amount));
				if (result.transactionSuccess()) {
					DateTime dateTime = DateTime.now(DateTimeZone.forID("Europe/Paris"));
					Main.getInstance().writeLog(Pseudo, LogInfo.GIVE,
							Cause.VAULT, dateTime, Double.parseDouble(amount));
					Main.sendmessage("Tu as donné {{RED}}" + Playerbal + "{{WHITE}} à {{YELLOW}}" + Pseudo,
							sender.getName());
					if (player!=null) {
						Main.sendmessage("Tu as reçu {{RED}}" + Playerbal + "", player.getName());
					}
				} else {
					Main.sendmessage("{{RED}}une erreur est survenu", sender.getName());
				}
			} else {
				Main.sendmessage("{{RED}}Montant invalible ou <0", sender.getName());
			}
		} else {
			Main.sendmessage("{{RED}}Use /money give <player> <montant>", sender.getName());
		}
	}

}
