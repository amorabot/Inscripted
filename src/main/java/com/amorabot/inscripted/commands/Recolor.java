package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Recolor implements CommandExecutor {
    private Inscripted plugin;

    public Recolor(Inscripted plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            return true;
        }

        Player player = (Player) sender;
        String colorArg = args[0];

        if (args.length == 1){
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ColorUtils.translateColorCodes("&" + colorArg + Utils.decolor(meta.getDisplayName())));
            item.setItemMeta(meta);
        }

        if (args.length == 2){

        }

        return true;
    }


}
