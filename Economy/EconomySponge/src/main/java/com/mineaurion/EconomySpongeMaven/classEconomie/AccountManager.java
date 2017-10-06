package com.mineaurion.EconomySpongeMaven.classEconomie;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import com.mineaurion.EconomySpongeMaven.Main;
import com.mineaurion.EconomySpongeMaven.Mysql.MySQLEngine;

public class AccountManager implements EconomyService {

	private Main main;
	private MySQLEngine mysqlEngine;

	public AccountManager(Main main) {
		this.main = main;
		mysqlEngine = main.getMysqlEngine();
		mysqlEngine.setupDatabase();
	}

	@Override
	public Optional<UniqueAccount> getOrCreateAccount(UUID uuid) {
		AAccount playeraccount = new AAccount(main, this, uuid);
		if (!hasAccount(uuid)&&main.getPlayer(uuid).isOnline()) {
			MySQLEngine.createaccount(uuid.toString(),main.getPlayer(uuid).getName(), main.getHoldings());
		}
		return Optional.of(playeraccount);
	}

	// Ne jamais utilisé
	@Override
	public Optional<Account> getOrCreateAccount(String identifier) {
		AVirtualAccount virtualAccount = new AVirtualAccount(main, this, identifier);
		return Optional.of(virtualAccount);
	}

	@Override
	public boolean hasAccount(UUID uuid) {
		return MySQLEngine.accountExist(uuid.toString());
	}

	@Override
	public void registerContextCalculator(ContextCalculator<Account> calculator) {
		// TODO Auto-generated method stub

	}

	@Override
	public Currency getDefaultCurrency() {

		return Main.getInstance().getDefaultCurrency();
	}

	@Override
	public Set<Currency> getCurrencies() {
		return new HashSet<>();
	}

	@Override
	public boolean hasAccount(String identifier) {
		return false;
	}

}
