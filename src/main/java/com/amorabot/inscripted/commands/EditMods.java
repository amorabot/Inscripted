package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierIDs;
import com.amorabot.inscripted.components.Items.Files.ModifierEditor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditMods implements CommandExecutor { //Todo: encapsular as ações na classe ModifiersEditor

    private final Inscripted plugin;
    public EditMods(Inscripted plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            return true;
        }
        if (args[0].equals("add_all")){
            ModifierEditor.setupMods();
            return true;
        }

        try {
            ModifierIDs mod = ModifierIDs.valueOf(args[0]);
            ModifierEditor.setMod(mod);
            return true;
        } catch (IllegalArgumentException exception){
            exception.printStackTrace();
        }
//
        return false;
    }

}
