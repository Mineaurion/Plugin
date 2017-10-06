package com.mineaurion.EconomyBukkit.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class Tabcomplete implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if (cmd.getName().equalsIgnoreCase("money")) {
			List<String> validplayer = Arrays.asList("balance", "top");
			List<String> validadmin = Arrays.asList("balance", "give", "infinite", "log", "set", "take", "top");

			ArrayList<String> SubCommand = new ArrayList<String>();

			if (args.length == 1) {

				if (sender instanceof Player) {

					if (!args[0].equals("")) {

						if (((Player) sender).hasPermission("economie.money.admin")) {
							for (String st : validadmin) {
								if (st.toLowerCase().startsWith(args[0].toLowerCase())) {
									SubCommand.add(st);
								}
							}
						} else {
							for (String st : validplayer) {
								if (st.toLowerCase().startsWith(args[0].toLowerCase())) {
									SubCommand.add(st);
								}
							}
						}
					} else {
						if (((Player) sender).hasPermission("economie.money.admin")) {
							for (String st : validadmin) {
								SubCommand.add(st);
							}
						} else {
							for (String st : validplayer) {
								SubCommand.add(st);
							}
						}
					}
					Collections.sort(SubCommand);
					return SubCommand;

				} else {
					if (!args[0].equals("")) {
						for (String st : validadmin) {
							if (st.toLowerCase().startsWith(args[0].toLowerCase())) {
								SubCommand.add(st);
							}
						}
					} else {
						for (String st : validadmin) {
							SubCommand.add(st);
						}
					}
					Collections.sort(SubCommand);
					return SubCommand;
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("economieadmin")) {

			List<String> validadmin = Arrays.asList("reload", "check");

			ArrayList<String> SubCommand = new ArrayList<String>();

			if (args.length == 1) {

				if (sender instanceof Player) {

					if (!args[0].equals("")) {

						if (((Player) sender).hasPermission("economie.money.admin")) {
							for (String st : validadmin) {
								if (st.toLowerCase().startsWith(args[0].toLowerCase())) {
									SubCommand.add(st);
								}
							}
						}
					} else {
						if (((Player) sender).hasPermission("economie.money.admin")) {
							for (String st : validadmin) {
								SubCommand.add(st);
							}

						}
					}
					Collections.sort(SubCommand);
					return SubCommand;

				} else {
					if (!args[0].equals("")) {
						for (String st : validadmin) {
							if (st.toLowerCase().startsWith(args[0].toLowerCase())) {
								SubCommand.add(st);
							}
						}
					} else {
						for (String st : validadmin) {
							SubCommand.add(st);
						}
					}
					Collections.sort(SubCommand);
					return SubCommand;
				}
			}
		}
		return null;
	}
}
