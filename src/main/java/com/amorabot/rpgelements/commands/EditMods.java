package com.amorabot.rpgelements.commands;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Affix;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.WeaponModifiers;
import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.Files.ModifiersEditor;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EditMods implements CommandExecutor {

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
        String itemSubtype = args[1];
        String affixType = "";
        if (args.length >= 3){
             affixType = args[2];
        }
        ModifiersEditor<? extends Enum<?>> modEditor = null;

        //Enum mapping inference
//        ItemTypes root;
//        Enum<?> subTypeEnum;
        String uppercaseSubtype = itemSubtype.toUpperCase();
        String uppercaseAffixType = affixType.toUpperCase();
        try{
            for (ItemTypes type : ItemTypes.values()){
                if (type.getSubtypes() == null){
                    continue;
                }
                List<String> localSubtypes = new ArrayList<>();
                for (Enum<?> subType : type.getSubtypes()){
                    localSubtypes.add(subType.toString());
                }
                if (localSubtypes.contains(uppercaseSubtype)){
//                    root = type;
                    switch (type){
                        case WEAPON -> modEditor = new ModifiersEditor<>(this.plugin, WeaponModifiers.class);
                        //                            subTypeEnum = WeaponTypes.valueOf(uppercaseSubtype);
                        //                            WeaponTypes weaponType = (WeaponTypes) subTypeEnum;
                        case ARMOR -> {
                            //
                        }
                        default -> {
                            //a
                        }
                    }
                }
            }

        } catch(IllegalArgumentException exception){
            return false;
        }
        assert modEditor != null;
        Map<String, Map<Integer, int[]>> mods = modEditor.getModsByAffixType(itemSubtype, affixType);
        if (mods == null){
            player.sendMessage("Wrong syntax, try `action` `itemSubType` [affixType]");
            return true;
        }

        switch (action){
            case "show":
                Set<String> modNames = mods.keySet();
                player.sendMessage(Utils.color("&e&b"+affixType+" List"));
                for (String name : modNames){
                    player.sendMessage(name);
                }
                return true;
            case "add":
                try {
                    Affix affix = Affix.valueOf(uppercaseAffixType);
                    modEditor.addMod(affix, mods, args[3]);
                }catch (IllegalArgumentException exception){
                    Utils.log("Invalid affixType");
                    return false;
                }
                break;
            case "remove":
                modEditor.removeMod(mods, args[3]);
                break;
            default:
                break;
        }

        return false;
    }

}
