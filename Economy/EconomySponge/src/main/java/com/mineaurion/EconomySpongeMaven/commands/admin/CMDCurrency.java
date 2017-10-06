package com.mineaurion.EconomySpongeMaven.commands.admin;

import java.io.IOException;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import com.mineaurion.EconomySpongeMaven.Main;

public class CMDCurrency implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String arg = args.<String>getOne("option").get();
		String config = args.<String>getOne("config").get();
		try {
			switch (arg) {
			case "minor":
				Main.getRootNode().getNode("QuickSetup", "Currency", "Minor").setValue(config);
				Main.getInstance().getLoader().save(Main.getRootNode());
				break;
			case "name":
				Main.getRootNode().getNode("QuickSetup", "Currency", "Name").setValue(config);
				Main.getInstance().getLoader().save(Main.getRootNode());
				break;
			case "Sign":
				Main.getRootNode().getNode("QuickSetup", "Currency", "Sign").setValue(config);
				Main.getInstance().getLoader().save(Main.getRootNode());
				break;
			case "DisplayMode":
				Main.getRootNode().getNode("QuickSetup", "DisplayMode").setValue(config);
				Main.getInstance().getLoader().save(Main.getRootNode());
				break;
			default:
				Main.sendmessage("{{RED}}Config Inconu", src.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return CommandResult.success();
	}

}
