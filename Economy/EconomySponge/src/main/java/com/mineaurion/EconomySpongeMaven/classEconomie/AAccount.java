package com.mineaurion.EconomySpongeMaven.classEconomie;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;

import com.mineaurion.EconomySpongeMaven.Main;
import com.mineaurion.EconomySpongeMaven.Mysql.MySQLEngine;

public class AAccount implements UniqueAccount {
	private Main main;
	private AccountManager accountManager;
	private UUID uuid;
	public AAccount(Main main, AccountManager accountManager, UUID uuid) {
		this.main = main;
		this.accountManager = accountManager;
		this.uuid = uuid;
	}

	public Text getDisplayName() {
		if (this.main.getUserStorageService().get(this.uuid).isPresent()) {
			return Text.of(((User) this.main.getUserStorageService().get(this.uuid).get()).getName());
		}
		return Text.of("PLAYER NAME");
	}

	public BigDecimal getDefaultBalance(Currency currency) {
		return new BigDecimal(this.main.getHoldings());
	}

	public boolean hasBalance(Currency currency, Set<Context> contexts) {
		return MySQLEngine.accountExist(this.uuid.toString());
	}

	public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
		double balance = 0.0D;
		if (hasBalance(currency, contexts)) {
			balance = MySQLEngine.getBalance(this.uuid.toString(),false);
		}
		return BigDecimal.valueOf(balance);
	}

	public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {
		return new HashMap<Currency, BigDecimal>();
	}

	public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
		TransactionResult transactionResult;
		if (hasBalance(currency, contexts) && amount.compareTo(BigDecimal.ZERO) >= 0) {
			if(amount==BigDecimal.valueOf(Double.MAX_VALUE)){
				return new ATransactionResult(this, currency, amount, contexts, ResultType.CONTEXT_MISMATCH,
						TransactionTypes.DEPOSIT);
			}
			boolean result = MySQLEngine.setBalance(this.uuid.toString(), amount.doubleValue());
			if (result) {
				transactionResult = new ATransactionResult(this, currency, amount, contexts, ResultType.SUCCESS,
						TransactionTypes.DEPOSIT);
				Sponge.getGame().getEventManager().post(new AEconomyTransactionEvent(transactionResult));
			} else {
				transactionResult = new ATransactionResult(this, currency, amount, contexts, ResultType.FAILED,
						TransactionTypes.DEPOSIT);
				Sponge.getGame().getEventManager().post(new AEconomyTransactionEvent(transactionResult));
			}
			return transactionResult;
		}
		transactionResult = new ATransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS,
				TransactionTypes.DEPOSIT);
		Sponge.getGame().getEventManager().post(new AEconomyTransactionEvent(transactionResult));
		return transactionResult;
	}

	@Override
	public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts) {
		TransactionResult transactionResult = new ATransactionResult(this, Main.getInstance().getDefaultCurrency(),
				BigDecimal.ZERO, contexts, ResultType.FAILED, TransactionTypes.WITHDRAW);
		Sponge.getGame().getEventManager().post(new AEconomyTransactionEvent(transactionResult));

		Map<Currency, TransactionResult> result = new HashMap<>();
		result.put(accountManager.getDefaultCurrency(), transactionResult);

		return result;
	}

	@Override
	public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
		return setBalance(currency, BigDecimal.ZERO, cause);
	}

	@Override
	public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
		BigDecimal curBalance = getBalance(currency, contexts);
		
		BigDecimal newBalance = curBalance.add(amount);
		return setBalance(currency, newBalance,
				Cause.of(NamedCause.of("AurionsEconomy", Main.getInstance().getPlugin())));
	}

	@Override
	public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
		BigDecimal curBalance = getBalance(currency, contexts);
		if(curBalance.compareTo(amount)<0||curBalance.compareTo(amount)==0){
			return new ATransactionResult(this, currency, amount, contexts, ResultType.FAILED,
					TransactionTypes.WITHDRAW);
		}
		if(curBalance==BigDecimal.valueOf(Double.MAX_VALUE)){
			return new ATransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS,
					TransactionTypes.WITHDRAW);
		}
		BigDecimal newBalance = curBalance.subtract(amount);
		return setBalance(currency, newBalance,
				Cause.of(NamedCause.of("AurionsEconomy", Main.getInstance().getPlugin())));
	}

	@Override
	public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause,
			Set<Context> contexts) {
		TransferResult transferResult;
		if (hasBalance(currency, contexts)) {
			BigDecimal curBalance = getBalance(currency, contexts);
			BigDecimal newBalance = curBalance.subtract(amount);
			if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
				withdraw(currency, amount, cause, contexts);

				if (to.hasBalance(currency)) {
					to.deposit(currency, amount, cause, contexts);

					transferResult = new ATransfertResult(this, to, currency, amount, contexts, ResultType.SUCCESS,
							TransactionTypes.TRANSFER);
					Sponge.getGame().getEventManager().post(new AEconomyTransactionEvent(transferResult));

					return transferResult;
				} else {
					transferResult = new ATransfertResult(this, to, currency, amount, contexts, ResultType.FAILED,
							TransactionTypes.TRANSFER);
					Sponge.getGame().getEventManager().post(new AEconomyTransactionEvent(transferResult));

					return transferResult;
				}
			} else {
				transferResult = new ATransfertResult(this, to, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS,
						TransactionTypes.TRANSFER);
				Sponge.getGame().getEventManager().post(new AEconomyTransactionEvent(transferResult));

				return transferResult;
			}
		}
		transferResult = new ATransfertResult(this, to, currency, amount, contexts, ResultType.FAILED,
				TransactionTypes.TRANSFER);
		Sponge.getGame().getEventManager().post(new AEconomyTransactionEvent(transferResult));

		return transferResult;
	}

	@Override
	public String getIdentifier() {
		 return uuid.toString();
	}

	@Override
	public Set<Context> getActiveContexts() {
		 return new HashSet<>();
	}

	@Override
	public UUID getUniqueId() {
		 return uuid;
	}

}
