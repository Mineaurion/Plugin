// 
// Decompiled by Procyon v0.5.30
// 

package com.mineaurion.tjk.GAL;

import java.util.Date;
import java.text.DateFormat;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.configuration.ConfigurationSection;
import java.io.InputStream;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.common.collect.Table;
import com.google.common.collect.Multimap;
import org.bukkit.configuration.InvalidConfigurationException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.EventHandler;
import java.util.Iterator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import java.io.IOException;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import com.google.common.collect.Multimaps;
import com.google.common.collect.ArrayListMultimap;
import java.io.FileWriter;
import java.util.logging.Logger;
import java.security.SecureRandom;
import java.util.List;
import org.bukkit.scheduler.BukkitTask;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import com.vexsoftware.votifier.model.Vote;
import java.util.concurrent.BlockingQueue;
import com.google.common.collect.ListMultimap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class GAL extends JavaPlugin implements Listener
{
    public GAL plugin;
    public static GAL p;
    public YamlConfiguration config;
    public ListMultimap<VoteType, GALVote> galVote;
    public ListMultimap<VoteType, GALReward> queuedVotes;
    public BlockingQueue<Vote> sqlList;
    public ConcurrentMap<String, Integer> voteTotals;
    public ConcurrentMap<String, Long> lastVoted;
    public ConcurrentMap<String, String> users;
    public Map<String, String> rewardMessages;
    public Map<String, Long> lastReceived;
    public BukkitTask rewardQueue;
    public BukkitTask voteReminder;
    public List<String> voteMessage;
    public List<String> remindMessage;
    public List<String> joinMessage;
    public SecureRandom random;
    public Logger log;
    public DB db;
    public Commands commands;
    public String dbMode;
    public String dbFile;
    public String dbHost;
    public int dbPort;
    public String dbUser;
    public String dbPass;
    public String dbName;
    public String dbPrefix;
    public String rewardFormat;
    public String votetopFormat;
    public List<String> rewardHeader;
    public List<String> votetopHeader;
    public List<String> rewardFooter;
    public List<String> blockedWorlds;
    public boolean voteCommand;
    public boolean rewardCommand;
    public boolean onJoin;
    public boolean voteRemind;
    public int remindSeconds;
    public int rateLimit;
    public boolean luckyVote;
    public boolean permVote;
    public boolean cumulativeVote;
    public boolean onlineOnly;
    public boolean broadcastQueue;
    public boolean broadcastRecent;
    public boolean broadcastOffline;
    public boolean logEnabled;
    public boolean getOfflinePlayers;
    private FileWriter fw;
    
    public GAL() {
        this.galVote = (ListMultimap<VoteType, GALVote>)Multimaps.synchronizedListMultimap((ListMultimap)ArrayListMultimap.create());
        this.queuedVotes = (ListMultimap<VoteType, GALReward>)Multimaps.synchronizedListMultimap((ListMultimap)ArrayListMultimap.create());
        this.sqlList = new LinkedBlockingQueue<Vote>();
        this.voteTotals = new ConcurrentHashMap<String, Integer>();
        this.lastVoted = new ConcurrentHashMap<String, Long>();
        this.users = new ConcurrentHashMap<String, String>();
        this.rewardMessages = new HashMap<String, String>();
        this.lastReceived = new HashMap<String, Long>();
        this.voteReminder = null;
        this.voteMessage = new ArrayList<String>(Arrays.asList("{GOLD}-----------------------------------------------------", "Vote for us every day for in game rewards and extras", "{GOLD}-----------------------------------------------------", "{AQUA}You currently have {GREEN}{votes} Votes"));
        this.remindMessage = new ArrayList<String>(Arrays.asList("{GOLD}-----------------------------------------------------", "You have not voted recently, please vote to support the server", "{GOLD}-----------------------------------------------------", "{AQUA}You currently have {GREEN}{votes} Votes"));
        this.joinMessage = new ArrayList<String>(Arrays.asList("{GOLD}-----------------------------------------------------", "Vote for us every day for in game rewards and extras", "{GOLD}-----------------------------------------------------", "{AQUA}You currently have {GREEN}{votes} Votes"));
        this.random = new SecureRandom();
        this.dbMode = "sqlite";
        this.dbFile = "GAL.db";
        this.dbHost = "localhost";
        this.dbPort = 3306;
        this.dbUser = "root";
        this.dbPass = "";
        this.dbName = "GAL";
        this.dbPrefix = "";
        this.rewardFormat = "{GREEN}{TOTAL} Votes {GRAY}- {AQUA}{REWARD}";
        this.votetopFormat = "{POSITION}. {GREEN}{username} - {WHITE}{TOTAL}";
        this.rewardHeader = Arrays.asList("{GOLD}---------------- {WHITE}[ {DARK_AQUA}Rewards{WHITE} ] {GOLD}----------------");
        this.votetopHeader = Arrays.asList("{GOLD}---------------- {WHITE}[ {DARK_AQUA}Top Voters{WHITE} ] {GOLD}----------------");
        this.rewardFooter = Arrays.asList("{AQUA}You currently have {GREEN}{votes} Votes");
        this.blockedWorlds = new ArrayList<String>();
        this.voteCommand = true;
        this.rewardCommand = true;
        this.onJoin = true;
        this.voteRemind = true;
        this.remindSeconds = 300;
        this.rateLimit = 10;
        this.luckyVote = false;
        this.permVote = false;
        this.cumulativeVote = false;
        this.onlineOnly = true;
        this.broadcastQueue = true;
        this.broadcastRecent = true;
        this.broadcastOffline = false;
        this.logEnabled = false;
        this.getOfflinePlayers = false;
        this.fw = null;
    }
    
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
        this.plugin = this;
        GAL.p = this;
        this.db = new DB(this);
        this.commands = new Commands(this);
        this.log = this.getLogger();
        this.reload();
        this.getCommand("gal").setExecutor((CommandExecutor)this.commands);
        this.getCommand("vote").setExecutor((CommandExecutor)this.commands);
        this.getCommand("rewards").setExecutor((CommandExecutor)this.commands);
        this.getCommand("fakevote").setExecutor((CommandExecutor)this.commands);
        this.getCommand("votetop").setExecutor((CommandExecutor)this.commands);
        if (this.getOfflinePlayers) {
            this.getServer().getScheduler().runTaskAsynchronously((Plugin)this, (Runnable)new Runnable() {
                @Override
                public void run() {
                    final long startTime = System.nanoTime();
                    final OfflinePlayer[] ops = GAL.this.getServer().getOfflinePlayers();
                    OfflinePlayer[] array;
                    for (int length = (array = ops).length, i = 0; i < length; ++i) {
                        final OfflinePlayer op = array[i];
                        if (op.getName().length() <= 16) {
                            GAL.this.plugin.users.put(op.getName().toLowerCase(), op.getName());
                        }
                    }
                    final long duration = System.nanoTime() - startTime;
                    GAL.this.log.info("Took " + duration / 1000000L + "ms to get " + ops.length + " offline players.");
                }
            });
        }
        try {
            final Metrics metrics = new Metrics((Plugin)this);
            metrics.start();
        }
        catch (IOException ex) {}
        this.log.info(String.valueOf(this.getDescription().getFullName()) + " Enabled");
    }
    
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        this.getServer().getScheduler().runTaskAsynchronously((Plugin)this, (Runnable)new Runnable() {
            @Override
            public void run() {
                if (!GAL.this.voteTotals.containsKey(p.getName().toLowerCase())) {
                    final int votes = GAL.this.db.getVotes(p.getName().toLowerCase());
                    GAL.this.plugin.log.info("Player: " + p.getName() + " has " + votes + " votes");
                    GAL.this.plugin.voteTotals.put(p.getName().toLowerCase(), votes);
                }
                else {
                    GAL.this.log.info("Player: " + p.getName() + " has " + GAL.this.voteTotals.get(p.getName().toLowerCase()) + " votes");
                }
                if (GAL.this.plugin.users.containsKey(p.getName().toLowerCase()) && !GAL.this.plugin.users.get(p.getName().toLowerCase()).equals(p.getName())) {
                    GAL.this.plugin.users.put(p.getName().toLowerCase(), p.getName());
                    GAL.this.plugin.db.setVotes(p.getName(), GAL.this.plugin.voteTotals.get(p.getName().toLowerCase()), false);
                }
                if (GAL.this.blockedWorlds.contains(p.getWorld().getName())) {
                    return;
                }
                final int queued = GAL.this.plugin.processQueue(p.getName());
                if (queued > 0) {
                    GAL.this.plugin.log.info("Player: " + p.getName() + " has " + queued + " queued votes");
                }
            }
        });
        if (this.onJoin) {
            this.getServer().getScheduler().runTaskLaterAsynchronously((Plugin)this, (Runnable)new Runnable() {
                @Override
                public void run() {
                    for (final String message : GAL.this.joinMessage) {
                        p.sendMessage(GAL.this.formatMessage(message, (CommandSender)p));
                    }
                }
            }, 20L);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChangeWorld(final PlayerChangedWorldEvent event) {
        final Player p = event.getPlayer();
        if (this.blockedWorlds.contains(p.getWorld().getName())) {
            return;
        }
        this.processQueue(p.getName());
    }
    
    public int processQueue(final String name) {
        final List<GALReward> playerQueue = new ArrayList<GALReward>();
        synchronized (this.queuedVotes) {
            final Iterator<Map.Entry<VoteType, GALReward>> i = this.queuedVotes.entries().iterator();
            while (i.hasNext()) {
                final Map.Entry<VoteType, GALReward> entry = i.next();
                if (entry.getValue().vote.getUsername().equalsIgnoreCase(name)) {
                    this.processVote(entry.getValue().vote, true);
                    playerQueue.add(entry.getValue());
                    i.remove();
                }
            }
        }
        // monitorexit(this.queuedVotes)
        if (!playerQueue.isEmpty()) {
            final int size = playerQueue.size();
            playerQueue.clear();
            this.getServer().getScheduler().runTaskAsynchronously((Plugin)this, (Runnable)new Runnable() {
                @Override
                public void run() {
                    GAL.this.plugin.db.modifyQuery("DELETE FROM `" + GAL.this.plugin.dbPrefix + "GALQueue` WHERE LOWER(`IGN`) = '" + name.toLowerCase() + "'");
                }
            });
            return size;
        }
        return 0;
    }
    
    public void reload() {
        this.galVote.clear();
        this.voteMessage.clear();
        this.remindMessage.clear();
        this.joinMessage.clear();
        this.rewardMessages.clear();
        if (this.voteReminder != null) {
            this.voteReminder.cancel();
        }
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }
        final File file = new File(this.getDataFolder(), "config.yml");
        if (!file.exists()) {
            final InputStream in = GAL.class.getResourceAsStream("/config.yml");
            if (in != null) {
                try {
                    final FileOutputStream out = new FileOutputStream(file);
                    final byte[] buffer = new byte[8192];
                    int length = 0;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                }
                catch (Exception ex2) {}
            }
        }
        this.config = new YamlConfiguration();
        this.config.options().pathSeparator('/');
        try {
            this.config.load(file);
        }
        catch (FileNotFoundException ex3) {}
        catch (IOException ex4) {}
        catch (InvalidConfigurationException ex) {
            this.log.severe("############################################");
            this.log.severe("Invalid config.yml, please check for errors");
            this.log.severe("at http://yaml-online-parser.appspot.com/");
            this.log.severe("");
            this.log.severe("Type \"/gal reload\" to reload the config");
            this.log.severe("############################################");
        }
        this.blockedWorlds = (List<String>)this.config.getStringList("blocked");
        this.voteMessage = (List<String>)this.config.getStringList("votemessage");
        this.remindMessage = (List<String>)this.config.getStringList("remindmessage");
        this.joinMessage = (List<String>)this.config.getStringList("joinmessage");
        this.voteCommand = this.config.getBoolean("settings/votecommand", true);
        this.rewardCommand = this.config.getBoolean("settings/rewardcommand", true);
        this.onJoin = this.config.getBoolean("settings/joinmessage", true);
        this.voteRemind = this.config.getBoolean("settings/voteremind", true);
        this.remindSeconds = this.config.getInt("settings/remindseconds", 300);
        this.rateLimit = this.config.getInt("settings/ratelimit", 10);
        this.luckyVote = this.config.getBoolean("settings/luckyvote", false);
        this.permVote = this.config.getBoolean("settings/permvote", false);
        this.cumulativeVote = this.config.getBoolean("settings/cumulative", false);
        this.onlineOnly = this.config.getBoolean("settings/onlineonly", true);
        this.getOfflinePlayers = this.config.getBoolean("settings/getofflineplayers", false);
        this.broadcastQueue = this.config.getBoolean("settings/broadcastqueue", true);
        this.broadcastRecent = this.config.getBoolean("settings/broadcastrecent", true);
        this.broadcastOffline = this.config.getBoolean("settings/broadcastoffline", false);
        this.logEnabled = this.config.getBoolean("settings/logfile", false);
        this.rewardFormat = this.config.getString("rewardformat", "{GREEN}{TOTAL} Votes {GRAY}- {AQUA}{REWARD}");
        this.votetopFormat = this.config.getString("votetopformat", "{POSITION}. {GREEN}{username} - {WHITE}{TOTAL}");
        this.rewardHeader = (List<String>)this.config.getStringList("rewardheader");
        this.votetopHeader = (List<String>)this.config.getStringList("votetopheader");
        this.rewardFooter = (List<String>)this.config.getStringList("rewardfooter");
        this.dbMode = this.config.getString("settings/dbMode", "sqlite");
        this.dbFile = this.config.getString("settings/dbFile", "GAL.db");
        this.dbHost = this.config.getString("settings/dbHost", "localhost");
        this.dbPort = this.config.getInt("settings/dbPort", 3306);
        this.dbUser = this.config.getString("settings/dbUser", "root");
        this.dbPass = this.config.getString("settings/dbPass", "");
        this.dbName = this.config.getString("settings/dbName", "GAL");
        this.dbPrefix = this.config.getString("settings/dbPrefix", "");
        final ConfigurationSection cs = this.config.getConfigurationSection("services");
        if (cs != null) {
            for (final String serviceName : cs.getKeys(false)) {
                final ConfigurationSection serviceConfig = cs.getConfigurationSection(serviceName);
                if (serviceConfig != null) {
                    this.galVote.put(VoteType.NORMAL, new GALVote(serviceName, serviceConfig.getString("playermessage", ""), serviceConfig.getString("broadcast", ""), serviceConfig.getStringList("commands")));
                    
                }
            }
        }
        if (this.luckyVote) {
            final ConfigurationSection luck = this.config.getConfigurationSection("luckyvotes");
            if (luck != null) {
                for (final String luckAmount : luck.getKeys(false)) {
                    final ConfigurationSection luckConfig = luck.getConfigurationSection(luckAmount);
                    if (luckConfig != null) {
                        this.galVote.put(VoteType.LUCKY, new GALVote(luckAmount, luckConfig.getString("playermessage", ""), luckConfig.getString("broadcast", ""), luckConfig.getStringList("commands")));
                    }
                }
            }
        }
        final ConfigurationSection ps = this.config.getConfigurationSection("perms");
        if (ps != null) {
            for (String permName : ps.getKeys(false)) {
                final ConfigurationSection permConfig = ps.getConfigurationSection(permName);
                if (permConfig != null) {
                    permName = permName.toLowerCase();
                    this.galVote.put(VoteType.PERMISSION, new GALVote(permName, permConfig.getString("playermessage", ""), permConfig.getString("broadcast", ""), permConfig.getStringList("commands")));
                }
            }
        }
        final ConfigurationSection total = this.config.getConfigurationSection("cumulative");
        if (total != null) {
            for (final String totalName : total.getKeys(false)) {
                final ConfigurationSection totalConfig = total.getConfigurationSection(totalName);
                if (totalConfig != null) {
                    this.galVote.put(VoteType.CUMULATIVE, new GALVote(totalName, totalConfig.getString("playermessage", ""), totalConfig.getString("broadcast", ""), totalConfig.getStringList("commands")));
                    this.rewardMessages.put(totalName, totalConfig.getString("rewardmessage"));
                }
            }
        }
        this.getServer().getScheduler().runTaskAsynchronously((Plugin)this, (Runnable)new Runnable() {
            @Override
            public void run() {
                GAL.this.db.initConnection();
                GAL.this.db.createTables();
                GAL.this.queuedVotes.clear();
                GAL.this.log.info("Loading queued votes");
                GAL.this.queuedVotes.putAll((Multimap)GAL.this.db.getQueuedVotes());
                GAL.this.log.info("Loaded " + GAL.this.queuedVotes.size() + " queued votes");
                final Table<String, Integer, Long> totals = GAL.this.db.getTotals();
                for (final Table.Cell<String, Integer, Long> cell : totals.cellSet()) {
                    final String user = (String)cell.getRowKey();
                    GAL.this.voteTotals.put(user.toLowerCase(), (Integer)cell.getColumnKey());
                    GAL.this.lastVoted.put(user.toLowerCase(), (Long)cell.getValue());
                    if (!GAL.this.users.containsKey(user.toLowerCase())) {
                        GAL.this.users.put(user.toLowerCase(), user);
                    }
                }
            }
        });
        if (this.voteRemind) {
            this.voteReminder = new BukkitRunnable() {
                public void run() {
                    Player[] getOnlinePlayers;
                    for (int length = (getOnlinePlayers = GAL.this.getServer().getOnlinePlayers().toArray(new Player[GAL.this.getServer().getOnlinePlayers().size()])).length, i = 0; i < length; ++i) {
                        final Player player = getOnlinePlayers[i];
                        final String name = player.getName().toLowerCase();
                        if (!GAL.this.lastVoted.containsKey(name) || GAL.this.lastVoted.get(name) <= System.currentTimeMillis() - 86400000L) {
                            if (!player.hasPermission("gal.noremind")) {
                                for (final String message : GAL.this.remindMessage) {
                                    player.sendMessage(GAL.this.formatMessage(message, (CommandSender)player));
                                }
                            }
                        }
                    }
                }
            }.runTaskTimer((Plugin)this, 20L, this.remindSeconds * 20L);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onVotifierEvent(final VotifierEvent event) {
        final Vote vote = event.getVote();
        this.log(vote);
        if (vote.getUsername() != null && vote.getUsername().length() > 0) {
            if (!vote.getUsername().trim().matches("[A-Za-z0-9-_]+")) {
                this.log.warning("Vote received for invalid username: " + vote.getUsername());
                return;
            }
            final String servicePair = String.valueOf(vote.getServiceName()) + "\u0000" + vote.getUsername();
            if (this.lastReceived.containsKey(servicePair) && System.currentTimeMillis() - this.lastReceived.get(servicePair) < this.rateLimit * 1000) {
                this.log.warning("Vote received too fast (duplicate vote?) from: " + vote.getServiceName() + " for username: " + vote.getUsername());
                return;
            }
            this.lastReceived.put(servicePair, System.currentTimeMillis());
            String user = vote.getUsername().replaceAll("[^a-zA-Z0-9_\\-]", "");
            user = user.substring(0, Math.min(user.length(), 16));
            vote.setUsername(user);
            if (!vote.getAddress().equals("fakeVote.local")) {
                final String service = vote.getServiceName().replaceAll("[^a-zA-Z0-9_\\.\\-]", "");
                vote.setServiceName(service);
            }
            this.processVote(vote);
        }
        else {
            this.log.info("Vote received on " + vote.getServiceName() + " without IGN, skipping as there is noone to reward...");
        }
    }
    
    public void processVote(final Vote vote) {
        this.processVote(vote, false);
    }
    
    public void processVote(final Vote vote, final boolean queued) {
        String playerName = vote.getUsername();
        final String serviceName = vote.getServiceName();
        final Player exactPlayer = this.getServer().getPlayerExact(playerName);
        if (exactPlayer != null) {
            playerName = exactPlayer.getName();
            vote.setUsername(playerName);
        }
        else if (this.users.containsKey(playerName.toLowerCase())) {
            playerName = this.users.get(playerName.toLowerCase());
            vote.setUsername(playerName);
        }
        if (!this.onlineOnly && exactPlayer == null) {
            this.log.info("Vote received on " + serviceName + " for Offline Player: " + playerName + ". Trying anyway.");
            this.processReward(new GALReward(VoteType.NORMAL, serviceName, vote, queued));
            return;
        }
        if (exactPlayer == null) {
            this.log.info("Vote received on " + serviceName + " for Offline Player: " + playerName + ". Adding to Queue for later");
            this.addToQueue(vote);
            return;
        }
        this.log.info("Vote received on " + serviceName + " for Player: " + playerName);
        if (this.blockedWorlds.contains(exactPlayer.getWorld().getName())) {
            this.log.info("Player: " + exactPlayer.getName() + " is in a blocked world.  Adding to Queue for later.");
            this.addToQueue(vote);
            return;
        }
        if (this.permVote) {
            for (final GALVote gVote : this.galVote.get(VoteType.PERMISSION)) {
                if (exactPlayer.hasPermission("gal." + gVote.key)) {
                    this.processReward(new GALReward(VoteType.PERMISSION, gVote.key, vote, queued));
                    this.log.info("Player: " + exactPlayer.getName() + " has permission: gal." + gVote.key);
                    return;
                }
            }
        }
        this.processReward(new GALReward(VoteType.NORMAL, serviceName, vote, queued));
    }
    
    public void processReward(final GALReward reward) {
        final String username = reward.vote.getUsername();
        int votetotal = 0;
        if (this.plugin.voteTotals.containsKey(username.toLowerCase())) {
            votetotal = this.plugin.voteTotals.get(username.toLowerCase());
        }
        ++votetotal;
        this.plugin.voteTotals.put(username.toLowerCase(), votetotal);
        this.plugin.lastVoted.put(username.toLowerCase(), System.currentTimeMillis());
        GAL.p.db.setVotes(username, votetotal, true);
        new ProcessReward(this, reward, votetotal).runTaskAsynchronously((Plugin)this);
    }
    
    public void onDisable() {
        if (this.voteReminder != null) {
            this.voteReminder.cancel();
        }
        this.getServer().getScheduler().cancelTasks((Plugin)this);
        this.closeLog();
        this.log.info(String.valueOf(this.getDescription().getFullName()) + " Disabled");
    }
    
    public void addToQueue(final Vote vote) {
        this.getServer().getScheduler().runTaskAsynchronously((Plugin)this, (Runnable)new Runnable() {
            @Override
            public void run() {
                final String player = vote.getUsername();
                final String service = vote.getServiceName();
                final String time = vote.getTimeStamp();
                final String ip = vote.getAddress();
                GAL.this.plugin.db.modifyQuery("INSERT INTO `" + GAL.this.plugin.dbPrefix + "GALQueue` (`IGN`,`service`,`timestamp`,`ip`) VALUES ('" + player + "','" + service + "','" + time + "','" + ip + "');");
                GAL.this.plugin.queuedVotes.put(VoteType.NORMAL, new GALReward(VoteType.NORMAL, service, vote, true));
                if (GAL.this.plugin.broadcastOffline) {
                    GALVote qVote = null;
                    for (final GALVote gVote : GAL.this.plugin.galVote.get(VoteType.NORMAL)) {
                        if (service.equalsIgnoreCase(gVote.key)) {
                            qVote = gVote;
                            break;
                        }
                    }
                    if (qVote == null) {
                        for (final GALVote gVote : GAL.this.plugin.galVote.get(VoteType.NORMAL)) {
                            if (gVote.key.equalsIgnoreCase("default")) {
                                qVote = gVote;
                                break;
                            }
                        }
                        if (qVote == null) {
                            return;
                        }
                    }
                    final String broadcast = GAL.this.plugin.formatMessage(qVote.broadcast, vote);
                    if (broadcast.length() > 0) {
                        Player[] getOnlinePlayers;
                        for (int length = (getOnlinePlayers = GAL.this.getServer().getOnlinePlayers().toArray(new Player[GAL.this.getServer().getOnlinePlayers().size()])).length, i = 0; i < length; ++i) {
                            final Player p = getOnlinePlayers[i];
                            if (GAL.this.plugin.broadcastRecent || !GAL.this.plugin.lastVoted.containsKey(p.getName().toLowerCase()) || GAL.this.plugin.lastVoted.get(p.getName().toLowerCase()) <= System.currentTimeMillis() - 86400000L) {
                                p.sendMessage(broadcast);
                            }
                        }
                    }
                }
            }
        });
    }
    
    public String formatMessage(final String message, final CommandSender sender) {
        int votes = 0;
        if (this.voteTotals.containsKey(sender.getName().toLowerCase())) {
            votes = this.voteTotals.get(sender.getName().toLowerCase());
        }
        return this.formatMessage(message.replace("{votes}", String.valueOf(votes)), new Vote[0]);
    }
    
    public String formatMessage(String message, final Vote... vote) {
        if (message == null) {
            return "";
        }
        String serviceName = "";
        String playerName = "";
        int votes = 0;
        if (vote.length > 0) {
            serviceName = vote[0].getServiceName();
            playerName = vote[0].getUsername();
            if (this.voteTotals.containsKey(playerName.toLowerCase())) {
                votes = this.plugin.voteTotals.get(playerName.toLowerCase());
            }
        }
        if (message.indexOf("/") == 0) {
            message = message.substring(1);
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        message = message.replace("{servicename}", serviceName).replace("{service}", serviceName).replace("{SERVICE}", serviceName).replace("{name}", playerName).replace("(name)", playerName).replace("{player}", playerName).replace("(player)", playerName).replace("{username}", playerName).replace("(username)", playerName).replace("<name>", playerName).replace("<player>", playerName).replace("<username>", playerName).replace("[name]", playerName).replace("[player]", playerName).replace("[username]", playerName).replace("{AQUA}", "§b").replace("{BLACK}", "§0").replace("{BLUE}", "§9").replace("{DARK_AQUA}", "§3").replace("{DARK_BLUE}", "§1").replace("{DARK_GRAY}", "§8").replace("{DARK_GREEN}", "§2").replace("{DARK_PURPLE}", "§5").replace("{DARK_RED}", "§4").replace("{GOLD}", "§6").replace("{GRAY}", "§7").replace("{GREEN}", "§a").replace("{LIGHT_PURPLE}", "§d").replace("{RED}", "§c").replace("{WHITE}", "§f").replace("{YELLOW}", "§e").replace("{BOLD}", "§l").replace("{ITALIC}", "§o").replace("{MAGIC}", "§k").replace("{RESET}", "§r").replace("{STRIKE}", "§m").replace("{STRIKETHROUGH}", "§m").replace("{UNDERLINE}", "§n").replace("{votes}", String.valueOf(votes));
        return message;
    }
    
    public void log(final Vote vote) {
        if (!this.logEnabled || vote == null) {
            return;
        }
        if (this.fw == null) {
            try {
                this.fw = new FileWriter(new File(this.getDataFolder(), "vote.log"), true);
            }
            catch (IOException ex) {
                this.log.log(Level.SEVERE, null, ex);
            }
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(DateFormat.getDateTimeInstance(0, 0).format(new Date())).append(" ").append(vote).append("\n");
        try {
            this.fw.write(sb.toString());
            this.fw.flush();
        }
        catch (IOException ex2) {
            this.log.log(Level.SEVERE, null, ex2);
        }
    }
    
    public void closeLog() {
        if (this.fw != null) {
            try {
                this.fw.close();
            }
            catch (IOException ex) {
                this.log.log(Level.SEVERE, null, ex);
            }
            this.fw = null;
        }
    }
}
