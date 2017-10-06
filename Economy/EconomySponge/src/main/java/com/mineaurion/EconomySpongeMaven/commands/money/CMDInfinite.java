package com.mineaurion.EconomySpongeMaven.commands.money;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;

import com.mineaurion.EconomySpongeMaven.Main;
import com.mineaurion.EconomySpongeMaven.Mysql.MySQLEngine;

public class CMDInfinite implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		User player = args.<User>getOne("player").get();
		boolean valeur = args.<Boolean>getOne("true|false").get();
		
		if(MySQLEngine.setInfiniteMoney(player.getUniqueId().toString(), valeur)){
			Main.sendmessage("Infiny monney pour "+player.getName()+" : {{RED}}"+String.valueOf(valeur), src.getName());
			return CommandResult.success();
		}else{
			Main.sendmessage("une erreur est survenu check la console", src.getName());
			return CommandResult.success();
		}
		
	}

}
