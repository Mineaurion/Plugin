package com.mineaurion.EconomyBukkit;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import com.mineaurion.EconomyBukkit.Mysql.MySQLEngine;
import com.mineaurion.EconomyBukkit.classEconomy.ACurrency;
import com.mineaurion.EconomyBukkit.classEconomy.VaultConnector;
import com.mineaurion.EconomyBukkit.command.Tabcomplete;
import com.mineaurion.EconomyBukkit.command.admin.CommandAdmin;
import com.mineaurion.EconomyBukkit.command.monney.CommandMoney;
import com.mineaurion.EconomyBukkit.command.pay.CommandPay;
import com.mineaurion.EconomyBukkit.event.EventManager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

	public FileConfiguration config;
	private static final Logger log = Logger.getLogger("Minecraft");

	@Getter
	private MySQLEngine mysqlEngine;
	@Getter
	private EventManager eventManager = null;
	@Getter
	public static Main instance = null;
	@Getter
	private ACurrency defaultCurrency;
	@Getter
	private VaultConnector vaultconnector;
	@Getter
	private DisplayFormat displayFormat = null;
	@Getter
	private double holdings = 0.0D;
	@Getter
	public String currencyName;
	@Getter
	public String currencySign;
	@Getter
	public String currencyMinor;

	public String currencyMajorColor = "{{BOLD}}{{CYAN}}";
	public String currencyMinorColor = "{{BOLD}}{{CYAN}}";

	public static String prefix = "{{DARK_GREEN}}[{{WHITE}}Money{{DARK_GREEN}}]{{WHITE}} ";

	@Override
	public void onDisable() {
		log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
	}

	@Override
	public void onEnable() {
		sendmessage("{{GOLD}}AurionEconomy demarre", "console");
		instance = this;

		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		config = this.getConfig();

		if (!config.getBoolean("Setup")) {
			sendmessage(
					"{{DARK_RED}}Configure le plugin (pense a changer le Setup en true) puis fait /economieadmin reload",
					"console");
		} else {
			// Si config setup
			sendmessage("{{GOLD}}Loading default settings", "console");
			loadDefaultSettings();
			sendmessage("{{GOLD}}Default settings loaded", "console");

			try {
				initialiseDatabase();
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			sendmessage("{{GOLD}}Loading Economie Class", "console");
			if (!setupEconomy()) {
				log.severe(
						String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
			sendmessage("{{GOLD}}Loading commands", "console");
			registerCommands();
			startUp();

			sendmessage("{{GOLD}}Ready", "console");
		}

	}

	private void registerCommands() {
		getCommand("money").setExecutor(new CommandMoney());
		getCommand("money").setTabCompleter(new Tabcomplete());
		getCommand("pay").setExecutor(new CommandPay());
		getCommand("economieadmin").setExecutor(new CommandAdmin());
		getCommand("economieadmin").setTabCompleter(new Tabcomplete());

	}

	private void loadDefaultSettings() {
		String value = config.getString("QuickSetup.DisplayMode").toUpperCase();
		if (value != null) {
			this.displayFormat = DisplayFormat.valueOf(value);
		} else {

			config.set("QuickSetup.DisplayMode", "LONG");
			this.displayFormat = DisplayFormat.LONG;
		}

		this.holdings = Double.parseDouble(config.getString("QuickSetup.StartBalance"));

		this.currencyMinor = config.getString("QuickSetup.Currency.Minor");
		this.currencyName = config.getString("QuickSetup.Currency.Name");
		this.currencySign = config.getString("QuickSetup.Currency.Sign");

	}

	private void initialiseDatabase() throws ClassNotFoundException, SQLException {

		sendmessage("{{GOLD}}Loading DataBase", "console");
		mysqlEngine = new MySQLEngine(this);
		DateTime dateTime = DateTime.now(DateTimeZone.forID("Europe/Paris"));

		MySQLEngine.clearLog(dateTime.minusMonths(1));
		sendmessage("{{GOLD}}DataBase loaded", "console");

	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			sendmessage("{{RED}}Not found", "console");
			return false;
		}
		Bukkit.getServer().getServicesManager().register(Economy.class, new VaultConnector(),this, ServicePriority.Highest);
		defaultCurrency = new ACurrency(currencyName, currencyName + "s", currencySign, 2,
				true);
		vaultconnector = new VaultConnector();
		return true;
	}

	public void startUp() {
		sendmessage("{{GOLD}}Loading listeners", "console");
		getServer().getPluginManager().registerEvents(new EventManager(), this);
		this.eventManager = new EventManager();
	}

	@SuppressWarnings("deprecation")
	public static void sendmessage(String message, String sender) {
		if (sender.equalsIgnoreCase("console") || sender.equalsIgnoreCase("Server")) {
			Bukkit.getConsoleSender().sendMessage(addColor(message));
		} else {
			Bukkit.getPlayer(sender).sendMessage(addColor(message));
		}
	}

	private static String addColor(String message) {
		message = prefix + message;
		StringBuilder textmain = new StringBuilder();
		Matcher m = Pattern.compile("(\\{\\{([^\\{\\}]+)\\}\\}|[^\\{\\}]+)").matcher(message);
		ChatColor color = null;
		ChatColor style = null;
		while (m.find()) {

			String entry = m.group();
			if (entry.contains("{{")) {
				color = null;
				style = null;
				switch (entry) {
				case "{{BLACK}}":
					color = ChatColor.BLACK;
					break;
				case "{{DARK_BLUE}}":
					color = ChatColor.DARK_BLUE;
					break;
				case "{{DARK_GREEN}}":
					color = ChatColor.DARK_GREEN;
					break;
				case "{{DARK_CYAN}}":
					color = ChatColor.DARK_AQUA;
					break;
				case "{{DARK_RED}}":
					color = ChatColor.DARK_RED;
					break;
				case "{{PURPLE}}":
					color = ChatColor.DARK_PURPLE;
					break;
				case "{{GOLD}}":
					color = ChatColor.GOLD;
					break;
				case "{{GRAY}}":
					color = ChatColor.GRAY;
					break;
				case "{{DARK_GRAY}}":
					color = ChatColor.DARK_GRAY;
					break;
				case "{{BLUE}}":
					color = ChatColor.AQUA;
					break;
				case "{{BRIGHT_GREEN}}":
					color = ChatColor.GREEN;
					break;
				case "{{CYAN}}":
					color = ChatColor.AQUA;
					break;
				case "{{RED}}":
					color = ChatColor.RED;
					break;
				case "{{LIGHT_PURPLE}}":
					color = ChatColor.LIGHT_PURPLE;
					break;
				case "{{YELLOW}}":
					color = ChatColor.YELLOW;
					break;
				case "{{WHITE}}":
					color = ChatColor.WHITE;
					break;
				case "{{OBFUSCATED}}":
					style = ChatColor.MAGIC;
					break;
				case "{{BOLD}}":
					style = ChatColor.BOLD;
					break;
				case "{{STRIKETHROUGH}}":
					style = ChatColor.STRIKETHROUGH;
					break;
				case "{{UNDERLINE}}":
					style = ChatColor.UNDERLINE;
					break;
				case "{{ITALIC}}":
					style = ChatColor.ITALIC;
					break;
				case "{{RESET}}":
					style = ChatColor.RESET;
					break;
				}
			} else {
				StringBuffer buff = new StringBuffer(entry);

				if (color != null) {
					buff.insert(0, color);

				}
				if (style != null) {
					buff.insert(0, style);
				}
				textmain.append(buff);
			}
		}
		return textmain.toString();
	}

	public String format(double balance) {
		DisplayFormat format = Main.getInstance().getDisplayFormat();

		StringBuilder string = new StringBuilder();

		String[] theAmount = String.valueOf(balance).split("\\.");

		DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols();

		unusualSymbols.setGroupingSeparator(',');

		DecimalFormat decimalFormat = new DecimalFormat("###,###", unusualSymbols);
		getClass();

		String name = Main.getInstance().currencyName;

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

			string.append(Main.getInstance().currencyMajorColor).append(amount).append(" {{RESET}}").append(" " + name)
					.append(" ").append(Main.getInstance().currencyMinorColor)
					.append(Long.toString(Long.parseLong(coin))).append(" {{RESET}}").append(" " + subName)
					.append("{{RESET}}");
		} else if (format == DisplayFormat.SMALL) {
			string.append(Main.getInstance().currencyMajorColor).append(amount).append(".")
					.append(Main.getInstance().currencyMinorColor).append(coin).append(" {{RESET}}").append(name);
		} else if (format == DisplayFormat.SIGN) {
			string.append(Main.getInstance().currencyMajorColor).append(Main.getInstance().getCurrencySign())
					.append(" " + amount).append(".").append(Main.getInstance().currencyMinorColor).append(coin)
					.append("{{RESET}}");
		} else if (format == DisplayFormat.SIGNFRONT) {
			string.append(Main.getInstance().currencyMajorColor).append(amount).append(".")
					.append(Main.getInstance().currencyMinorColor).append(coin).append("{{RESET}}")
					.append(" " + Main.getInstance().getCurrencySign());
		} else if (format == DisplayFormat.MAJORONLY) {
			string.append(Main.getInstance().currencyMajorColor).append(amount).append(" ").append(name)
					.append("{{RESET}}");
		}

		return string.toString();
	}

	public void writeLog(String player, LogInfo info, Cause cause, DateTime dateTime, double amount) {
		if (config.getBoolean("Logging.Enabled")) {
			MySQLEngine.saveLog(player, info, cause, dateTime, amount);
		}

	}

	public void load(String name) {
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		config = this.getConfig();

		if (!config.getBoolean("Setup")) {
			sendmessage(
					"{{DARK_RED}}Configure le plugin (pense a changer le Setup en true) puis fait /economieadmin reload",
					name);
		} else {
			// Si config setup
			sendmessage("{{GOLD}}Loading default settings", name);
			loadDefaultSettings();
			sendmessage("{{GOLD}}Default settings loaded", name);

			try {
				initialiseDatabase();
			} catch (ClassNotFoundException | SQLException e) {
				sendmessage("{{RED}}Erreur lors du chargement de la base de donné", name);
				e.printStackTrace();
			}

			sendmessage("{{GOLD}}Loading Economie Class", name);
			if (!setupEconomy()) {
				log.severe(
						String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
			startUp();
			sendmessage("{{GOLD}}Ready", name);
		}
	}
}
