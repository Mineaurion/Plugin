package com.mineaurion.EconomySpongeMaven.commands;

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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.mineaurion.EconomySpongeMaven.LogInfo;
import com.mineaurion.EconomySpongeMaven.Main;
import com.mineaurion.EconomySpongeMaven.classEconomie.AAccount;
import com.mineaurion.EconomySpongeMaven.classEconomie.AccountManager;

public class CMDPay implements CommandExecutor {
	private AccountManager accountManager;

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		User player = args.<User>getOne("player").get();
		String strAmount = args.<String>getOne("montant").get();
		Pattern amountPattern = Pattern.compile("^[+]?(\\d*\\.)?\\d+$");
		Matcher m = amountPattern.matcher(strAmount);

		if (m.matches()) {
			BigDecimal amount = new BigDecimal(strAmount).setScale(2, BigDecimal.ROUND_DOWN);
			accountManager = Main.getInstance().getAccountManager();
			Currency defaultCurrency = accountManager.getDefaultCurrency();

			if (src instanceof Player) {

				Player sender = (Player) src;
				if (sender.getUniqueId().equals(player.getUniqueId())) {
					throw new CommandException(Text.of("You cannot pay yourself!"));
				}

				AAccount playerAccount = (AAccount) accountManager.getOrCreateAccount(sender.getUniqueId()).get();
				AAccount recipientAccount = (AAccount) accountManager.getOrCreateAccount(player.getUniqueId()).get();

				TransferResult transferResult = playerAccount.transfer(recipientAccount,
						accountManager.getDefaultCurrency(), amount,
						Cause.of(NamedCause.of("Aurion", Main.getInstance().getPlugin())));

				if (transferResult.getResult() == ResultType.SUCCESS) {
					String amountText = TextSerializers.FORMATTING_CODE.serialize(defaultCurrency.format(amount));
					DateTime dateTime = DateTime.now(DateTimeZone.forID("Europe/Paris"));
					Main.writeLog(sender.getName()+"->"+player.getName(), LogInfo.PAY, Cause.of(NamedCause.of("AurionsEconomy", "Sponge")), dateTime, amount.doubleValue());
					Main.sendmessage("Tu as envoye {{GOLD}}" + amountText + "{{WHITE}} a " + player.getName(),
							src.getName());
					if (player.isOnline()) {
						Main.sendmessage("Tu as recu {{GOLD}}" + amountText + "{{WHITE}} de " + src.getName(),
								src.getName());
					}

					return CommandResult.success();
				} else if (transferResult.getResult() == ResultType.ACCOUNT_NO_FUNDS) {
					throw new CommandException(Text.of("Insufficient funds!"));
				}
			} else {
				throw new CommandException(Text.of("Tu n'est pas un joueurs utilise /money give"));
			}
		} else {
			throw new CommandException(Text.of("Invalid amount! Must be a positive number!"));
		}
		return CommandResult.success();

	}

}
