package com.amorabot.inscripted.handlers.Inventory;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.events.CurrencyUsageEvent;
import com.amorabot.inscripted.events.FunctionalItemAccessInterface;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

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

        if (!event.getMappedCurrency().apply(targetItem)){
            Utils.error("Invalid currency usage (CurrencyUsage EventListener)");
            event.setCancelled(true);
            return;
        }

        currencyStack.setAmount(currencyStack.getAmount()-1);
        Utils.log("consuming 1 " + event.getMappedCurrency());


        List<String> updatedItemModifiers = new ArrayList<>();
        Item itemData = FunctionalItemAccessInterface.deserializeGenericItemData(targetItem.getItemMeta().getPersistentDataContainer());
        assert itemData != null;
        itemData.getRenderer().renderMods(itemData, updatedItemModifiers);
        event.getPlayer().sendMessage(Utils.color("&f&lMODS: (" + event.getMappedCurrency().getDisplayName() + ")"));
        for (String s : updatedItemModifiers){
            if (s.equals("@FOOTER@")){continue;}
            event.getPlayer().sendMessage(ColorUtils.translateColorCodes(s));
        }
        event.getPlayer().sendMessage(Utils.color("&f-----"));
    }
}
