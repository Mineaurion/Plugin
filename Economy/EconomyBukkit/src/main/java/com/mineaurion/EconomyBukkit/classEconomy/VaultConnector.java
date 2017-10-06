package com.mineaurion.EconomyBukkit.classEconomy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.mineaurion.EconomyBukkit.Main;
import com.mineaurion.EconomyBukkit.Mysql.MySQLEngine;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class VaultConnector implements Economy {

	public ACurrency currency = Main.getInstance().getDefaultCurrency();

	@Override
	public boolean isEnabled() {
		return Main.instance != null && Main.instance.isEnabled();
	}

	@Override
	public String getName() {
		return "EconomyBukkit";
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public int fractionalDigits() {
		return 2;
	}

	@Override
	public String format(double amount) {
		return currency.format(amount);
	}

	@Override
	public String currencyNamePlural() {
		return currency.getPluralDisplayName();
	}

	@Override
	public String currencyNameSingular() {
		return currency.getDisplayName();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean hasAccount(String playerName) {
		UUID uuid;
		if(Bukkit.getPlayer(playerName)!=null){
		 uuid = Bukkit.getPlayer(playerName).getUniqueId();
		}else
		{
			uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
		}
		return MySQLEngine.accountExist(uuid.toString());
	}

	@Override
	public boolean hasAccount(OfflinePlayer offlinePlayer) {
		UUID uuid = offlinePlayer.getPlayer().getUniqueId();
		return MySQLEngine.accountExist(uuid.toString());
	}

	@SuppressWarnings("deprecation")
	@Override
	public double getBalance(String playerName) {
		double balance = 0.0D;
		if (hasAccount(playerName)) {
			UUID uuid;
			if(Bukkit.getPlayer(playerName)!=null){
			 uuid = Bukkit.getPlayer(playerName).getUniqueId();
			}else
			{
				uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
			}
			balance = MySQLEngine.getBalance(uuid.toString(), false);
		}
		return balance;
	}

	@Override
	public double getBalance(OfflinePlayer offlinePlayer) {
		double balance = 0.0D;
		if (hasAccount(offlinePlayer)) {
			UUID uuid = offlinePlayer.getUniqueId();
			balance = MySQLEngine.getBalance(uuid.toString(), false);
		}
		return balance;
	}

	@Override
	public boolean has(String playerName, double amount) {
		double balance = getBalance(playerName);
		return balance > amount;
	}

	@Override
	public boolean has(OfflinePlayer offlinePlayer, double amount) {
		double balance = getBalance(offlinePlayer);
		return balance > amount;
	}

	@SuppressWarnings("deprecation")
	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		double balance = getBalance(playerName);
		if (balance < amount) {
			return new EconomyResponse(0, balance, ResponseType.FAILURE, "Insufficient funds.");
		}
		double newBalance = balance - amount;

		UUID uuid;
		if(Bukkit.getPlayer(playerName)!=null){
		 uuid = Bukkit.getPlayer(playerName).getUniqueId();
		}else
		{
			uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
		}
		
		return setBalance(uuid, newBalance, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
		double balance = getBalance(offlinePlayer);
		if (balance < amount) {
			return new EconomyResponse(0, balance, ResponseType.FAILURE, "Insufficient funds.");
		}
		double newBalance = balance - amount;

		return setBalance(offlinePlayer.getUniqueId(), newBalance, amount);
	}

	@SuppressWarnings("deprecation")
	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		double balance = getBalance(playerName);
		double newBalance = balance + amount;
		UUID uuid;
		if(Bukkit.getPlayer(playerName)!=null){
		 uuid = Bukkit.getPlayer(playerName).getUniqueId();
		}else
		{
			uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
		}
		
		return setBalance(uuid, newBalance, amount);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
		double balance = getBalance(offlinePlayer);
		double newBalance = balance + amount;
		return setBalance(offlinePlayer.getUniqueId(), newBalance, amount);
	}

	private EconomyResponse setBalance(UUID uuid, double newBalance, double amount) {
		if (newBalance >= 0) {
			boolean result = MySQLEngine.setBalance(uuid.toString(), newBalance);
			if (result) {
				return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
			} else {
				return new EconomyResponse(0, newBalance, ResponseType.FAILURE, "Error Mysql");
			}
		}
		return new EconomyResponse(0, newBalance, ResponseType.FAILURE, "Error nouveau montant > 0");
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "not Implemented");
		// BankAccount bank = eco.bank(name).addOwner(player);
		// if (bank.exists())
		// return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to
		// create bank!");
		// else
		// return new EconomyResponse(0, 0, ResponseType.SUCCESS, "Created bank
		// " + name);
	}

	@Override
	public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "not Implemented");
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "not Implemented");
		// Account deleted = eco.bank(name).delete();
		// if (deleted.exists())
		// return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to
		// delete bank account!");
		// else
		// return new EconomyResponse(0, 0, ResponseType.SUCCESS, "Deleted bank
		// account (or it didn't
		// exist)");
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "not Implemented");
		// double balance = eco.bank(name).balance();
		// return new EconomyResponse(0, balance,
		// ResponseType.SUCCESS, "Balance of bank "+ name +": "+ balance);
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "not Implemented");
		// BankAccount bank = eco.bank(name);
		// double balance = bank.balance();
		// if (bank.has(amount))
		// return new EconomyResponse(0, balance, ResponseType.SUCCESS, "Bank "
		// + name + " has at least " +
		// amount );
		// else
		// return new EconomyResponse(0, balance, ResponseType.FAILURE, "Bank "
		// + name + " does not have at
		// least " + amount );
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "not Implemented");
		// BankAccount bank = eco.bank(name);
		// TransactionResult result = bank.remove(amount);
		// if (result == TransactionResult.SUCCESS)
		// return new EconomyResponse(amount, bank.balance(),
		// ResponseType.SUCCESS, "Removed " + amount + "
		// from bank " + name);
		// else
		// return new EconomyResponse(0, bank.balance(), ResponseType.SUCCESS,
		// "Failed to remove " + amount +
		// " from bank " + name);
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "not Implemented");
		// BankAccount bank = eco.bank(name);
		// TransactionResult result = bank.add(amount);
		// if (result == TransactionResult.SUCCESS)
		// return new EconomyResponse(amount, bank.balance(),
		// ResponseType.SUCCESS, "Added " + amount + " to
		// bank " + name);
		// else
		// return new EconomyResponse(0, bank.balance(), ResponseType.SUCCESS,
		// "Failed to add " + amount + "
		// to bank " + name);
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "not Implemented");
		// return new EconomyResponse(0, 0, eco.bank(name).isOwner(playerName)?
		// ResponseType.SUCCESS :
		// FAILURE, "");
	}

	@Override
	public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "not Implemented");
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "not Implemented");
		// return new EconomyResponse(0, 0, eco.bank(name).isMember(playerName)?
		// ResponseType.SUCCESS : FAILURE,
		// "");
	}

	@Override
	public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "not Implemented");
	}

	@Override
	public List<String> getBanks() {
		return new ArrayList<>();
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		return hasAccount(playerName);
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
		return hasAccount(offlinePlayer);
	}

	@Override
	public boolean createPlayerAccount(String playerName, String world) {
		return hasAccount(playerName); // TODO multiworld support
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
		return hasAccount(offlinePlayer); // TODO multiworld support
	}

	@Override
	public EconomyResponse depositPlayer(String player, String world, double amount) {
		return depositPlayer(player, amount);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String world, double amount) {
		return depositPlayer(offlinePlayer, amount);
	}

	@Override
	public double getBalance(String player, String world) {
		return getBalance(player);
	}

	@Override
	public double getBalance(OfflinePlayer offlinePlayer, String world) {
		return getBalance(offlinePlayer); // TODO multiworld-support
	}

	@Override
	public boolean has(String player, String world, double amount) {
		return has(player, amount); // TODO multiworld-support
	}

	@Override
	public boolean has(OfflinePlayer offlinePlayer, String world, double amount) {
		return has(offlinePlayer, amount); // TODO multiworld-support
	}

	@Override
	public boolean hasAccount(String player, String world) {
		return hasAccount(player);
	}

	@Override
	public boolean hasAccount(OfflinePlayer offlinePlayer, String world) {
		return hasAccount(offlinePlayer);
	}

	@Override
	public EconomyResponse withdrawPlayer(String player, String world, double amount) {
		return withdrawPlayer(player, amount); // TODO multiworld-support
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String world, double amount) {
		return withdrawPlayer(offlinePlayer, amount); // TODO multiworld-support
	}

}
