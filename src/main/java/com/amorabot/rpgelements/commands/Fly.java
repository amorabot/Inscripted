package com.amorabot.rpgelements.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
@Deprecated
public class Fly implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){

        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can run this command");
            return true;
        }

        Player player = (Player) sender; //aqui ja garantimos que sender é um player, portanto podemos usar o sender
        //genérico apenas com as funcionalidades de player usando o casting (Player)

        if (player.getAllowFlight()){ //se o player ja pode voar, desligue o voo, senão, ligue
            player.setAllowFlight(false);
            player.sendMessage("Flying Disabled");
        } else {
            player.setAllowFlight(true);
            player.sendMessage("Flying Enabled");
        }

        return true;
    }
}
