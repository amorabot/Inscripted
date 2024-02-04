package com.amorabot.inscripted.APIs;

import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MessageAPI {

    public static final TextComponent messageIndicator = Component.text(Utils.color("[&a!&f]"));

    public static void broadcast(TextComponent textComponent){
         for (Player player : Bukkit.getServer().getOnlinePlayers()){
             player.sendMessage(messageIndicator.append(textComponent));
         }
    }
    public static void whisper(){

    }
    public static TextComponent deathMessage(Player killer, Player killed){
        ItemStack killerHeldItem = killer.getInventory().getItemInMainHand();
        if (killerHeldItem.getType().isAir()){
            return Component.text(killer.getName() + " dunked on " + killed.getName());
        }
        TextComponent deathMessage = Component.text(killer.getName() + " ");
        TextComponent weaponDisplay = Component.text("");
        if (!killerHeldItem.hasItemMeta()){
            weaponDisplay = weaponDisplay.append(killerHeldItem.displayName());
            return deathMessage.append(weaponDisplay).append(Component.text(" " + killed.getName()));
        }

        weaponDisplay = weaponDisplay.append(killerHeldItem.displayName());

        if (killerHeldItem.getItemMeta().hasLore()){
            MiniMessage mm = MiniMessage.miniMessage();
            for (String loreLine : killerHeldItem.getItemMeta().getLore()){

                weaponDisplay = (TextComponent) weaponDisplay.appendNewline();
                final String miniMessageString = MiniMessage.miniMessage().serialize(
                        LegacyComponentSerializer.legacySection().deserialize(loreLine)
                );
                weaponDisplay = weaponDisplay.append(mm.deserialize(miniMessageString));
            }
        }
        TextComponent textComponent = Component.text("\uD83D\uDDE1")
                .color(NamedTextColor.RED)
                .hoverEvent(HoverEvent.showText(weaponDisplay));
        return deathMessage.append(textComponent).append(Component.text(" " + killed.getName()));
    }
}
