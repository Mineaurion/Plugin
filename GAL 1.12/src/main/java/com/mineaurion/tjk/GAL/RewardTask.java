// 
// Decompiled by Procyon v0.5.30
// 

package com.mineaurion.tjk.GAL;

import java.util.Iterator;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import java.util.List;
import org.bukkit.scheduler.BukkitRunnable;

public class RewardTask extends BukkitRunnable
{
    private GAL plugin;
    private List<String> commands;
    private String message;
    private String broadcast;
    private boolean shouldBroadcast;
    private Player player;
    private String username;
    
    public RewardTask(final GAL plugin, final GALVote vote, final GALReward reward) {
        this.commands = new ArrayList<String>();
        this.message = "";
        this.broadcast = "";
        this.shouldBroadcast = true;
        this.username = "";
        this.plugin = plugin;
        this.message = vote.message;
        this.broadcast = vote.broadcast;
        this.commands = vote.commands;
        if (reward.queued) {
            this.shouldBroadcast = (plugin.broadcastQueue && !plugin.broadcastOffline);
        }
        this.username = reward.vote.getUsername();
        this.player = plugin.getServer().getPlayerExact(this.username);
        if (this.player != null) {
            this.username = this.player.getName();
        }
    }
    
    public void run() {
        if (this.broadcast.length() > 0 && this.shouldBroadcast) {
            Player[] getOnlinePlayers;
            for (int length = (getOnlinePlayers = this.plugin.getServer().getOnlinePlayers().toArray(new Player[this.plugin.getServer().getOnlinePlayers().size()])).length, i = 0; i < length; ++i) {
                final Player p = getOnlinePlayers[i];
                if (this.plugin.broadcastRecent || p.getName().equalsIgnoreCase(this.username) || !this.plugin.lastVoted.containsKey(p.getName().toLowerCase()) || this.plugin.lastVoted.get(p.getName().toLowerCase()) <= System.currentTimeMillis() - 86400000L) {
                    String[] split;
                    for (int length2 = (split = this.broadcast.split("\\\\n")).length, j = 0; j < length2; ++j) {
                        final String b = split[j];
                        p.sendMessage(b);
                    }
                }
            }
        }
        if (this.message.length() > 0 && this.player != null) {
            String[] split2;
            for (int length3 = (split2 = this.message.split("\\\\n")).length, k = 0; k < length3; ++k) {
                final String m = split2[k];
                this.player.sendMessage(m);
            }
        }
        for (final String command : this.commands) {
            this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, (Runnable)new Runnable() {
                @Override
                public void run() {
                    RewardTask.this.plugin.getServer().dispatchCommand((CommandSender)RewardTask.this.plugin.getServer().getConsoleSender(), command);
                }
            });
            try {
                Thread.sleep(50L);
            }
            catch (InterruptedException ex) {}
        }
    }
}
