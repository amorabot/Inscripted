package com.amorabot.rpgelements.commands;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.FunctionalItems.FunctionalItemHandler;
import com.amorabot.rpgelements.components.Items.Weapon.Weapon;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class Identify implements CommandExecutor {

    private RPGElements plugin;

    public Identify(RPGElements plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            return true; //Se não é um player usando
        }
        Player player = (Player) sender;
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR){
            return true; // Se o player não estiver segurando nada
        }
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!heldItem.hasItemMeta()){
            return true; //Se ele estivar algo que não tenha ItemMeta (no caso, um persistentDataContainer)
        }
        ItemMeta heldItemMeta = heldItem.getItemMeta();
        PersistentDataContainer dataContainer = heldItemMeta.getPersistentDataContainer();

        if (FunctionalItemHandler.isWeapon(dataContainer)){
            Weapon weapon = FunctionalItemHandler.deserializeWeapon(dataContainer);
            if (weapon == null){return false;}
            weapon.identify();
            FunctionalItemHandler.serializeWeapon(weapon, dataContainer);
            heldItem.setItemMeta(heldItemMeta);
            weapon.imprint(heldItem);
        }

//        if (dataContainer.has(new NamespacedKey(plugin, "item-data"), new GenericItemContainerDataType<>(Weapon.class))){
//            Weapon weapon = dataContainer.get(new NamespacedKey(plugin, "item-data"), new GenericItemContainerDataType<>(Weapon.class));
//            assert weapon != null;
//            weapon.identify();
//            dataContainer.set(new NamespacedKey(plugin, "item-data"), new GenericItemContainerDataType<>(Weapon.class),weapon);
//            heldItem.setItemMeta(heldItemMeta);
//            weapon.render(heldItem, weapon.getRenderer());
//            //After the item's changed, equip it if in main hand     //////      Negate currency usage with items in main hand
////            if (isEquipable(event.getCurrentItem())){
////                new DelayedTask(new BukkitRunnable() {
////                    @Override
////                    public void run() {
////                        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
////                        if (isEquipable(mainHandItem)) {
////                            equip(mainHandItem, player);
////                        }
////                    }
////                }, 5L);
////            }
//        }

        return true;
    }
}