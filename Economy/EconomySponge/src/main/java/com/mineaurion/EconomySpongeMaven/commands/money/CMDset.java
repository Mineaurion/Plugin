package com.mineaurion.EconomySpongeMaven.commands.money;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.mineaurion.EconomySpongeMaven.LogInfo;
import com.mineaurion.EconomySpongeMaven.Main;
import com.mineaurion.EconomySpongeMaven.classEconomie.AAccount;
import com.mineaurion.EconomySpongeMaven.classEconomie.AccountManager;

public class CMDset implements CommandExecutor {
	private AccountManager accountManager;

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		accountManager = Main.getInstance().getAccountManager();

		User player = args.<User>getOne("player").get();
		String strAmount = args.<String>getOne("montant").get();
		Pattern amountPattern = Pattern.compile("^[+]?(\\d*\\.)?\\d+$");
		Matcher m = amountPattern.matcher(strAmount);

		if (m.matches()) {
			BigDecimal amount = new BigDecimal(strAmount).setScale(2, BigDecimal.ROUND_DOWN);
			Currency defaultCurrency = accountManager.getDefaultCurrency();
			String amountText = TextSerializers.FORMATTING_CODE.serialize(defaultCurrency.format(amount));
			AAccount playeraccount = (AAccount) accountManager.getOrCreateAccount(player.getUniqueId()).get();
			playeraccount.setBalance(defaultCurrency, amount,
					Cause.of(NamedCause.of("Aurions", Main.getInstance().getPlugin())));
			DateTime dateTime = DateTime.now(DateTimeZone.forID("Europe/Paris"));
			Main.writeLog(player.getName(), LogInfo.SET, Cause.of(NamedCause.of("AurionsEconomy", "Sponge")), dateTime, amount.doubleValue());
			Main.sendmessage("Balance set pour " + player.getName() + " a " + amountText, src.getName());
		} else {
			throw new CommandException(Text.of("Invalid amount! Must be a positive number!"));
		}
		return CommandResult.success();
	}

}
