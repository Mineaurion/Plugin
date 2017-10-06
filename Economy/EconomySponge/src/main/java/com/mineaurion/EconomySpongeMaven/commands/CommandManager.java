package com.mineaurion.EconomySpongeMaven.commands;

import java.util.HashMap;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.mineaurion.EconomySpongeMaven.commands.admin.CMDAdmin;
import com.mineaurion.EconomySpongeMaven.commands.admin.CMDCheck;
import com.mineaurion.EconomySpongeMaven.commands.admin.CMDCurrency;
import com.mineaurion.EconomySpongeMaven.commands.admin.CMDDatabase;
import com.mineaurion.EconomySpongeMaven.commands.admin.CMDReload;
import com.mineaurion.EconomySpongeMaven.commands.money.CMDBalance;
import com.mineaurion.EconomySpongeMaven.commands.money.CMDGive;
import com.mineaurion.EconomySpongeMaven.commands.money.CMDInfinite;
import com.mineaurion.EconomySpongeMaven.commands.money.CMDLog;
import com.mineaurion.EconomySpongeMaven.commands.money.CMDMoney;
import com.mineaurion.EconomySpongeMaven.commands.money.CMDMoneyHelp;
import com.mineaurion.EconomySpongeMaven.commands.money.CMDset;
import com.mineaurion.EconomySpongeMaven.commands.money.CMDtake;
import com.mineaurion.EconomySpongeMaven.commands.money.CMDtop;

public class CommandManager {

	private CommandSpec cmdbalance = CommandSpec.builder()
			.permission("economie.money.player")
			.arguments(GenericArguments.optional(GenericArguments.string(Text.of("player"))))
			.executor(new CMDBalance())
			.build();
			
	private CommandSpec cmdgive = CommandSpec.builder()
			.permission("economie.money.admin")
			.arguments(GenericArguments.user(Text.of("player")),GenericArguments.string(Text.of("montant")))
			.executor(new CMDGive())
			.build();
	
	private CommandSpec cmdinfinite = CommandSpec.builder()
			.permission("economie.money.admin")
			.arguments(GenericArguments.user(Text.of("player")),GenericArguments.bool(Text.of("true|false")))
			.executor(new CMDInfinite())
			.build();
	
	private CommandSpec cmdlog = CommandSpec.builder()
			.permission("economie.money.admin")
			.arguments(GenericArguments.optional(GenericArguments.user(Text.of("player"))))
			.executor(new CMDLog())
			.build();
	
	private CommandSpec cmdset = CommandSpec.builder()
			.permission("economie.money.admin")
			.arguments(GenericArguments.user(Text.of("player")),GenericArguments.string(Text.of("montant")))
			.executor(new CMDset())
			.build();
	
	private CommandSpec cmdtake = CommandSpec.builder()
			.permission("economie.money.admin")
			.arguments(GenericArguments.user(Text.of("player")),GenericArguments.string(Text.of("montant")))
			.executor(new CMDtake())
			.build();
	
	private CommandSpec cmdtop = CommandSpec.builder()
			.permission("economie.money.player")
			.executor(new CMDtop())
			.build();
	
	private CommandSpec cmdMoneyhelp = CommandSpec.builder()
			.permission("economie.money.player")
			.executor(new CMDMoneyHelp())
			.build();

	public CommandSpec cmdmoney = CommandSpec.builder()
			.child(cmdbalance, "balance")
			.child(cmdgive, "give")
			.child(cmdinfinite, "infinite")
			.child(cmdlog, "log")
			.child(cmdset, "set")
			.child(cmdtake, "take")
			.child(cmdtop, "top")
			.child(cmdMoneyhelp, "help")
			.executor(new CMDMoney())
			.build();

	public CommandSpec pay = CommandSpec.builder()
			.permission("economie.pay")
			.arguments(GenericArguments.user(Text.of("player")),GenericArguments.string(Text.of("montant")))
			.executor(new CMDPay())
			.build();

	
	
	@SuppressWarnings("serial")
	HashMap<String, String> arg1 = new HashMap<String, String> (){{put("minor","minor");put("name","name");put("Sign","Sign");put("DisplayMode","DisplayMode");}};
	private CommandSpec cmdsetupcurrency = CommandSpec.builder()
			.permission("economie.admin")
			.arguments(GenericArguments.choices(Text.of("option"),arg1),GenericArguments.remainingJoinedStrings(Text.of("config")))
			.executor(new CMDCurrency())
			.build();
	
	@SuppressWarnings("serial")
	HashMap<String, String> arg2 = new HashMap<String, String> (){{put("address","address");put("port","port");put("Username","Username");put("Password","Password");put("db","db");put("prefix","prefix");}};
	private CommandSpec cmdsetupdatabase = CommandSpec.builder()
			.permission("economie.admin")
			.arguments(GenericArguments.choices(Text.of("option"),arg2),GenericArguments.remainingJoinedStrings(Text.of("config")))
			.executor(new CMDDatabase())
			.build();
	
	/*@SuppressWarnings("serial")
	HashMap<String, String> arg3 = new HashMap<String, String> (){{put("address","address");put("port","port");put("Username","Username");put("Password","Password");put("db","db");put("prefix","prefix");}};
	private CommandSpec cmdsetupconvert = CommandSpec.builder()
			.permission("economie.setup")
			.arguments(GenericArguments.choices(Text.of("option"),arg3),GenericArguments.remainingJoinedStrings(Text.of("config")))
			.executor(new CMDConvertDatabase())
			.build();
*/
	private CommandSpec cmdReload = CommandSpec.builder()
			.permission("economie.admin")
			.executor(new CMDReload())
			.build();

	private CommandSpec cmdCheck = CommandSpec.builder()
			.permission("economie.admin")
			.arguments(GenericArguments.user(Text.of("player")),GenericArguments.string(Text.of("montant")),GenericArguments.remainingJoinedStrings(Text.of("command")))
			.executor(new CMDCheck())
			.build();
	
	public CommandSpec Economieadmin = CommandSpec.builder()
			.permission("economie.admin")
			.child(cmdReload, "reload")
			.child(cmdCheck,"check")
			.child(cmdsetupcurrency, "currency")
			.child(cmdsetupdatabase, "database")
			//.child(cmdsetupconvert, "convert")
			.executor(new CMDAdmin())
			.build();
		
	
	
	
	
}
