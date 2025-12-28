package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.item.inscription.definition.EffectIDs;
import com.amorabot.inscripted.item.inscription.definition.KeystoneIDs;
import com.amorabot.inscripted.item.render.InscriptedPalette;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InscriptionsInfo implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player)){
            return false;
        }
        Player player = (Player) commandSender;
        PlayerInventory inventory = player.getInventory();
//        ItemStack heldItem = inventory.getItemInMainHand();
//        Weapon weaponData = ItemDeserializer.deserializeWeaponData(heldItem);

        List<Component> infoComponents = new ArrayList<>();
        Component emptyComponent = Component.text("");

        infoComponents.add(Component.text("Special Effects ---").color(InscriptedPalette.RELIC.getColor()).decorate(TextDecoration.BOLD));
        for (EffectIDs effect : EffectIDs.values()) {
            Component currentDescription = Component.text(effect.getDescription()).color(InscriptedPalette.NEUTRAL_GRAY.getColor());
            infoComponents.add(secondaryEffectRenderer(effect));
            infoComponents.add(currentDescription);
            infoComponents.add(emptyComponent);
        }
        infoComponents.add(emptyComponent);
        infoComponents.add(Component.text("Special Keystones ---").color(InscriptedPalette.RELIC.getColor()).decorate(TextDecoration.BOLD));
        for (KeystoneIDs keystone : KeystoneIDs.values()) {
            Component currentDescription = Component.text(keystone.getDescription()).color(InscriptedPalette.NEUTRAL_GRAY.getColor());
            infoComponents.add(secondaryKeystoneRenderer(keystone));
            infoComponents.add(currentDescription);
            infoComponents.add(emptyComponent);
        }

        for (Component infoLine : infoComponents) {
            player.sendMessage(infoLine);
        }
        return true;
    }

    private Component secondaryEffectRenderer(EffectIDs effect){
        String effectName = effect.name().replace("_"," ").strip();
        String effectInfo = effect.getInfo();
        String effectColor = InscriptedPalette.RELIC.getColorString();
        String infoColor = InscriptedPalette.NEUTRAL_GRAY.getColorString();
        return MiniMessage.miniMessage()
                .deserialize("<"+effectColor+"><b>" + effectName + "</b></"+effectColor+">" + "<"+infoColor+"> ⏵ " + effectInfo + "</"+infoColor+">");
    }
    private Component secondaryKeystoneRenderer(KeystoneIDs keystone){
        String keystoneName = keystone.name().replace("_"," ").strip();
        String keystoneColor = InscriptedPalette.RELIC.getColorString();
        return MiniMessage.miniMessage()
                .deserialize("<"+keystoneColor+"><b>" + keystoneName + " ✎</b></"+keystoneColor+">");
    }
}
