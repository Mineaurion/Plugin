package com.mineaurion.EconomySpongeMaven.event;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class EconomyChangeEvent extends AbstractEvent {
	private String account;
    private double amount;

    public EconomyChangeEvent(String account, double amount) {
        this.account = account;
        this.amount = amount;
    }

    @Override
    public Cause getCause() {
        return null;
    }
}
