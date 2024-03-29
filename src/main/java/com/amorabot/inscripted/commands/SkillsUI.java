package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.managers.UIManager;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class SkillsUI implements TabExecutor { //Tab executor é um 2 em 1. Combina CommandExecutor com TabCompletion
    //tab completion nos dá a funcionalidade de completar args com o tab.

    private Inscripted Inscripted;
    private com.amorabot.inscripted.managers.UIManager UIManager;
    private ItemStack pointsIndicator;
    private ItemStack intelligenceIndicator;
    private ItemStack agilityIndicator;
    private ItemStack strengthIndicator;

    public SkillsUI(Inscripted plugin){
        Inscripted = plugin;

        pointsIndicator = new ItemStack(Material.EXPERIENCE_BOTTLE, 1);
        ItemMeta pointsMeta = pointsIndicator.getItemMeta();
        pointsMeta.setDisplayName(Utils.color("&e&lSkill points"));
        pointsMeta.setLore(Arrays.asList(
                Utils.color("&7You have" + "10" + "points"),
                Utils.color("&7"),
                Utils.color("Allocate points to enhance your abilities") ));
        pointsIndicator.setItemMeta(pointsMeta);

        intelligenceIndicator = new ItemStack(Material.BLUE_DYE, 1);
        ItemMeta intelligenceMeta = intelligenceIndicator.getItemMeta();
        intelligenceMeta.setDisplayName(Utils.color("&9&LIntelligence"));
        intelligenceMeta.setLore(Arrays.asList(
                Utils.color("&7"),
                Utils.color("&7Click here to allocate points or dealocate")
        ));
        intelligenceIndicator.setItemMeta(intelligenceMeta);

        agilityIndicator = new ItemStack(Material.LIME_DYE, 1);
        ItemMeta agilityeMeta = agilityIndicator.getItemMeta();
        agilityeMeta.setDisplayName(Utils.color("&A&LAgility"));
        agilityeMeta.setLore(Arrays.asList(
                Utils.color("&7"),
                Utils.color("&7Click here to allocate points")
        ));
        agilityIndicator.setItemMeta(agilityeMeta);

        strengthIndicator = new ItemStack(Material.RED_DYE, 1);
        ItemMeta strengthMeta = strengthIndicator.getItemMeta();
        strengthMeta.setDisplayName(Utils.color("&C&LStrength"));
        strengthMeta.setLore(Arrays.asList(
                Utils.color("&7"),
                Utils.color("&7Click here to allocate points")
        ));
        strengthIndicator.setItemMeta(strengthMeta);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){

        if (!(sender instanceof Player)){
            Utils.log("Esse comando é exclusivo para players.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("pluginstudies.menu")){
            player.sendMessage("&cVocê não tem permissão para usar esse comando.");
            return true;
        }
        UIManager = new UIManager(Inscripted, player);

        UIManager.openSkillsUI(pointsIndicator, intelligenceIndicator, agilityIndicator, strengthIndicator);
/*
*/
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }

    public ItemStack editItem(ItemStack item, int amount, List<String> lore){
        if (amount == 0){
            //se tentarmos colocar 0 items, resetamos para 1, que é o mínimo
            amount = 1;
        }
        item.setAmount(amount);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
}
