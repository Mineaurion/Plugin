package com.mineaurion.EconomySpongeMaven.commands.admin;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;

import com.mineaurion.EconomySpongeMaven.LogInfo;
import com.mineaurion.EconomySpongeMaven.Main;
import com.mineaurion.EconomySpongeMaven.classEconomie.AAccount;
import com.mineaurion.EconomySpongeMaven.classEconomie.AccountManager;

public class CMDCheck implements CommandExecutor {

	private AccountManager accountManager;

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		accountManager = Main.getInstance().getAccountManager();

		String commands = args.<String>getOne("command").get();
		User player = args.<User>getOne("player").get();
		String strAmount = args.<String>getOne("montant").get();
		Pattern amountPattern = Pattern.compile("^[+]?(\\d*\\.)?\\d+$");
		Matcher m = amountPattern.matcher(strAmount);

		if (m.matches()) {
			BigDecimal amount = new BigDecimal(strAmount).setScale(2, BigDecimal.ROUND_DOWN);
			AAccount playeraccount = (AAccount) accountManager.getOrCreateAccount(player.getUniqueId()).get();
			Currency defaultCurrency = accountManager.getDefaultCurrency();
			Text amountText = defaultCurrency.format(amount);

			TransactionResult transactionresult = playeraccount.withdraw(defaultCurrency, amount,
					Cause.of(EventContext.empty(),Main.getInstance().getPlugin()));
			if (transactionresult.getResult() == ResultType.SUCCESS||transactionresult.getResult() == ResultType.ACCOUNT_NO_FUNDS) {
				DateTime dateTime = DateTime.now(DateTimeZone.forID("Europe/Paris"));
				Main.writeLog(player.getName(), LogInfo.CHECK, Cause.of(EventContext.empty(),Main.getInstance().getPlugin()), dateTime, amount.doubleValue());
				Main.sendmessage("Tu as retirer {{RED}}" + amountText.toPlainSingle() + "{{WHITE}} a {{YELLOW}}" + player.getName(),
						src.getName());
				String[] parts = commands.split("/");

				for (int i = 0; i < parts.length; i++) {
					String command = parts[i];
					Sponge.getScheduler().createTaskBuilder()
							.execute(() -> Sponge.getCommandManager().process(
									Sponge.getServer().getConsole().getCommandSource().get(),
									command.replace("<username>", player.getName())))
							.submit(Main.getInstance());
				}

				if (player.isOnline()) {
					Main.sendmessage("Tu as depense {{RED}}" + amountText.toPlainSingle() + "", player.getName());
				}
				return CommandResult.success();
			} else {
				throw new CommandException(Text.of("You don't have money"));
			}
		} else {
			throw new CommandException(Text.of("Invalid amount! Must be a positive number!"));
		}
	}

}
