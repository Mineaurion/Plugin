package com.mineaurion.EconomySpongeMaven.commands.admin;

import java.io.IOException;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import com.mineaurion.EconomySpongeMaven.Main;

public class CMDDatabase implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String arg = args.<String>getOne("option").get();
		String config = args.<String>getOne("config").get();
		try {
			switch (arg) {
			case "address":
				Main.getRootNode().getNode("Database", "Address").setValue(config);
				Main.getInstance().getLoader().save(Main.getRootNode());
				break;
			case "port":
				Main.getRootNode().getNode("Database", "Port").setValue(config);
				Main.getInstance().getLoader().save(Main.getRootNode());
				break;
			case "Username":
				Main.getRootNode().getNode("Database", "Username").setValue(config);
				Main.getInstance().getLoader().save(Main.getRootNode());
				break;
			case "Password":
				Main.getRootNode().getNode("Database", "Password").setValue(config);
				Main.getInstance().getLoader().save(Main.getRootNode());
				break;
			case "db":
				Main.getRootNode().getNode("Database", "Db").setValue(config);
				Main.getInstance().getLoader().save(Main.getRootNode());
				break;
			case "prefix":
				Main.getRootNode().getNode("Database", "Prefix").setValue(config);
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
