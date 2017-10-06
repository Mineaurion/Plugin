// 
// Decompiled by Procyon v0.5.30
// 

package com.mineaurion.tjk.GAL;

import java.util.List;

public class GALVote
{
    public String key;
    public String message;
    public String broadcast;
    public List<String> commands;
    
    public GALVote(final String k, final String m, final String b, final List<String> c) {
        this.key = k;
        this.message = m;
        this.broadcast = b;
        this.commands = c;
    }
}
