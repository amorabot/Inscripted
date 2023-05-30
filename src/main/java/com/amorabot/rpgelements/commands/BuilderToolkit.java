package com.amorabot.rpgelements.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BuilderToolkit implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){

        if (!(sender instanceof Player)){
            Bukkit.getLogger().info("Only players can use this command...");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0){
            player.sendMessage(ChatColor.RED + "Argumentos insuficientes");
            return true;
        }

        if (!player.hasPermission("pluginstudies.builder")){
            player.sendMessage(ChatColor.RED + "Você não pode usar esse comando.");
            return true;
        }

        String playerName = args[0];
        Player target = Bukkit.getServer().getPlayerExact(playerName); //retorna um player object ou null caso n dê
        if (target == null){
            player.sendMessage(ChatColor.GRAY + "Este player não está online");
            return true;
        }

        target.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
        target.getInventory().addItem(new ItemStack(Material.ARROW));
        target.getInventory().addItem(new ItemStack(Material.GUNPOWDER));
        return true;
    }
}
