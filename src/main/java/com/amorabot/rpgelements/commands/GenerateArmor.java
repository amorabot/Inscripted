package com.amorabot.rpgelements.commands;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.Armor.Armor;
import com.amorabot.rpgelements.components.Items.Armor.ArmorModifiers;
import com.amorabot.rpgelements.components.Items.Armor.ArmorTypes;
import com.amorabot.rpgelements.components.Items.Armor.BasicArmorGenerator;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
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
        String armorSubTypeArg = args[3];
        ItemRarities rarity;
        ItemTypes armorType;
        ArmorTypes armorSubtype;
        try { //Argument Validation
            rarity = ItemRarities.valueOf(rarityArg.toUpperCase());
            armorType = ItemTypes.valueOf(armorTypeArg.toUpperCase());
            armorSubtype = ArmorTypes.valueOf(armorSubTypeArg.toUpperCase());
        } catch (IllegalArgumentException exception){
            player.sendMessage(Utils.color("&cCorrect syntax -> /armor itemLevel rarity armorType [-d]"));
            return false;
        }

        Set<ItemTypes> armorSet = Set.of(ItemTypes.HELMET, ItemTypes.CHESTPLATE, ItemTypes.LEGGINGS, ItemTypes.BOOTS);
        if (!armorSet.contains(armorType)){ //ArmorSlot Validation
            return false;
        }

        Armor randomArmor = BasicArmorGenerator.createGenericArmor(armorType, armorSubtype, ilvlArg, rarity, false, false);
        try {
            if (args[4].equals("-d")){
                assert randomArmor != null;
                for (Modifier<ArmorModifiers> mod : randomArmor.getModifiers()){
                    player.sendMessage(mod.getModifier().getDisplayName());
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
