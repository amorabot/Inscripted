package com.amorabot.rpgelements.commands;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.Armor.Armor;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

public class GenerateArmor implements CommandExecutor {


    private final RPGElements plugin;
    public GenerateArmor(RPGElements plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        int ilvlArg = Integer.parseInt(args[0]);
        String rarityArg = args[1];
        String armorTypeArg = args[2];
        ItemRarities rarity;
        ItemTypes armorType;
        try { //Argument Validation
            rarity = ItemRarities.valueOf(rarityArg.toUpperCase());
            armorType = ItemTypes.valueOf(armorTypeArg.toUpperCase());
        } catch (IllegalArgumentException exception){
            player.sendMessage(Utils.color("&cCorrect syntax -> /armor itemLevel rarity armorType [-d]"));
            return false;
        }

        Set<ItemTypes> armorSet = Set.of(ItemTypes.HELMET, ItemTypes.CHESTPLATE, ItemTypes.LEGGINGS, ItemTypes.BOOTS);
        if (!armorSet.contains(armorType)){ //ArmorSlot Validation
            return false;
        }

        Armor randomArmor = new Armor(armorType, ilvlArg, rarity, true);
        try {
            if (args[3].equals("-d")){
                player.sendMessage(randomArmor.getName());
                player.sendMessage(randomArmor.getType().toString());
                Map<DefenceTypes, Integer> def = randomArmor.getDefencesMap();
                if (def.containsKey(DefenceTypes.ARMOR)){
                    player.sendMessage("Armor: " + def.get(DefenceTypes.ARMOR));
                }
                if (def.containsKey(DefenceTypes.DODGE)){
                    player.sendMessage("Dodge: " + def.get(DefenceTypes.DODGE));
                }
                if (def.containsKey(DefenceTypes.WARD)){
                    player.sendMessage("Ward: " + def.get(DefenceTypes.WARD));
                }
            }
            Utils.log("Running armor generation in debug mode");
        } catch (IndexOutOfBoundsException e){
            Utils.log("Running armor generation in basic mode");
        }
        player.getInventory().addItem(randomArmor.getItemForm(plugin));


        return true;
    }
}
