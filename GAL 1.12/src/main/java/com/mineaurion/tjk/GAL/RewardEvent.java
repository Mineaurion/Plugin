// 
// Decompiled by Procyon v0.5.30
// 

package com.mineaurion.tjk.GAL;

import java.util.List;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class RewardEvent extends Event implements Cancellable
{
    private static final HandlerList handlers;
    private boolean cancelled;
    private VoteType type;
    private String message;
    private String broadcast;
    private List<String> commands;
    private String key;
    
    static {
        handlers = new HandlerList();
    }
    
    public RewardEvent(final VoteType type, final GALVote vote) {
        this.type = type;
        this.message = vote.message;
        this.broadcast = vote.broadcast;
        this.commands = vote.commands;
        this.key = vote.key;
    }
    
    public VoteType getType() {
        return this.type;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public List<String> getCommandList() {
        return this.commands;
    }
    
    public String getBroadcastMessage() {
        return this.broadcast;
    }
    
    public String getPlayerMessage() {
        return this.message;
    }
    
    public void setCommandList(final List<String> commands) {
        this.commands = commands;
    }
    
    public void setBroadcastMessage(final String broadcast) {
        this.broadcast = broadcast;
    }
    
    public void setPlayerMessage(final String message) {
        this.message = message;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }
    
    public HandlerList getHandlers() {
        return RewardEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return RewardEvent.handlers;
    }
    
    public GALVote getVote() {
        return new GALVote(this.key, this.message, this.broadcast, this.commands);
    }
}
