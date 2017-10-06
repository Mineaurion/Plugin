package com.mineaurion.EconomySpongeMaven.commands.money;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import com.mineaurion.EconomySpongeMaven.Mysql.MySQLEngine;

public class CMDtop implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		MySQLEngine.Balancetop(src);
		return CommandResult.success();
	}

}
