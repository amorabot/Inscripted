package com.amorabot.rpgelements.commands;

import com.amorabot.rpgelements.RPGElements;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.amorabot.rpgelements.managers.UIManager;

import static com.amorabot.rpgelements.utils.Utils.*;

public class ArmorStand implements CommandExecutor {

    private UIManager UIManager;
    private RPGElements plugin;

    public ArmorStand(RPGElements plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            log("Apenas jogadores podem usar o comando ArmorStandGUI");
            return true;
        }

        Player player = (Player) sender;
        UIManager = new UIManager(plugin, player);

        UIManager.openMainASInterface();
//        Inventory inventory = Bukkit.createInventory(player, 3 * 9, color("&8&lArmor Stand GUI"));
//
//        ItemStack armorStand = new ItemStack(Material.ARMOR_STAND);
//        ItemMeta armorStandMeta = armorStand.getItemMeta();
//        armorStandMeta.setDisplayName(color("&c&lCustom Test Dummy UI"));
//        armorStandMeta.setLore(Arrays.asList(color("&7"),color("&7Build your custom armor stand")));
//        armorStand.setItemMeta(armorStandMeta);
//        inventory.setItem(13, armorStand);
//
//        ItemStack exitIndicator = new ItemStack(Material.BARRIER);
//        ItemMeta exitMeta = exitIndicator.getItemMeta();
//        exitMeta.setDisplayName(color("&c&lClick to exit"));
//        exitIndicator.setItemMeta(exitMeta);
//        inventory.setItem(26, exitIndicator);
//
//        player.openInventory(inventory);

        return true;
    }
}
