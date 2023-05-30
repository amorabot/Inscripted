package com.amorabot.rpgelements.commands;

import com.amorabot.rpgelements.RPGElements;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Menu implements Listener,CommandExecutor {
    private String invName = "Sword inventory";

    public Menu(RPGElements plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) { //aqui é checado o click do player no inventário
        //a lógica é a mesma para a interação com qualquer container
        if (!event.getView().getTitle().equals(invName)){ //pegar a janela do click, checar o titulo e comparar
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        Bukkit.getLogger().info(String.format("O item clicado estava no slot " + slot));

        event.setCancelled(true); //isso torna o evento de tentar interagir com os itens dessa view inválido
    }

    @Override //abaixo daqui são as funcionalidades de command da classe menu
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){

        if (!(sender instanceof Player)){
            sender.sendMessage("Only Players can send use this command!");
            return true;
        }

        Player player = (Player) sender;
        Inventory inv = Bukkit.createInventory(player, 9 * 3, invName);

        inv.setItem(11, getItem(new ItemStack(Material.ZOMBIE_SPAWN_EGG), "&9Spawn zombie",
                "Spawn a training zombie on demand!"));
        inv.setItem(13, getItem(new ItemStack(Material.WOODEN_SWORD), "&9Training Sword",
                "Basic training equipment", "...not very sturdy"));
        inv.setItem(15, getItem(new ItemStack(Material.GHAST_TEAR), "&9Fragment",
                "Click here to get 10 fragments"));

        player.openInventory(inv);

        return true;
    }

    private ItemStack getItem(ItemStack item, String name, String ... lore){
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();
        for (String s : lore){
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lores);
        item.setItemMeta(meta);
        return item;
    }
}
