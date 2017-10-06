// 
// Decompiled by Procyon v0.5.30
// 

package com.mineaurion.tjk.GAL;

import org.bukkit.plugin.Plugin;
import com.vexsoftware.votifier.model.Vote;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.bukkit.entity.Player;

public class VoteAPI
{
    public static int getVoteTotal(final String name) {
        if (GAL.p.voteTotals.containsKey(name.toLowerCase())) {
            return GAL.p.voteTotals.get(name.toLowerCase());
        }
        return 0;
    }
    
    public static int getVoteTotal(final Player player) {
        return getVoteTotal(player.getName());
    }
    
    public static long getLastVoteTime(final String name) {
        if (GAL.p.lastVoted.containsKey(name.toLowerCase())) {
            return GAL.p.lastVoted.get(name.toLowerCase());
        }
        return 0L;
    }
    
    public static long getLastVoteTime(final Player player) {
        return getLastVoteTime(player.getName());
    }
    
    public static void nameChange(String from, String to) {
        from = from.replaceAll("[^a-zA-Z0-9_\\-]", "");
        to = to.replaceAll("[^a-zA-Z0-9_\\-]", "");
        final String oldUser = from.substring(0, Math.min(from.length(), 16));
        final String newUser = to.substring(0, Math.min(to.length(), 16));
        GAL.p.getServer().getScheduler().runTaskAsynchronously((Plugin)GAL.p, (Runnable)new Runnable() {
            @Override
            public void run() {
                final List<GALReward> playerQueue = new ArrayList<GALReward>();
                synchronized (GAL.p.queuedVotes) {
                    final Iterator<Map.Entry<VoteType, GALReward>> i = GAL.p.queuedVotes.entries().iterator();
                    while (i.hasNext()) {
                        final Map.Entry<VoteType, GALReward> entry = i.next();
                        if (entry.getValue().vote.getUsername().equalsIgnoreCase(oldUser)) {
                            playerQueue.add(entry.getValue());
                            i.remove();
                        }
                    }
                }
                // monitorexit(GAL.p.queuedVotes)
                GAL.p.db.modifyQuery("DELETE FROM `" + GAL.p.dbPrefix + "GALQueue` WHERE LOWER(`IGN`) = '" + oldUser.toLowerCase() + "'");
                for (final GALReward reward : playerQueue) {
                    final Vote vote = reward.vote;
                    vote.setUsername(newUser);
                    final GALReward newReward = new GALReward(reward.type, vote.getServiceName(), vote, true);
                    GAL.p.db.modifyQuery("INSERT INTO `" + GAL.p.dbPrefix + "GALQueue` (`IGN`,`service`,`timestamp`,`ip`) VALUES ('" + newUser.toLowerCase() + "','" + vote.getServiceName() + "','" + vote.getTimeStamp() + "','" + vote.getAddress() + "');");
                    GAL.p.queuedVotes.put(newReward.type, newReward);
                }
                int votes = 0;
                if (GAL.p.voteTotals.containsKey(oldUser.toLowerCase())) {
                    votes = GAL.p.voteTotals.get(oldUser.toLowerCase());
                }
                if (votes > 0) {
                    GAL.p.db.setVotes(oldUser, 0, false);
                    GAL.p.voteTotals.put(oldUser.toLowerCase(), 0);
                    GAL.p.db.setVotes(newUser, 0, false);
                    GAL.p.voteTotals.put(newUser.toLowerCase(), votes);
                }
            }
        });
    }
}
