package com.amorabot.rpgelements.commands;

import com.amorabot.rpgelements.RPGElements;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
//        Player player = (Player) sender;
//        String action = args[0]; //show, add, remove,
//        String itemType = args[1];
//        String itemSubtype = args[2].toUpperCase();
//        String affixType = "";
//        if (args.length >= 4){
//             affixType = args[3];
//        }
//        //Enum-Mapped args
//        ItemTypes mappedType;
//        Affix mappedAffixType;
//        try {
//            mappedType = ItemTypes.valueOf(itemType.toUpperCase());
//            if (affixType.equals("")){return false;}
//            mappedAffixType = Affix.valueOf(affixType.toUpperCase());
//        } catch (IllegalArgumentException exception){
//            player.sendMessage(Utils.color("&cWrong arguments"));
//            return false;
//        }
//        ModifiersEditor<? extends Enum<?>> modEditor = null;
//
//        switch (action){
//        }
//
        return false;
    }

}
