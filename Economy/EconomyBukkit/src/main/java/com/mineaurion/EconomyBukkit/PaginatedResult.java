package com.mineaurion.EconomyBukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

public abstract class PaginatedResult<T> {
    private final String header;

    protected static final int PER_PAGE = 9;
    
    public PaginatedResult(String header) {
        this.header = header;
    }
    
    public void display(CommandSender sender, Collection<? extends T> results, int page) throws CommandException {
        display(sender, new ArrayList<T>(results), page);
    }
    
    public void display(CommandSender sender, List<? extends T> results, int page) throws CommandException {
        if (results.size() == 0) throw new CommandException("No results match!");
        --page;

        int maxPages = results.size() / PER_PAGE;

        // If the content divides perfectly, eg (18 entries, and 9 per page)
        // we end up with a blank page this handles this case
        if (results.size() % PER_PAGE == 0) {
            maxPages--;
        }

        page = Math.max(0, Math.min(page, maxPages));

        sender.sendMessage(ChatColor.YELLOW + header + " - Page (" + (page + 1) + "/" + (maxPages + 1) + ")");
        for (int i = PER_PAGE * page; i < PER_PAGE * page + PER_PAGE  && i < results.size(); i++) {
            Main.sendmessage(format(results.get(i)),sender.getName());
        }
    }
    
    public abstract String format(T entry);

}
