package com.amorabot.inscripted.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ColorTestsCommand implements TabExecutor {
    /*
    MiniMessage.miniMessage().deserialize("<gray>Hello <name> :)", Placeholder.parsed("name", "<red>TEST"));
    returns Component.text("Hello ", NamedTextColor.GRAY).append(Component.text("TEST :)", NamedTextColor.RED));

    #bcd613 -> Mercenary new color

    #90cbf5 -> Augmented new color
    #ffc517 -> Runic new color
    #ed325a -> Relic new Color

    #f7f4d2 -> "White"
    #a8a588 -> Tinted "white"

    #8f8d7f -> Midtone (Gray)

    #403e37 -> Dark gray
    #241d1b -> Darkest text
    */

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)){
            return true;
        }
        Player player = (Player) commandSender;
        if (strings == null){return false;}
        try{
            String selectorArgument = strings[0];

            switch (selectorArgument){
                case "test":
                    String hexColor = strings[1];
                    Component demoComponent = Component.text("This is a test text! || UPPERCASES -> lowercases")
                            .appendNewline().append(Component.text("This is the BOLD test...").decorate(TextDecoration.BOLD));
                    player.sendMessage(demoComponent);
                    player.sendMessage("Color demo: " + hexColor);
                    player.sendMessage(demoComponent.color(TextColor.fromHexString(hexColor)));
                    return true;
                case "append":
                    String textToAppend = strings[1];
                    String textColor = strings[2];
                    ItemStack heldItem = player.getInventory().getItemInMainHand();
                    List<Component> lore = new ArrayList<>();

                    Component component = Component.text(textToAppend).color(TextColor.fromHexString(textColor)).decoration(TextDecoration.ITALIC, false);
                    if (!heldItem.getItemMeta().hasLore()){
                        lore.add(component);
                        heldItem.lore(lore);
                        return true;
                    }
                    List<Component> newLore = heldItem.lore();
                    newLore.add(component);
                    heldItem.lore(newLore);

            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            player.sendMessage("Invalid Call");
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
