package com.mineaurion.tjk.MinecraftToDiscord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;

import com.google.inject.Inject;
import com.mrpowergamerbr.temmiewebhook.TemmieWebhook;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@Plugin(id = Sponges.ID, name = "MinecraftToDiscord", version = "1.0", description = "An utility plugin", authors = {"THEJean_Kevin" })
public class Sponges {
	public final static String ID = "minecrafttodiscord";
	
	@Inject
	Game game;
	
	public static Sponges instance = null;
	
	@Inject private PluginContainer plugin;
	
	@Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;
	
    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> loader;
	
	
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path privateConfigDir;
    public Path ConfigDir;
    public CommentedConfigurationNode rootNode;
    
	
	public static TemmieWebhook temmie;
	
	@Listener
	public void onInitialization(GameInitializationEvent event) throws IOException{
		instance = this;
		
		
		
		
		setup("console");
		
		CommandSpec discordreload = CommandSpec.builder()
			    .description(Text.of("MinecraftToDiscord reload"))
			    .permission("MinecraftToDiscord")
			    .executor(new reloadcmd())
			    .build();
		
		CommandSpec webhook = CommandSpec.builder()
				.permission("MinecraftToDiscord")
				.arguments(GenericArguments.remainingJoinedStrings(Text.of("message")))
				.executor(new wehookcmd())
				.build();
		
		Sponge.getCommandManager().register(this, discordreload, "discordreload");
		Sponge.getCommandManager().register(this, webhook, "webhook");	
	}
	
	
	public void setup(String player) throws IOException {
		if (Files.notExists(defaultConfig)) {
			sendmessage("{{GOLD}}copy du fichier config", "console");
			Asset configAsset = plugin.getAsset("config.conf").get();
			configAsset.copyToFile(defaultConfig);
		}
		
		rootNode = loader.load();
		
		if(rootNode.getNode("link").getString().equalsIgnoreCase("NONE")){
			sendmessage("Hey, is this your first time using MinecraftToDiscord?",player);
			sendmessage("If yes, then you need to add your Webhook URL to the",player);
			sendmessage("config.yml!",player);
			sendmessage("",player);
			sendmessage("...so go there and do that.",player);
			sendmessage("After doing that, use /discordreload",player);
			return;
		}
		
		temmie = new TemmieWebhook(rootNode.getNode("link").getString());
	}
	
	
	public static void sendmessage(String message, String sender) {
		if (sender.equals("console")||sender.equals("Server")) {
			Sponge.getGame().getServer().getConsole().sendMessage(addColor(message));
		} else {
			Sponge.getGame().getServer().getPlayer(sender).get().sendMessage(addColor(message));
		}
	}
	
	static Text addColor(String message) {
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


	public static Sponges getInstance() {
		// TODO Auto-generated method stub
		return instance;
	}
	
	
	
}
