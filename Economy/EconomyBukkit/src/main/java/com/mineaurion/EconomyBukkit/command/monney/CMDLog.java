package com.mineaurion.EconomyBukkit.command.monney;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import com.mineaurion.EconomyBukkit.Main;
import com.mineaurion.EconomyBukkit.PaginatedResult;
import com.mineaurion.EconomyBukkit.Mysql.MySQLEngine;

public class CMDLog {

	@SuppressWarnings({ })
	public CMDLog(CommandSender sender, String[] args) throws NumberFormatException, CommandException, SQLException {
		String querry = MySQLEngine.logTable.selectEntry;
		if (args.length == 3) {
			querry = querry + " WHERE `player`=" + args[2]+" ";
		}
		querry = querry + "ORDER BY `ID` DESC";
		ResultSet res = MySQLEngine.getLog(querry);		
		if (res.isBeforeFirst()) {
			Map<Integer, String> resultat = new HashMap<>();
			
			try {
				int i =0;
				while (res.next()) {
					i++;
					String info = "{{CYAN}}"+res.getString("PLAYER") + " : {{RED}}" + res.getDouble("AMOUNT") + " - {{YELLOW}}"+res.getString("TYPE") + " - {{GRAY}}"
							+ new SimpleDateFormat("HH:mm:ss-dd-MM-yyyy").format(res.getTimestamp("TIME"));
					resultat.put(i,info);
				}
				res.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			int page = 1;
			if(args.length==2){
				page=Integer.parseInt(args[1]);
			}
			new PaginatedResult<Map.Entry<Integer, String>>("PLAYER : AMOUNT - TYPE - TIME") {
				@Override
				public String format(Map.Entry<Integer, String> entry) {
					return entry.getValue();
				}
			}.display(sender, resultat.entrySet(), page);
		}else{
			Main.sendmessage("{{RED}}les logs sont vide", sender.getName());
		}
	}

}