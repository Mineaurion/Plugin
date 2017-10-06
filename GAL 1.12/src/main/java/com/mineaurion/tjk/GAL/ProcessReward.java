// 
// Decompiled by Procyon v0.5.30
// 

package com.mineaurion.tjk.GAL;

import java.util.List;
import java.util.Iterator;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import java.util.ArrayList;
import com.vexsoftware.votifier.model.Vote;
import org.bukkit.plugin.Plugin;
import java.security.SecureRandom;
import java.util.logging.Logger;
import org.bukkit.scheduler.BukkitRunnable;

public class ProcessReward extends BukkitRunnable
{
    private GAL plugin;
    private Logger log;
    private SecureRandom random;
    private GALReward reward;
    private int votetotal;
    
    public ProcessReward(final GAL plugin, final GALReward reward, final int votetotal) {
        this.random = new SecureRandom();
        this.plugin = plugin;
        this.reward = reward;
        this.log = plugin.log;
        this.votetotal = votetotal;
    }
    
    public void run() {
        try {
            String username = this.reward.vote.getUsername();
            final Player player = this.plugin.getServer().getPlayerExact(username);
            if (player != null) {
                username = player.getName();
            }
            GALVote vote = null;
            int lucky = 0;
            final boolean isFakeVote = this.reward.vote.getAddress().equals("fakeVote.local");
            if (isFakeVote) {
                final String[] service = this.reward.vote.getServiceName().split("\\|");
                if (service.length > 1) {
                    this.reward.vote.setServiceName(service[0]);
                    try {
                        lucky = Integer.parseInt(service[1]);
                    }
                    catch (NumberFormatException ex2) {}
                }
            }
            switch (this.reward.type) {
                case NORMAL:
                case PERMISSION: {
                    if (this.plugin.luckyVote && isFakeVote && lucky > 0) {
                        final String luckyString = String.valueOf(lucky);
                        for (final GALVote gVote : this.plugin.galVote.get(VoteType.LUCKY)) {
                            try {
                                if (lucky == Integer.parseInt(gVote.key)) {
                                    this.log.info("Player: " + username + " was lucky with number " + luckyString);
                                    new ProcessReward(this.plugin, new GALReward(VoteType.LUCKY, luckyString, this.reward.vote, false), this.votetotal).runTaskAsynchronously((Plugin)this.plugin);
                                    break;
                                }
                                continue;
                            }
                            catch (NumberFormatException ex3) {}
                        }
                    }
                    else if (this.plugin.luckyVote && player != null) {
                        int luckiest = 0;
                        for (final GALVote gVote : this.plugin.galVote.get(VoteType.LUCKY)) {
                            int l = 0;
                            try {
                                l = Integer.parseInt(gVote.key);
                            }
                            catch (NumberFormatException ex) {
                                continue;
                            }
                            if (l <= 0) {
                                continue;
                            }
                            if (l <= luckiest || this.random.nextInt(l) != 0) {
                                continue;
                            }
                            luckiest = l;
                        }
                        if (luckiest > 0) {
                            final String luckString = String.valueOf(luckiest);
                            for (final GALVote gVote2 : this.plugin.galVote.get(VoteType.LUCKY)) {
                                try {
                                    if (luckiest == Integer.parseInt(gVote2.key)) {
                                        this.log.info("Player: " + username + " was lucky with number " + luckString);
                                        new ProcessReward(this.plugin, new GALReward(VoteType.LUCKY, luckString, this.reward.vote, false), this.votetotal).runTaskAsynchronously((Plugin)this.plugin);
                                        break;
                                    }
                                    continue;
                                }
                                catch (NumberFormatException ex4) {}
                            }
                        }
                    }
                    if (this.plugin.cumulativeVote) {
                        for (final GALVote gVote3 : this.plugin.galVote.get(VoteType.CUMULATIVE)) {
                            try {
                                if (this.votetotal != Integer.parseInt(gVote3.key)) {
                                    continue;
                                }
                                new ProcessReward(this.plugin, new GALReward(VoteType.CUMULATIVE, gVote3.key, this.reward.vote, false), this.votetotal).runTaskAsynchronously((Plugin)this.plugin);
                                if (player != null) {
                                    this.log.info("Player: " + username + " has voted " + this.votetotal + " times");
                                    break;
                                }
                                this.log.info("Offline Player: " + username + " has voted " + this.votetotal + " times");
                                break;
                            }
                            catch (NumberFormatException ex5) {}
                        }
                        break;
                    }
                    break;
                }
            }
            switch (this.reward.type) {
                case NORMAL: {
                    for (final GALVote gVote3 : this.plugin.galVote.get(VoteType.NORMAL)) {
                        if (this.reward.key.equalsIgnoreCase(gVote3.key)) {
                            vote = gVote3;
                            break;
                        }
                    }
                    if (vote != null) {
                        break;
                    }
                    for (final GALVote gVote3 : this.plugin.galVote.get(VoteType.NORMAL)) {
                        if (gVote3.key.equalsIgnoreCase("default")) {
                            vote = gVote3;
                            break;
                        }
                    }
                    if (vote == null) {
                        this.log.severe("Default service not found, check your config!");
                        break;
                    }
                    break;
                }
                case PERMISSION: {
                    for (final GALVote gVote3 : this.plugin.galVote.get(VoteType.PERMISSION)) {
                        if (this.reward.key.equalsIgnoreCase(gVote3.key)) {
                            vote = gVote3;
                            break;
                        }
                    }
                    if (vote == null) {
                        this.log.severe("Perm config key '" + this.reward.key + "' not found, check your config!");
                        break;
                    }
                    break;
                }
                case LUCKY: {
                    for (final GALVote gVote3 : this.plugin.galVote.get(VoteType.LUCKY)) {
                        if (this.reward.key.equals(gVote3.key)) {
                            vote = gVote3;
                            break;
                        }
                    }
                    if (vote == null) {
                        this.log.severe("Lucky config key '" + this.reward.key + "' not found, check your config!");
                        break;
                    }
                    break;
                }
                case CUMULATIVE: {
                    for (final GALVote gVote3 : this.plugin.galVote.get(VoteType.CUMULATIVE)) {
                        if (this.reward.key.equals(gVote3.key)) {
                            vote = gVote3;
                            break;
                        }
                    }
                    if (vote == null) {
                        this.log.severe("Cumulative config key '" + this.reward.key + "' not found, check your config!");
                        break;
                    }
                    break;
                }
            }
            if (vote == null) {
                return;
            }
            final String message = this.plugin.formatMessage(vote.message, this.reward.vote);
            final String broadcast = this.plugin.formatMessage(vote.broadcast, this.reward.vote);
            final List<String> commands = new ArrayList<String>();
            for (final String command : vote.commands) {
                commands.add(this.plugin.formatMessage(command, this.reward.vote));
            }
            final RewardEvent event = new RewardEvent(this.reward.type, new GALVote(this.reward.key, message, broadcast, commands));
            this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, (Runnable)new Runnable() {
                @Override
                public void run() {
                    ProcessReward.this.plugin.getServer().getPluginManager().callEvent((Event)event);
                }
            });
            if (!event.isCancelled()) {
                vote = event.getVote();
                new RewardTask(this.plugin, vote, this.reward).runTaskAsynchronously((Plugin)this.plugin);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
