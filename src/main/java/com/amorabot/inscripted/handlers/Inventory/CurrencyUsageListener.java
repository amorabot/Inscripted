package com.amorabot.inscripted.handlers.Inventory;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.events.CurrencyUsageEvent;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class CurrencyUsageListener implements Listener {

    public CurrencyUsageListener(){
        Inscripted plugin = Inscripted.getPlugin();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCurrencyUsage(CurrencyUsageEvent event){
        if (event.isCancelled()){
            Utils.error("Invalid currency usage event call...");
            return;
        }
        ItemStack targetItem = event.getTargetItem();
        ItemStack currencyStack = event.getCurrencyItem();
        Player player = event.getPlayer();

//        Item targetItemData = FunctionalItemAccessInterface.deserializeGenericItemData(targetItem.getItemMeta().getPersistentDataContainer());
//
//        if (!event.getMappedCurrency().apply(targetItem, targetItemData, player)){
//            Utils.error("Invalid currency usage (CurrencyUsage EventListener)");
//            SoundAPI.playInvalidActionFor(player, player.getLocation());
//            event.setCancelled(true);
//            return;
//        }
//
//        if (targetItemData instanceof Weapon){
//            Currencies.applyChanges(targetItem, targetItemData, ((Weapon)targetItemData).getSubtype());
//        } else if (targetItemData instanceof Armor) {
//            Currencies.applyChanges(targetItem, targetItemData, ((Armor)targetItemData).getSubype());
//        }
//        currencyStack.setAmount(currencyStack.getAmount()-1);
    }
}
