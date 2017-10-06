package com.mineaurion.EconomyBukkit.command.monney;

import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mineaurion.EconomyBukkit.Main;

public class CommandMoney implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("money")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (args.length == 0) {
					if (player.hasPermission("economie.money.player")) {
						new CMDBalance(sender, args);
					} else {
						noPermission(sender);
					}
					return true;
				}
				String subCommand = args[0].toUpperCase();
				switch (subCommand) {

				case "BALANCE":
					if (player.hasPermission("economie.money.player")) {
						new CMDBalance(sender, args);
					} else {
						noPermission(sender);
					}
					return true;

				case "GIVE":
					if (player.hasPermission("economie.money.admin")) {
						new CMDGive(sender, args);
					} else {
						noPermission(sender);
					}
					return true;

				case "INFINITE":
					if (player.hasPermission("economie.money.admin")) {
						new CMDInfinite(sender, args);
					} else {
						noPermission(sender);
					}
					return true;
				case "LOG":
					if (player.hasPermission("economie.money.admin")) {
						try {
							new CMDLog(sender, args);
						} catch (NumberFormatException | CommandException | SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						noPermission(sender);
					}
					return true;
				case "SET":
					if (player.hasPermission("economie.money.admin")) {
						new CMDSet(sender, args);
					} else {
						noPermission(sender);
					}
					return true;
				case "TAKE":
					if (player.hasPermission("economie.money.admin")) {
						new CMDTake(sender, args);
					} else {
						noPermission(sender);
					}
					return true;
				case "TOP":

					if (player.hasPermission("economie.money.player")) {
						new CMDTop(sender, args);
					} else {
						noPermission(sender);
					}
					return true;
				case "HELP":
					if (player.hasPermission("economie.money.player")) {
						new CMDHelp(sender, args);
					} else {
						noPermission(sender);
					}
					return true;
				}
			} else {
				if (args[0].length() == 0) {
					Main.sendmessage("Use /money balance|give|infinite|log|set|take|top", sender.getName());
					return true;
				}
				String subCommand = args[0].toUpperCase();
				switch (subCommand) {

				case "BALANCE":
					new CMDBalance(sender, args);
					return true;
				case "GIVE":
					new CMDGive(sender, args);
					return true;
				case "INFINITE":
					new CMDInfinite(sender, args);
					return true;
				case "LOG":
					try {
						new CMDLog(sender, args);
					} catch (NumberFormatException | CommandException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				case "SET":
					new CMDSet(sender, args);
					return true;
				case "TAKE":
					new CMDTake(sender, args);
					return true;
				case "TOP":
					new CMDTop(sender, args);
					return true;
				case "HELP":
					new CMDHelp(sender, args);
					return true;
				}

			}
			return false;

		}
		return false;
	}

	private void noPermission(CommandSender sender) {
		Main.sendmessage("{{RED}}You don't have permission", sender.getName());

	}

}
