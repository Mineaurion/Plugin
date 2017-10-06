package com.mineaurion.EconomySpongeMaven.classEconomie;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.economy.EconomyTransactionEvent;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

public class AEconomyTransactionEvent extends AbstractEvent implements EconomyTransactionEvent {

	private TransactionResult transactionResult;

    public AEconomyTransactionEvent(TransactionResult transactionResult) {
        this.transactionResult = transactionResult;
    }

    @Override
    public Cause getCause() {
        return Cause.of(NamedCause.of("TotalEconomy", Sponge.getPluginManager().getPlugin("totaleconomy").get()));
    }

    @Override
    public TransactionResult getTransactionResult() {
        return this.transactionResult;
    }
}
