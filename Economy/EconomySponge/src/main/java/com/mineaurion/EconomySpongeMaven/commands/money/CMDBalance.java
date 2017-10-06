package com.mineaurion.EconomySpongeMaven.commands.money;

import java.math.BigDecimal;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import com.mineaurion.EconomySpongeMaven.Main;
import com.mineaurion.EconomySpongeMaven.Mysql.MySQLEngine;
import com.mineaurion.EconomySpongeMaven.classEconomie.AAccount;
import com.mineaurion.EconomySpongeMaven.classEconomie.AccountManager;

public class CMDBalance implements CommandExecutor {
	private AccountManager accountManager;

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		 
		accountManager = Main.getInstance().getAccountManager();
		if (args.< String>getOne("player").isPresent()) {
			String player = args.< String>getOne("player").get();
			
			Currency defaultCurrency = accountManager.getDefaultCurrency();
			Text playerBalance = defaultCurrency.format(new BigDecimal(MySQLEngine.getBalanceName(player, false)));
			Main.sendmessage("Balance de {{CYAN}}" + player + "{{WHITE}}: " + playerBalance,
					src.getName());
			return CommandResult.success();
		} else {
			if (src instanceof Player) {
				Player sender = (Player) src;
				AAccount playerAccount = (AAccount) accountManager.getOrCreateAccount(sender.getUniqueId()).get();
				Currency defaultCurrency = accountManager.getDefaultCurrency();
				Text playerBalance = defaultCurrency.format(playerAccount.getBalance(defaultCurrency));
				Main.sendmessage("Balance : " + playerBalance.toPlainSingle(),
						src.getName());
				return CommandResult.success();
			}else{
				Main.sendmessage("La console na pas de monney, utilise /money balance <Player>", "console");
				return CommandResult.success();
			}
		}
	}

}
