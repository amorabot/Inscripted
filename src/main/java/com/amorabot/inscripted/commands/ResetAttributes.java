package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetAttributes implements CommandExecutor {

    private Inscripted plugin;

    public ResetAttributes(Inscripted plugin){
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
