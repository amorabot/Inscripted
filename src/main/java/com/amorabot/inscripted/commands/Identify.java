package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Items.currency.Currencies;
import com.amorabot.inscripted.events.FunctionalItemAccessInterface;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import static com.amorabot.inscripted.events.FunctionalItemAccessInterface.*;

public class Identify implements CommandExecutor {

    private Inscripted plugin;

    public Identify(Inscripted plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            return true; //Se não é um player usando
        }
        Player player = (Player) sender;
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR){
            player.getInventory().addItem(Currencies.AUGMENT.get(2));
            player.getInventory().addItem(Currencies.REGAL.get(2));
            player.getInventory().addItem(Currencies.ALTERATION.get(10));
            player.getInventory().addItem(Currencies.CHAOS.get(10));
            return true; // Se o player não estiver segurando nada
        }
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!heldItem.hasItemMeta()){
            return true; //Se ele estivar algo que não tenha ItemMeta (no caso, um persistentDataContainer)
        }
        ItemMeta heldItemMeta = heldItem.getItemMeta();
        PersistentDataContainer dataContainer = heldItemMeta.getPersistentDataContainer();

        if (isIdentified(WEAPON_TAG, dataContainer)){
            player.sendMessage(ColorUtils.translateColorCodes(DefenceTypes.DODGE.getTextColor()+ "&lThis weapon is already identified!"));
        }
        if (isIdentified(ARMOR_TAG, dataContainer)){
            player.sendMessage(ColorUtils.translateColorCodes(DefenceTypes.DODGE.getTextColor()+ "&lThis armor is already identified!"));
        }

        Currencies.SCROLL_OF_WISDOM.apply(heldItem);
        player.getInventory().addItem(Currencies.SCROLL_OF_WISDOM.get(7));

        return true;
    }
}