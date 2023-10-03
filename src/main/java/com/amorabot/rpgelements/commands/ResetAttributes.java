package com.amorabot.rpgelements.commands;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Player.Profile;
import com.amorabot.rpgelements.managers.JSONProfileManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetAttributes implements CommandExecutor {

    private RPGElements plugin;

    public ResetAttributes(RPGElements plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            return false;
        }
        Player player = (Player) sender;
        //TODO: implementar reset de perfis
        Profile profile = JSONProfileManager.getProfile(player.getUniqueId());
        profile.getStats().setArmorSet(null,null,null,null);
        profile.updateArmorSlot();
        profile.getHealthComponent().resetCurrentHealth();
        return true;
    }
}
