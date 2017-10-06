package com.mineaurion.tjk.MinecraftToDiscord;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import com.mrpowergamerbr.temmiewebhook.DiscordMessage;

public class wehookcmd implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String message = args.<String>getOne("message").get();
		message.replace("<player>", src.getName());
		Text msg = Sponges.addColor(message);
		message = msg.toPlain();
		
		DiscordMessage dm = DiscordMessage.builder()
				.username("Minecraft Request") // Player's name
				.content(message) // Player's message
				.avatarUrl("https://www.residentadvisor.net/images/reviews/2012/specialrequest-ep1ep2.jpg") // Avatar
				.build();
		
		Sponges.temmie.sendMessage(dm);
		
		return CommandResult.success();
	}

}
