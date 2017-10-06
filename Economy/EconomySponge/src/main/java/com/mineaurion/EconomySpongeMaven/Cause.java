package com.mineaurion.EconomySpongeMaven;

import java.io.Serializable;

public enum Cause implements Serializable {
	VAULT, USER, PLUGIN, SPOUT, CONVERT, EXCHANGE, PAYMENT, PAYDAY_TAX, PAYDAY_WAGE, UNKNOWN;

	private Cause() {
	}
}