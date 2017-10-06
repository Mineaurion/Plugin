package com.mineaurion.EconomySpongeMaven.commands;

import com.mineaurion.help.HELP.Argument;
import com.mineaurion.help.HELP.Help;
import com.mineaurion.help.HELP.Usage;

public class InitHelp {

	public static void init() {
		Usage usagebalance = new Usage(Argument.of("[player]","Nom du joueur a qui tu veut savoir la balance"));
		Help balance = new Help("money balance","balance","Get the balance of target player")
				.setPermission("economie.money.balance")
				.setUsage(usagebalance)
				.addExample("/money balance THEJean_Kevin");
		Help.register(balance);
		
		Usage usagegive = new Usage(Argument.of("<player>","Nom du joueur � qui tu veut modifier la balance"))
				.addArgument(Argument.of("<montant>","montant � donn�"));
		Help give = new Help("money give","give","Donne le montant au joueur")
				.setPermission("economie.money.admin")
				.setUsage(usagegive)
				.addExample("/money give THEJean_Kevin 10");
		Help.register(give);
		
		Usage usagetake = new Usage(Argument.of("<player>","Nom du joueur � qui tu veut modifier la balance"))
				.addArgument(Argument.of("<montant>","montant � donn�"));
		Help take = new Help("money take","take","Retire le montant au joueur")
				.setPermission("economie.money.admin")
				.setUsage(usagetake)
				.addExample("/money take THEJean_Kevin 10");
		Help.register(take);
		
		Usage usageset = new Usage(Argument.of("<player>","Nom du joueur � qui tu veut modifier la balance"))
				.addArgument(Argument.of("<montant>","montant � donn�"));
		Help set = new Help("money set","set","Defini la nouvelle balance du joueur")
				.setPermission("economie.money.admin")
				.setUsage(usageset)
				.addExample("/money set THEJean_Kevin 10");
		Help.register(set);
		
		Usage usageinfinite = new Usage(Argument.of("<player>","Nom du joueur � qui tu veut modifier la balance"));
		Help infinite = new Help("money infinite","infinite","Done une balance illimit�")
				.setPermission("economie.money.admin")
				.setUsage(usageinfinite)
				.addExample("/money infinite THEJean_Kevin");
		Help.register(infinite);
		
		Usage usagelog = new Usage(Argument.of("[player]","Nom du joueur � qui tu veut voir les transaction")).addArgument(Argument.of("[page]","page"));
		Help log = new Help("money log","log","Voit les transactions")
		.setPermission("economie.money.admin")
		.setUsage(usagelog)
		.addExample("/money log THEJean_Kevin");
		Help.register(log);
		
		Usage usagetop = new Usage(Argument.of("",""));
		Help top = new Help("money top","top","voit le top en money")
				.setPermission("economie.money.top")
				.setUsage(usagetop)
				.addExample("/money top");
		Help.register(top);
		
		Help money = new Help("money","money","Money command")
				.setPermission("economie.money")
				.addChild(balance)
				.addChild(give)
				.addChild(infinite)
				.addChild(log)
				.addChild(set)
				.addChild(take)
				.addChild(top);
		Help.register(money);
		
		Usage usagepay = new Usage(Argument.of("<player>","Nom du joueur que tu veut payer"))
				.addArgument(Argument.of("<montant>","montant � donn�"));
		
		Help pay = new Help("pay","pay","command pour payer un joueurs")
				.setPermission("economie.pay")
				.setUsage(usagepay)
				.addExample("/pay THEJean_Kevin 10");
		Help.register(pay);
		
		Usage usagecurrency = new Usage(Argument.of("(minor,name,Sign,DisplayMode (LONG, SIGN, SIGNFRONT, SMALL, MAJORONLY)","argument valable"));
		Help currency = new Help("economieadmin currency","currency","Setup currency config")
				.setPermission("economie.setup")
				.setUsage(usagecurrency)
				.addExample("/economieadmin currency name aurions");
		Help.register(currency);
		
		Usage usagedatabase = new Usage(Argument.of("(address,port,Username,Password,db,prefix)","argument valable"));
		Help database = new Help("economieadmin database","database","Setup database config")
				.setPermission("economie.setup")
				.setUsage(usagedatabase)
				.addExample("/economieadmin database address mineaurion.com");
		Help.register(database);
		
		//Usage usageconvert = new Usage(Argument.of("(address,port,Username,Password,db,prefix)","argument valable"));
		/*Help convert = new Help("economieadmin convert","convert","Setup convert config")
				.setPermission("economie.setup")
				.setUsage(usageconvert)
				.addExample("/economieadmin convert address mineaurion.com");
		Help.register(convert);*/
		
		Usage usageReload = new Usage(Argument.of("",""));
		Help reload = new Help("economieadmin reload","reload","reload config")
				.setPermission("economie.reload")
				.setUsage(usageReload)
				.addExample("/economieadmin reload");
		Help.register(reload);
		
		Usage usageCheck = new Usage(Argument.of("<player>","Nom du joueur que tu veut check"))
				.addArgument(Argument.of("<montant>","montant � check"))
				.addArgument(Argument.of("<command>","list des commands"));
		Help check = new Help("economieadmin check","check","check si le joueus a l'argent pour executer les commands")
				.setPermission("economie.check")
				.setUsage(usageCheck)
				.addExample("/economie check THEJean_Kevin 10 /say bonjour");
		Help.register(check);
		
		Help economieadmin = new Help("economieadmin","economieadmin","Command admin")
				.setPermission("economie.admin")
				.addChild(currency)
				.addChild(database)
				//.addChild(convert)
				.addChild(reload)
				.addChild(check);
		Help.register(economieadmin);
		
		
	}

}
