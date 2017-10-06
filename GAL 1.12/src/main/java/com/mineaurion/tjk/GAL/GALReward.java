// 
// Decompiled by Procyon v0.5.30
// 

package com.mineaurion.tjk.GAL;

import com.vexsoftware.votifier.model.Vote;

public class GALReward
{
    public VoteType type;
    public String key;
    public Vote vote;
    public boolean queued;
    
    public GALReward(final VoteType t, final String k, final Vote v, final boolean q) {
        this.type = t;
        this.key = k;
        this.vote = v;
        this.queued = q;
    }
}
