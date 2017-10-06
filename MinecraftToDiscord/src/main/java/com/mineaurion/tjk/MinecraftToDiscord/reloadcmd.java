package com.mineaurion.tjk.MinecraftToDiscord;

import java.io.IOException;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class reloadcmd implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		try {
			Sponges.getInstance().setup(src.getName());
			
		} catch (IOException e) {
			Sponges.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
		}
		return CommandResult.success();
	}

}
