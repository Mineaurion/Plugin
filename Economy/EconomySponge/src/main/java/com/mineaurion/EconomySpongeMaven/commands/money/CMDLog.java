package com.mineaurion.EconomySpongeMaven.commands.money;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList.Builder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;

import com.mineaurion.EconomySpongeMaven.Main;
import com.mineaurion.EconomySpongeMaven.Mysql.MySQLEngine;

public class CMDLog implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		String querry = MySQLEngine.logTable.selectEntry;
		if (args.<User>getOne("player").isPresent()) {
			User player = args.<User>getOne("player").get();
			querry = querry + " WHERE `PLAYER`=" + player.getName();
		}
		querry = querry + " ORDER BY `ID` DESC";

		ResultSet res = MySQLEngine.getLog(querry);
		if (res != null) {
			Optional<PaginationService> paginationService = Sponge.getServiceManager().provide(PaginationService.class);
			if (paginationService.isPresent()) {
				Builder page = paginationService.get().builder();
				page.header(Text.of("PLAYER : AMOUNT - TYPE - TIME"))
				.title(Text.of("LOG"))
				.padding(Text.of("-"));
				
				try {
					while (res.next()) {
						// PLAYER : AMOUNT - TYPE - TIME
						String info = res.getString("PLAYER") + " : " + res.getDouble("AMOUNT") + " - "+res.getString("TYPE") + " - "
								+ new SimpleDateFormat("HH:mm:ss-dd-MM-yyyy").format(res.getTimestamp("TIME"));
						page.contents(Text.of(info));
					}
					res.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				page.linesPerPage(10)
				.sendTo(src);
				return CommandResult.success();
			} else {
				String info = null;
				try {
					
					while (res.next()) {
						// PLAYER : AMOUNT - TYPE - TIME
						info = res.getString("PLAYER") + " : " + res.getDouble("AMOUNT") + " - "
								+ new SimpleDateFormat("HH:mm:ss-dd-MM-yyyy").format(res.getDate("TIME").getTime());
						src.sendMessage(Text.of(info));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return CommandResult.success();
			}
		}else{
			Main.sendmessage("{{RED]}}Une erreur est survenu", src.getName());
			return CommandResult.success();
		}
	}

}
