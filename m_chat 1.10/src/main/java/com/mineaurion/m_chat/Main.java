package com.mineaurion.m_chat;

import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.serializer.TextSerializers;

import me.rojo8399.placeholderapi.PlaceholderService;





@Plugin(id = "m_chat", name = "m_chat", version = "1.0", description = "An utility plugin", authors = {
		"THEJean_Kevin" })
public class Main implements CommandExecutor {
	
	public CommandSpec cmd = CommandSpec.builder()
			.permission("addons.chat")
			.arguments(GenericArguments.string(Text.of("player")),GenericArguments.remainingJoinedStrings(Text.of("message")))
			.executor(this)
			.build();
	
	@Listener
	public void onInitialization(GamePreInitializationEvent event){
		
	Sponge.getCommandManager().register(this, cmd, "m_chat");
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String username = args.<String>getOne("player").get();
		String text = args.<String>getOne("message").get();
		Text message = TextSerializers.FORMATTING_CODE.deserialize(text);
		if(Sponge.getPluginManager().getPlugin("placeholderapi").isPresent()){
			 Optional<PlaceholderService> service = Sponge.getGame().getServiceManager().provide(PlaceholderService.class);
			 if (service.isPresent()){
				 PlaceholderService placeholderService = service.get();
				 username = placeholderService.replacePlaceholders(username, src, placeholderService).toPlain();
				 message = placeholderService.replacePlaceholders(message, src, placeholderService);
			 }
		}
		
		if(username.equalsIgnoreCase("@@")){
			MessageChannel messageChannel = MessageChannel.TO_ALL;
			messageChannel.send(message);
		}else{
			
		Optional<Player> player = Sponge.getServer().getPlayer(username);
			if(player.isPresent())
			{
			player.get().sendMessage(message);
			}
		}
		return CommandResult.success();
	}
}

	