package com.amorabot.inscripted.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {

    public StatsCommand(){
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }
}
