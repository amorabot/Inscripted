package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Show implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player)){
            return false;
        }
        Player player = (Player) commandSender;
        PlayerInventory inventory = player.getInventory();
        ItemStack heldItem = inventory.getItemInMainHand();
        if (heldItem.getType().isAir()){
            Utils.log(":(");
            return false;
        }
        List<TextComponent> itemAsText = new ArrayList<>();
        if (!heldItem.hasItemMeta()){
            player.sendMessage(heldItem.getType().name().toLowerCase().replace("_", " "));
            return true;
        }
        itemAsText.add(Component.text(heldItem.getItemMeta().getDisplayName()));
        TextComponent showComponentHeader = itemAsText.get(0);

        if (heldItem.getItemMeta().hasLore()){
            MiniMessage mm = MiniMessage.miniMessage();

            for (String loreLine : heldItem.getItemMeta().getLore()){

                showComponentHeader = (TextComponent) showComponentHeader.appendNewline();
                // this just runs the legacy string through two serializers to
                final String miniMessageString = MiniMessage.miniMessage().serialize(
                        LegacyComponentSerializer.legacySection().deserialize(loreLine)
                );
                showComponentHeader = showComponentHeader.append(mm.deserialize(miniMessageString));
            }
        }
        TextComponent textComponent = Component.text("[ITEM]")
                .decorate(TextDecoration.BOLD).decorate(TextDecoration.UNDERLINED)
                .color(TextColor.color(120,120,120))
                .hoverEvent(HoverEvent.showText(showComponentHeader));
        Audience audience = Audience.audience((Audience) player);
        audience.sendMessage(textComponent);

        Utils.log("show");

        return true;
    }
}
