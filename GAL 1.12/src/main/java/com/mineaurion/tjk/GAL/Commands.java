// 
// Decompiled by Procyon v0.5.30
// 

package com.mineaurion.tjk.GAL;

import org.bukkit.event.Event;
import com.vexsoftware.votifier.model.VotifierEvent;
import com.vexsoftware.votifier.model.Vote;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class Commands implements CommandExecutor
{
    private GAL plugin;
    
    public Commands(final GAL plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final String cmdName = command.getName();
        if (cmdName.equalsIgnoreCase("gal")) {
            final String arg1 = (args.length > 0) ? args[0] : null;
            String arg2 = (args.length > 1) ? args[1] : null;
            final String arg3 = (args.length > 2) ? args[2] : null;
            if (arg1 == null) {
                sender.sendMessage("- /gal reload | clearqueue | cleartotals | forcequeue | total <player> <total> | clear <player> | top [count] | votes <player> | broadcast <message>");
                return true;
            }
            if (arg1.equalsIgnoreCase("reload")) {
                if (sender.isOp() || sender.hasPermission("gal.admin")) {
                    this.plugin.reload();
                    sender.sendMessage("Reloaded " + this.plugin.getDescription().getFullName());
                    this.plugin.log.info("Reloaded " + this.plugin.getDescription().getFullName());
                    return true;
                }
                return false;
            }
            else if (arg1.equalsIgnoreCase("cleartotals")) {
                if (sender.isOp() || sender.hasPermission("gal.admin")) {
                    this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            Commands.this.plugin.db.modifyQuery("DELETE FROM `" + Commands.this.plugin.dbPrefix + "GALTotals`;");
                        }
                    });
                    this.plugin.voteTotals.clear();
                    sender.sendMessage("Reset vote totals");
                    return true;
                }
                return false;
            }
            else if (arg1.equalsIgnoreCase("clearqueue")) {
                if (sender.isOp() || sender.hasPermission("gal.admin")) {
                    this.plugin.queuedVotes.clear();
                    this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            Commands.this.plugin.db.modifyQuery("DELETE FROM `" + Commands.this.plugin.dbPrefix + "GALQueue`;");
                        }
                    });
                    this.plugin.queuedVotes.clear();
                    sender.sendMessage("Cleared vote queue");
                    return true;
                }
                return false;
            }
            else if (arg1.equalsIgnoreCase("forcequeue")) {
                if (sender.isOp() || sender.hasPermission("gal.admin")) {
                    this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            if (Commands.this.plugin.queuedVotes.isEmpty()) {
                                sender.sendMessage("There are no queued votes!");
                                return;
                            }
                            sender.sendMessage("Processing " + Commands.this.plugin.queuedVotes.size() + " votes...");
                            synchronized (Commands.this.plugin.queuedVotes) {
                                final Iterator<Map.Entry<VoteType, GALReward>> i = Commands.this.plugin.queuedVotes.entries().iterator();
                                while (i.hasNext()) {
                                    final Map.Entry<VoteType, GALReward> entry = i.next();
                                    final Vote vote = entry.getValue().vote;
                                    Commands.this.plugin.log.info("Forcing queued vote for " + vote.getUsername() + " on " + vote.getServiceName());
                                    Commands.this.plugin.processReward(new GALReward(entry.getKey(), vote.getServiceName(), vote, false));
                                    i.remove();
                                }
                            }
                            // monitorexit(Commands.access$0(this.this$0).queuedVotes)
                            Commands.this.plugin.db.modifyQuery("DELETE FROM `" + Commands.this.plugin.dbPrefix + "GALQueue`;");
                        }
                    });
                    return true;
                }
                return false;
            }
            else if (arg1.equalsIgnoreCase("top")) {
                if (sender.isOp() || sender.hasPermission("gal.admin")) {
                    int count = 10;
                    try {
                        count = ((arg2 == null) ? 10 : Integer.parseInt(arg2));
                    }
                    catch (NumberFormatException ex2) {}
                    this.voteTop(sender, count);
                    return true;
                }
                return false;
            }
            else if (arg1.equalsIgnoreCase("total")) {
                if (!sender.isOp() && !sender.hasPermission("gal.admin")) {
                    return false;
                }
                if (arg3 == null) {
                    sender.sendMessage("- /gal total <player> <total>");
                    return true;
                }
                String user = arg2.replaceAll("[^a-zA-Z0-9_\\-]", "");
                user = user.substring(0, Math.min(user.length(), 16));
                int count2 = 0;
                try {
                    count2 = Integer.parseInt(arg3);
                }
                catch (NumberFormatException ex) {
                    sender.sendMessage("- /gal total <player> <total>");
                    return true;
                }
                final String username = user;
                final int votes = count2;
                this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new Runnable() {
                    @Override
                    public void run() {
                        Commands.this.plugin.db.setVotes(username, votes, false);
                    }
                });
                this.plugin.voteTotals.put(user.toLowerCase(), count2);
                this.plugin.lastVoted.put(user.toLowerCase(), System.currentTimeMillis());
                sender.sendMessage("Setting " + user + "'s total votes to: " + count2);
                return true;
            }
            else if (arg1.equalsIgnoreCase("clear")) {
                if (!sender.isOp() && !sender.hasPermission("gal.admin")) {
                    return false;
                }
                if (arg2 == null) {
                    sender.sendMessage("- /gal clear <player>");
                    return true;
                }
                arg2 = arg2.replaceAll("[^a-zA-Z0-9_\\-]", "");
                final String user = arg2.substring(0, Math.min(arg2.length(), 16));
                this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new Runnable() {
                    @Override
                    public void run() {
                        Commands.this.plugin.db.modifyQuery("DELETE FROM `" + Commands.this.plugin.dbPrefix + "GALQueue` WHERE LOWER(`IGN`) = '" + user.toLowerCase() + "'");
                    }
                });
                synchronized (this.plugin.queuedVotes) {
                    final Iterator<Map.Entry<VoteType, GALReward>> i = this.plugin.queuedVotes.entries().iterator();
                    while (i.hasNext()) {
                        final Map.Entry<VoteType, GALReward> entry = i.next();
                        if (entry.getValue().vote.getUsername().equalsIgnoreCase(user)) {
                            i.remove();
                        }
                    }
                }
                // monitorexit(this.plugin.queuedVotes)
                sender.sendMessage("Clearing " + user + "'s queued votes");
                return true;
            }
            else if (arg1.equalsIgnoreCase("broadcast")) {
                if (!sender.isOp() && !sender.hasPermission("gal.admin")) {
                    return false;
                }
                if (args.length > 1) {
                    final StringBuilder sb = new StringBuilder();
                    for (int j = 1; j < args.length; ++j) {
                        sb.append(args[j]).append(" ");
                    }
                    this.plugin.getServer().broadcastMessage(this.plugin.formatMessage(sb.toString().trim(), new Vote[0]));
                    return true;
                }
                return false;
            }
            else {
                if (!arg1.equalsIgnoreCase("votes")) {
                    sender.sendMessage("- /gal reload | clearqueue | cleartotals | total <player> <total> | clear <player> | top [count]");
                    return true;
                }
                if (!sender.isOp() && !sender.hasPermission("gal.admin")) {
                    return false;
                }
                if (arg2 == null) {
                    sender.sendMessage("- /gal votes <player>");
                    return true;
                }
                int votes2 = 0;
                if (this.plugin.voteTotals.containsKey(arg2.toLowerCase())) {
                    votes2 = this.plugin.voteTotals.get(arg2.toLowerCase());
                }
                sender.sendMessage("Player: " + arg2 + " has " + votes2 + " votes");
                return true;
            }
        }
        else if (cmdName.equalsIgnoreCase("fakevote")) {
            if (!sender.isOp() && !sender.hasPermission("gal.admin")) {
                return false;
            }
            final String arg1 = (args.length > 0) ? args[0] : null;
            final String arg2 = (args.length > 1) ? args[1] : null;
            final String arg3 = (args.length > 2) ? args[2] : null;
            if (arg1 == null) {
                sender.sendMessage("- /fakevote <player> [servicename] [luckynumber]");
                return true;
            }
            int lucky = 0;
            if (arg3 != null) {
                try {
                    lucky = Integer.parseInt(arg3);
                }
                catch (NumberFormatException ex3) {}
            }
            final Vote fakeVote = new Vote();
            fakeVote.setUsername(arg1);
            final StringBuilder service = new StringBuilder();
            service.append((arg2 == null) ? "fakeVote" : arg2);
            if (arg3 != null) {
                service.append("|").append(lucky);
            }
            fakeVote.setServiceName(service.toString());
            fakeVote.setAddress("fakeVote.local");
            fakeVote.setTimeStamp(String.valueOf(System.currentTimeMillis()));
            this.plugin.getServer().getPluginManager().callEvent((Event)new VotifierEvent(fakeVote));
            sender.sendMessage("sent fake vote!");
            this.plugin.log.info("Sent fake vote: " + fakeVote.toString());
            return true;
        }
        else if (cmdName.equalsIgnoreCase("vote")) {
            if (this.plugin.voteCommand) {
                for (final String message : this.plugin.voteMessage) {
                    sender.sendMessage(this.plugin.formatMessage(message, sender));
                }
                return true;
            }
            return false;
        }
        else if (cmdName.equalsIgnoreCase("rewards")) {
            if (this.plugin.rewardCommand && this.plugin.cumulativeVote) {
                for (final String message : this.plugin.rewardHeader) {
                    sender.sendMessage(this.plugin.formatMessage(message, sender));
                }
                for (final GALVote gVote : this.plugin.galVote.get(VoteType.CUMULATIVE)) {
                    if (this.plugin.rewardMessages.containsKey(gVote.key)) {
                        final String message2 = this.plugin.rewardFormat.replace("{TOTAL}", gVote.key).replace("{REWARD}", this.plugin.rewardMessages.get(gVote.key));
                        sender.sendMessage(this.plugin.formatMessage(message2, sender));
                    }
                }
                for (final String message : this.plugin.rewardFooter) {
                    sender.sendMessage(this.plugin.formatMessage(message, sender));
                }
                return true;
            }
            return false;
        }
        else {
            if (!cmdName.equalsIgnoreCase("votetop")) {
                return false;
            }
            if (sender.isOp() || sender.hasPermission("gal.admin") || sender.hasPermission("gal.top")) {
                this.voteTop(sender, 10);
                return true;
            }
            return false;
        }
    }
    
    public void voteTop(final CommandSender sender, final int count) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                int place = 1;
                final Map<String, Integer> votes = Commands.this.plugin.db.getVoteTop(count);
                for (final String message : Commands.this.plugin.votetopHeader) {
                    sender.sendMessage(Commands.this.plugin.formatMessage(message, sender));
                }
                for (final Map.Entry<String, Integer> entry : votes.entrySet()) {
                    String user = entry.getKey();
                    final int total = entry.getValue();
                    if (Commands.this.plugin.users.containsKey(user.toLowerCase())) {
                        user = Commands.this.plugin.users.get(user.toLowerCase());
                    }
                    final String message2 = Commands.this.plugin.votetopFormat.replace("{POSITION}", String.valueOf(place)).replace("{TOTAL}", String.valueOf(total)).replace("{username}", user);
                    sender.sendMessage(Commands.this.plugin.formatMessage(message2, new Vote[0]));
                    ++place;
                }
            }
        });
    }
}
