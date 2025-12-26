package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.item.structure.Weapon.Weapon;
import com.amorabot.inscripted.item.structure.io.ItemDeserializer;
import com.amorabot.inscripted.player.Archetypes;
import com.amorabot.inscripted.skill.Skills;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SkillsInfo implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player)){
            return false;
        }
        Player player = (Player) commandSender;
        PlayerInventory inventory = player.getInventory();
        ItemStack heldItem = inventory.getItemInMainHand();
        Weapon weaponData = ItemDeserializer.deserializeWeaponData(heldItem);

        List<Component> infoComponents = new ArrayList<>();
        Component emptyComponent = Component.text("");

        if (weaponData==null){
            player.sendMessage(Component.text("Hold a fucking weapon then try it, so you can find out its skills"));
            return true;
        }
        Archetypes weaponArchetype = weaponData.getWeaponType().mapArchetype();
        for (Skills skill : Skills.values()) {
            Archetypes skillArchetype = skill.getArchetype();
            if (skillArchetype==null){continue;}
            if (!skillArchetype.equals(weaponArchetype)){continue;}
            List<Component> skillComponents = skill.getSkillDataComponents();
            infoComponents.addAll(skillComponents);
            infoComponents.add(emptyComponent);
            infoComponents.add(emptyComponent);
        }

        for (Component infoLine : infoComponents) {
            player.sendMessage(infoLine);
        }

        return true;
    }
}
