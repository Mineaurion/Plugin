package com.mineaurion.EconomySpongeMaven.classEconomie;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import com.mineaurion.EconomySpongeMaven.DisplayFormat;
import com.mineaurion.EconomySpongeMaven.Main;

public class ACurrency implements Currency {

	private Text singular;
    private Text plural;
    private Text symbol;
    private int numFractionDigits;
    private boolean defaultCurrency;
    
    public ACurrency(Text singular, Text plural, Text symbol, int numFractionDigits, boolean defaultCurrency) {
        this.singular = singular;
        this.plural = plural;
        this.symbol = symbol;
        this.numFractionDigits = numFractionDigits;
        this.defaultCurrency = defaultCurrency;
    }
      
    
	@Override
	public String getId() {
		
		return "AurionsEconomie:"+singular.toPlain();
	}
	@Override
	public String getName() {
		
		return singular.toPlain();
	}
	@Override
	public Text getDisplayName() {
		
		return singular;
	}
	@Override
	public Text getPluralDisplayName() {
		
		return plural;
	}
	@Override
	public Text getSymbol() {
		
		return symbol;
	}
	@Override
	public Text format(BigDecimal balance, int numFractionDigits) {
		DisplayFormat format = Main.getInstance().getDisplayFormat();
		
		StringBuilder string = new StringBuilder();
		
		String[] theAmount = balance.toPlainString().split("\\.");
		
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
					.append("{{RESET}} ").append(name);
		} else if (format == DisplayFormat.SIGN) {
			string.append(Main.getInstance().currencyMajorColor).append(Main.getInstance().getCurrencySign()).append(amount).append(".")
					.append(Main.getInstance().currencyMinorColor).append(coin).append("{{RESET}}");
		} else if (format == DisplayFormat.SIGNFRONT) {
			string.append(Main.getInstance().currencyMajorColor).append(amount).append(".").append(Main.getInstance().currencyMinorColor).append(coin)
					.append("{{RESET}}").append(Main.getInstance().getCurrencySign());
		} else if (format == DisplayFormat.MAJORONLY) {
			string.append(Main.getInstance().currencyMajorColor).append(amount).append(" ").append(name).append("{{RESET}}");
		}
	
	return Text.of(string.toString());
	}
	@Override
	public int getDefaultFractionDigits() {
		
		return numFractionDigits;
	}
	@Override
	public boolean isDefault() {
		
		return defaultCurrency;
	}
    
    
	
}
