package com.amorabot.rpgelements.commands;

import com.amorabot.rpgelements.components.Items.Armor.ArmorModifiers;
import com.amorabot.rpgelements.components.Items.Armor.ArmorTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Affix;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.rpgelements.components.Items.Files.ModifiersJSON;
import com.amorabot.rpgelements.components.Items.Weapon.WeaponModifiers;
import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.Files.ModifiersEditor;
import com.amorabot.rpgelements.components.Items.Weapon.WeaponTypes;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

public class EditMods implements CommandExecutor { //Todo: encapsular as ações na classe ModifiersEditor

    private final RPGElements plugin;
    public EditMods(RPGElements plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        String action = args[0]; //show, add, remove,
        String itemType = args[1];
        String itemSubtype = args[2].toUpperCase();
        String affixType = "";
        if (args.length >= 4){
             affixType = args[3];
        }
        //Enum-Mapped args
        ItemTypes mappedType;
        Affix mappedAffixType;
        try {
            mappedType = ItemTypes.valueOf(itemType.toUpperCase());
            if (affixType.equals("")){return false;}
            mappedAffixType = Affix.valueOf(affixType.toUpperCase());
        } catch (IllegalArgumentException exception){
            player.sendMessage(Utils.color("&cWrong arguments"));
            return false;
        }
        ModifiersEditor<? extends Enum<?>> modEditor = null;

        switch (action){
            case "createfile":
                if (!ModifiersEditor.fileExists()){
                    try {
                        player.sendMessage("File created");
                        ModifiersEditor.createEmptyJSON();
                        return true;
                    } catch (IOException e) {
                        player.sendMessage("File not found");
                        return false;
                    }
                }
                player.sendMessage("File already exists");
                return true;
            case "addroot": //ItemTypes type -> add all subtypes if ARMOR -> add all prefixTypes for each one
                switch (mappedType){
                    case WEAPON -> {
                        modEditor = new ModifiersEditor<>(WeaponModifiers.class);
                        Map<String, Map<String, Map<String, Map<Integer, int[]>>>> subTypesMap = new HashMap<>();

                        Map<String, Map<String, Map<Integer, int[]>>> affixTypeMap = new HashMap<>();

                        Map<String, Map<Integer, int[]>> modValueMap = new HashMap<>(); //Contains a filler modifier
                        Map<Integer, int[]> valueMapTemplate = new HashMap<>();
                        valueMapTemplate.put(0, new int[2]);
                        valueMapTemplate.put(1, new int[2]);// 2 filler entries, 0 -> 0,0     1 -> 0,0

                        modValueMap.put("FILLER-MOD", valueMapTemplate); //For each affixType, a fillerMod should be added
                        for (Affix affix : Affix.values()){
                            affixTypeMap.put(affix.toString(), modValueMap);
                        }
                        //For each subtype, a template with all mods should be added
                        for (WeaponTypes weaponTypes : WeaponTypes.values()){
                            subTypesMap.put(weaponTypes.toString(), affixTypeMap);
                        }
                        //In the mods map, the entry for WEAPON should be added, adding all its subtypes
                        modEditor.persistJSONData(mappedType.toString(), subTypesMap);
                        player.sendMessage(mappedType + " Root added (overwriten)");
                        return true;
                    }
                    default -> {
                        modEditor = new ModifiersEditor<>(ArmorModifiers.class);
                        Map<String, Map<String, Map<String, Map<Integer, int[]>>>> subTypesMap = new HashMap<>();

                        Map<String, Map<String, Map<Integer, int[]>>> affixTypeMap = new HashMap<>();

                        Map<String, Map<Integer, int[]>> modValueMap = new HashMap<>(); //Contains a filler modifier
                        Map<Integer, int[]> valueMapTemplate = new HashMap<>();
                        valueMapTemplate.put(0, new int[2]);
                        valueMapTemplate.put(1, new int[2]);// 2 filler entries, 0 -> 0,0     1 -> 0,0

                        modValueMap.put("FILLER-MOD", valueMapTemplate); //For each affixType, a fillerMod should be added
                        for (Affix affix : Affix.values()){
                            affixTypeMap.put(affix.toString(), modValueMap);
                        }
                        //For each subtype, a template with all mods should be added
                        for (ArmorTypes armorTypes : ArmorTypes.values()){
                            subTypesMap.put(armorTypes.toString(), affixTypeMap);
                        }
                        //In the mods map, the entry for ARMORPIECE should be added, adding all its subtypes
                        modEditor.persistJSONData(mappedType.toString(), subTypesMap);
                        player.sendMessage(mappedType + " Root added (overwriten)");
                        return true;
                    }
                }
            case "show": //type , subtype (CARVED_PLATING, SWORD) affixtype

                return true;
            case "add": //type[1] , subtype[2] (CARVED_PLATING, SWORD) affixtype[3] modifierName[4]
                switch (mappedType){
                    case WEAPON -> {
                        //
                    }
                    default -> {
                        ArmorTypes armorSubType;
                        ArmorModifiers modEntry;
                        String modText = args[4].toUpperCase();
                        try {
                            armorSubType = ArmorTypes.valueOf(itemSubtype);
                            modEntry = ArmorModifiers.valueOf(modText);
                        }catch (IllegalArgumentException exception){
                            player.sendMessage(Utils.color("&cWrong arguments"));
                            return false;
                        }
                        modEditor = new ModifiersEditor<>(ArmorModifiers.class);
                        if (modEntry.getAffixType() != mappedAffixType){
                            player.sendMessage(Utils.color("&cTrying to add: " + mappedAffixType + " / Given: " + modEntry.getAffixType()));
                            return false;
                        }
                        modEditor.addModifier(mappedType, armorSubType.toString(), mappedAffixType, modText);
                        return true;
                    }
                }
                break;
            case "remove":
                switch (mappedType){
                    case WEAPON -> {
                        //
                    }
                    default -> {
                        ArmorTypes armorSubType;
                        try {
                            armorSubType = ArmorTypes.valueOf(itemSubtype);
                        }catch (IllegalArgumentException exception){
                            player.sendMessage(Utils.color("&cWrong arguments"));
                            return false;
                        }
                        modEditor = new ModifiersEditor<>(ArmorModifiers.class);
                        modEditor.removeModifier(mappedType, armorSubType.toString(), mappedAffixType, args[4].toUpperCase());
                    }
                }
                break;
            case "clear":
                switch (mappedType){
                    case WEAPON -> {
                        //
                    }
                    default -> {
                        ArmorTypes armorSubType;
                        try {
                            armorSubType = ArmorTypes.valueOf(itemSubtype);
                        }catch (IllegalArgumentException exception){
                            player.sendMessage(Utils.color("&cWrong arguments"));
                            return false;
                        }
                        modEditor = new ModifiersEditor<>(ArmorModifiers.class);
                        modEditor.clearAffix(mappedType, armorSubType.toString(), mappedAffixType);
                        return true;
                    }
                }
                break;
            default:
                break;
        }

        return false;
    }

}
