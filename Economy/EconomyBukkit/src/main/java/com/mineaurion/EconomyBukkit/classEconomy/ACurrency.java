package com.mineaurion.EconomyBukkit.classEconomy;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import com.mineaurion.EconomyBukkit.DisplayFormat;
import com.mineaurion.EconomyBukkit.Main;

public class ACurrency {
	private String singular;
    private String plural;
    private String symbol;
    private int numFractionDigits;
    private boolean defaultCurrency;
    
    public ACurrency(String singular, String plural, String symbol, int numFractionDigits, boolean defaultCurrency) {
        this.singular = singular;
        this.plural = plural;
        this.symbol = symbol;
        this.numFractionDigits = numFractionDigits;
        this.defaultCurrency = defaultCurrency;
    }
      
    
	public String getId() {
		
		return "AurionsEconomie:"+singular;
	}
	
	public String getName() {
		
		return singular;
	}
	
	public String getDisplayName() {
		
		return singular;
	}
	
	public String getPluralDisplayName() {
		
		return plural;
	}
	
	public String getSymbol() {
		
		return symbol;
	}
	
	public String format(double balance) {
		DisplayFormat format = Main.getInstance().getDisplayFormat();
		
		StringBuilder string = new StringBuilder();
		
		String[] theAmount = Double.toString(balance).split("\\.");
		
		DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols();
		
		unusualSymbols.setGroupingSeparator(',');
		
		DecimalFormat decimalFormat = new DecimalFormat("###,###", unusualSymbols);getClass();
		
		String name = getName();
		
		String coin;
		
		if (theAmount.length == 2) {
			if (theAmount[1].length() >= 2) {
				coin = theAmount[1].substring(0, 2);
			} else {
				coin = theAmount[1] + "0";
			}
		} else {
			coin = "0";
		}

		String amount;
		
		try {
			
			amount = decimalFormat.format(Double.parseDouble(theAmount[0]));
			
		} catch (NumberFormatException e) {
			
			amount = theAmount[0];
			
		}
		if (format == DisplayFormat.LONG) {
			String subName = Main.getInstance().getCurrencyMinor();
			
			string.append(Main.getInstance().currencyMajorColor).append(amount).append(" {{RESET}}").append(name).append(" ")
					.append(Main.getInstance().currencyMinorColor).append(Long.toString(Long.parseLong(coin))).append(" {{RESET}}")
					.append(subName).append("{{RESET}}");
		} else if (format == DisplayFormat.SMALL) {
			string.append(Main.getInstance().currencyMajorColor).append(amount).append(".").append(Main.getInstance().currencyMinorColor).append(coin)
					.append("{{RESET}}").append(name);
		} else if (format == DisplayFormat.SIGN) {
			string.append(Main.getInstance().currencyMajorColor).append(Main.getInstance().getCurrencySign()).append(amount).append(".")
					.append(Main.getInstance().currencyMinorColor).append(coin).append("{{RESET}}");
		} else if (format == DisplayFormat.SIGNFRONT) {
			string.append(Main.getInstance().currencyMajorColor).append(amount).append(".").append(Main.getInstance().currencyMinorColor).append(coin)
					.append("{{RESET}}").append(Main.getInstance().getCurrencySign());
		} else if (format == DisplayFormat.MAJORONLY) {
			string.append(Main.getInstance().currencyMajorColor).append(amount).append(" ").append(name).append("{{RESET}}");
		}
	
	return string.toString();
	}
	
	public int getDefaultFractionDigits() {
		
		return numFractionDigits;
	}
	
	public boolean isDefault() {
		
		return defaultCurrency;
	}
    
}
