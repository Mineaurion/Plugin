package com.mineaurion.EconomySpongeMaven;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;

import com.google.inject.Inject;
import com.mineaurion.EconomySpongeMaven.Mysql.MySQLEngine;
import com.mineaurion.EconomySpongeMaven.classEconomie.ACurrency;
import com.mineaurion.EconomySpongeMaven.classEconomie.AccountManager;
import com.mineaurion.EconomySpongeMaven.commands.CommandManager;
import com.mineaurion.EconomySpongeMaven.commands.InitHelp;
import com.mineaurion.EconomySpongeMaven.event.EconomyChangeEvent;
import com.mineaurion.EconomySpongeMaven.event.EventManager;

import lombok.Getter;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@Plugin(id = "aurionseconomy", name = "AurionsEconomy", version = "1.0", description = "An utility plugin", authors = {
		"greatman", "THEJean_Kevin" })
public class Main {

	@Inject
	@Getter
	private PluginContainer plugin;

	@Getter
	public static Main instance = null;

	static @Getter @Inject private Game game;

	@Inject
	@DefaultConfig(sharedRoot = false)
	private Path defaultConfig;
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path privateConfigDir;
	@Getter
	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> loader;
	@Getter
	public Path ConfigDir;

	@Getter
	private AccountManager accountManager;
	@Getter
	private EventManager eventManager = null;

	@Getter
	private MySQLEngine mysqlEngine;

	@Getter
	private Currency defaultCurrency;


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

	@Getter
	public static CommentedConfigurationNode rootNode;

	@Getter
	private UserStorageService userStorageService;

	@Listener
	public void onInitialization(GamePreInitializationEvent event) throws IOException {

		sendmessage("{{GOLD}}AurionEconomy demarre", "console");
		instance = this;
		ConfigDir = privateConfigDir;

		if (Files.notExists(defaultConfig)) {
			sendmessage("{{GOLD}}copy du fichier config", "console");
			Asset configAsset = plugin.getAsset("config.conf").get();
			configAsset.copyToFile(defaultConfig);
		}
		rootNode = loader.load();

		// Si config pas setup
		if (!rootNode.getNode("Setup").getBoolean()) {

			sendmessage(
					"{{DARK_RED}}Configure le plugin (pense a changer le Setup en true) puis fait /economieadmin reload",
					"console");

		} else {
			// Si config setup
			sendmessage("{{GOLD}}Loading default settings", "console");
			loadDefaultSettings();
			sendmessage("{{GOLD}}Default settings loaded", "console");

			initialiseDatabase();

			sendmessage("{{GOLD}}Loading Economie Class", "console");
			InitEconomie();
			startUp();
			sendmessage("{{GOLD}}Ready", "console");
		}

	}

	public void load(String player) throws IOException {
		
		if (Files.notExists(defaultConfig)) {
			sendmessage("{{GOLD}}copy du fichier config", player);
			Asset configAsset = plugin.getAsset("config.conf").get();
			configAsset.copyToFile(defaultConfig);
		}
		rootNode = loader.load();

		// Si config pas setup
		if (!rootNode.getNode("Setup").getBoolean()) {

			sendmessage(
					"{{DARK_RED}}Configure le plugin (pense a changer le Setup en true) puis fait /economieadmin reload",
					player);

		} else {
			// Si config setup
			sendmessage("{{GOLD}}Loading default settings", player);
			loadDefaultSettings();
			sendmessage("{{GOLD}}Default settings loaded", player);

			initialiseDatabase();

			sendmessage("{{GOLD}}Loading Economie Class", player);
			InitEconomie();
			startUp();
			sendmessage("{{GOLD}}Ready", player);
		}
	}

	
	@Listener
	public void init(GameInitializationEvent event) {
		sendmessage("{{GOLD}}Loading commands", "console");
		registerCommands();
		InitHelp.init();
	}

	@Listener
	public void onServerStart(GameStartedServerEvent event) {
		userStorageService = Sponge.getGame().getServiceManager().provideUnchecked(UserStorageService.class);
	}

	public void InitEconomie() {
		defaultCurrency = new ACurrency(Text.of(currencyName), Text.of(currencyName + "s"), Text.of(currencySign), 2,
				true);

		sendmessage("{{GOLD}}Loading Account Manager", "console");
		accountManager = new AccountManager(this);
		Sponge.getGame().getServiceManager().setProvider(this, EconomyService.class, accountManager);
		sendmessage("{{GOLD}}Account Manager loaded", "console");

	}

	public void startUp() {
		sendmessage("{{GOLD}}Loading listeners", "console");
		Sponge.getEventManager().registerListeners(this, new EventManager());
		this.eventManager = new EventManager();
	}

	private void loadDefaultSettings() {
		String value = rootNode.getNode("QuickSetup", "DisplayMode").getString().toUpperCase();
		if (value != null) {
			this.displayFormat = DisplayFormat.valueOf(value);
		} else {
			rootNode.getNode("QuickSetup", "DisplayMode").setValue("LONG");
			this.displayFormat = DisplayFormat.LONG;
		}

		this.holdings = Double.parseDouble(rootNode.getNode("QuickSetup", "StartBalance").getString());

		this.currencyMinor = rootNode.getNode("QuickSetup", "Currency", "Minor").getString();
		this.currencyName = rootNode.getNode("QuickSetup", "Currency", "Name").getString();
		this.currencySign = rootNode.getNode("QuickSetup", "Currency", "Sign").getString();

	}

	private void initialiseDatabase() {
		
			sendmessage("{{GOLD}}Loading DataBase", "console");
			mysqlEngine = new MySQLEngine(this);
			DateTime dateTime = DateTime.now(DateTimeZone.forID("Europe/Paris"));
			
			MySQLEngine.clearLog(dateTime.minusMonths(1));
			sendmessage("{{GOLD}}DataBase loaded", "console");
		
	}

	private void registerCommands() {
		CommandManager commandManager = new CommandManager();

		Sponge.getCommandManager().register(this, commandManager.cmdmoney, "money");
		Sponge.getCommandManager().register(this, commandManager.Economieadmin, "economieadmin");
		Sponge.getCommandManager().register(this, commandManager.pay, "pay");

	}

	public static void sendmessage(String message, String sender) {
		if (sender.equals("console")||sender.equals("Server")) {
			Sponge.getGame().getServer().getConsole().sendMessage(addColor(message));
		} else {
			Sponge.getGame().getServer().getPlayer(sender).get().sendMessage(addColor(message));
		}
	}

	private static Text addColor(String message) {
		message = prefix + message;
		Text.Builder textMain = Text.builder();
		Matcher m = Pattern.compile("(\\{\\{([^\\{\\}]+)\\}\\}|[^\\{\\}]+)").matcher(message);
		TextColor color = null;
		TextStyle.Base style = null;
		while (m.find()) {

			String entry = m.group();
			if (entry.contains("{{")) {
				color = null;
				style = null;
				switch (entry) {
				case "{{BLACK}}":
					color = TextColors.BLACK;
					break;
				case "{{DARK_BLUE}}":
					color = TextColors.DARK_BLUE;
					break;
				case "{{DARK_GREEN}}":
					color = TextColors.DARK_GREEN;
					break;
				case "{{DARK_CYAN}}":
					color = TextColors.DARK_AQUA;
					break;
				case "{{DARK_RED}}":
					color = TextColors.DARK_RED;
					break;
				case "{{PURPLE}}":
					color = TextColors.DARK_PURPLE;
					break;
				case "{{GOLD}}":
					color = TextColors.GOLD;
					break;
				case "{{GRAY}}":
					color = TextColors.GRAY;
					break;
				case "{{DARK_GRAY}}":
					color = TextColors.DARK_GRAY;
					break;
				case "{{BLUE}}":
					color = TextColors.AQUA;
					break;
				case "{{BRIGHT_GREEN}}":
					color = TextColors.GREEN;
					break;
				case "{{CYAN}}":
					color = TextColors.AQUA;
					break;
				case "{{RED}}":
					color = TextColors.RED;
					break;
				case "{{LIGHT_PURPLE}}":
					color = TextColors.LIGHT_PURPLE;
					break;
				case "{{YELLOW}}":
					color = TextColors.YELLOW;
					break;
				case "{{WHITE}}":
					color = TextColors.WHITE;
					break;
				case "{{OBFUSCATED}}":
					style = TextStyles.OBFUSCATED;
					break;
				case "{{BOLD}}":
					style = TextStyles.BOLD;
					break;
				case "{{STRIKETHROUGH}}":
					style = TextStyles.STRIKETHROUGH;
					break;
				case "{{UNDERLINE}}":
					style = TextStyles.UNDERLINE;
					break;
				case "{{ITALIC}}":
					style = TextStyles.ITALIC;
					break;
				case "{{RESET}}":
					style = TextStyles.RESET;
					break;
				}
			} else {
				Text.Builder text = Text.builder(entry);

				if (color != null) {
					text.color(color);
				}
				if (style != null) {
					text.style(style);
				}
				textMain.append(text.build());
			}
		}
		return textMain.build();
	}

	public static void writeLog(String player, LogInfo info, org.spongepowered.api.event.cause.Cause cause, DateTime dateTime,
			double amount) {
		if (rootNode.getNode("Logging", "Enabled").getBoolean()) {
			MySQLEngine.saveLog(player, info, cause, dateTime, amount);
		}
	}

	public void throwEvent(EconomyChangeEvent event) {
		getGame().getEventManager().post(event);

	}

	public static boolean isValidDouble(String number) {
		boolean valid = false;
		if (isDouble(number) && isPositive(Double.parseDouble(number))) {
			valid = true;
		}
		return valid;
	}

	public static boolean isDouble(String number) {
		boolean result = false;
		if (number != null) {
			try {
				Double.parseDouble(number);
				result = true;
			} catch (NumberFormatException e) {
			}
		}
		return result;
	}

	public static boolean isPositive(double number) {
		return number >= 0.00;
	}

	public Player getPlayer(UUID uuid) {
		return Sponge.getGame().getServer().getPlayer(uuid).get();
	}

}
