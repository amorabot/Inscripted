package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
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
        Profile profile = JSONProfileManager.getProfile(player.getUniqueId());
        profile.getEquipmentComponent().setArmorSet(null, null, null, null);
        profile.getHealthComponent().replenishHitPoints();
        profile.updateEquipmentSlot(ItemTypes.WEAPON, null, player.getUniqueId());
        return true;
    }
}
