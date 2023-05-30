package com.amorabot.rpgelements.commands;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class UpdateNBT implements CommandExecutor {

    private RPGElements plugin;

    public UpdateNBT(RPGElements plugin){
        this.plugin = plugin;

    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){

        //Filter for player senders only
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        //If the player is not holding anything, return
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR){
            return true;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

//        int ilvl = 0;
//        if (args.length > 0){
//            ilvl = Integer.parseInt(args[0]);
//        } else{
////            player.sendMessage("A item level must be passed!");
//            player.sendMessage(meta.getAsString());
//        }
        if (args.length == 0){ //No arguments were passed in
            player.sendMessage(meta.getAsString());
            if(dataContainer.isEmpty()){
                player.sendMessage("datacontainerVazio");
                return true;
            } else {
                player.sendMessage("Item tem conteúdo no dataContainer!");
                return true;
            }
//            if (dataContainer.has(new NamespacedKey(plugin, "rarity"),PersistentDataType.STRING)){
//                //Se tem uma key "rarity" que é STRING, mostre
//                String rarityDisplay = dataContainer.get(new NamespacedKey(plugin, "rarity"), PersistentDataType.STRING);
//                switch (rarityDisplay){
//                    case "common":
//                        player.sendMessage(color("&7Rarity: " + "&f&l" + rarityDisplay ));
//                        break;
//                    case "magic":
//                        player.sendMessage(color("&7Rarity: " + "&9&l" + rarityDisplay ));
//                        break;
//                    case "rare":
//                        player.sendMessage(color("&7Rarity: " + "&e&l" + rarityDisplay ));
//                        break;
//                    case "unique":
//                        player.sendMessage(color("&7Rarity: " + "&4&l" + rarityDisplay ));
//                        break;
//                }
//            }
//            if (dataContainer.has(new NamespacedKey(plugin, "ilvl"), PersistentDataType.INTEGER)){
//                //Se tem uma key "ilvl" que é INTEGER, mostre
//                player.sendMessage(color("&7ilvl: " + "&6&l" + dataContainer.get(new NamespacedKey(plugin, "ilvl"), PersistentDataType.INTEGER)));
//            }
//            if (dataContainer.has(new NamespacedKey(plugin, "equipment-type"), PersistentDataType.STRING)){
//                player.sendMessage(color("&7Type: " + dataContainer.get(new NamespacedKey(plugin, "equipment-type"), PersistentDataType.STRING)));
//            }
//            return true;
        } else if (args.length == 2) { //If 2 arguments were passed (stat - value)
            String stat = args[0];
            switch (stat){
                case "rarity":
                    //Check for existing rarity NBT
                    if (dataContainer.has(new NamespacedKey(plugin, "rarity"), PersistentDataType.STRING)){
                        player.sendMessage(Utils.color("&cRarity already defined!"));
                    } else {
                        String rarity = args[1]; // TODO checagem de valores para rarity (common, magic, rare, unique)
                        dataContainer.set(new NamespacedKey(plugin, "rarity"), PersistentDataType.STRING, rarity);

                        item.setItemMeta(meta);
                        player.sendMessage(Utils.color("&aRarity modified!"));
                    }
                    break;
                case "ilvl":
                    if (dataContainer.has(new NamespacedKey(plugin, "ilvl"), PersistentDataType.INTEGER)){
                        player.sendMessage(Utils.color("&cThe item already has a item level!"));
                    } else {
                        //The item doesnt have a ilvl yet
                        int ilvl = Integer.parseInt(args[1]); // TODO checagem se args[1] é parseavel
                        dataContainer.set(new NamespacedKey(plugin, "ilvl"), PersistentDataType.INTEGER, ilvl);

                        item.setItemMeta(meta);
                        player.sendMessage(Utils.color("&aIlvl stored!"));
                    }
                    break;
                case "equipment-type":
                    if (dataContainer.has(new NamespacedKey(plugin, "equipment-type"), PersistentDataType.STRING)){
                        player.sendMessage(Utils.color("&cItem type already defined!"));
                    } else {
                        String type = args[1]; // TODO checar todos os tipos de equipamento permitidos
                        dataContainer.set(new NamespacedKey(plugin, "equipment-type"), PersistentDataType.STRING, type);

                        item.setItemMeta(meta);
                        player.sendMessage(Utils.color("&aEquipment type stored!"));
                    }
                    break;
            }
        }

        return true;
    }
}
