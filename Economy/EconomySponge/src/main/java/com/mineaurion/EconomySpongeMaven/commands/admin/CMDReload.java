package com.mineaurion.EconomySpongeMaven.commands.admin;

import java.io.IOException;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import com.mineaurion.EconomySpongeMaven.Main;

public class CMDReload implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Main.sendmessage("{{GREEN}}Reload du plugin", src.getName());	
		try {
			Main.getInstance().load(src.getName());
		} catch (IOException e) {
			Main.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
		}
		return CommandResult.success();
	}

}
