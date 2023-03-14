package pluginstudies.pluginstudies.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import pluginstudies.pluginstudies.CustomDataTypes.ModifierInfoDataType;
import pluginstudies.pluginstudies.CustomDataTypes.ModifierInformation;
import pluginstudies.pluginstudies.PluginStudies;
import pluginstudies.pluginstudies.components.Attributes;
import pluginstudies.pluginstudies.components.Profile;
import pluginstudies.pluginstudies.components.Stats;
import pluginstudies.pluginstudies.managers.ProfileManager;
import pluginstudies.pluginstudies.utils.DelayedTask;

import java.util.Objects;

import static pluginstudies.pluginstudies.utils.StatCalculator.applyIntelligenceToWard;
import static pluginstudies.pluginstudies.utils.StatCalculator.applyStrengthToHealth;
import static pluginstudies.pluginstudies.utils.Utils.*;

public class WeaponEquipHandler implements Listener {
    private ProfileManager profileManager;
    private PluginStudies plugin;
    public WeaponEquipHandler(PluginStudies plugin){
        this.plugin = plugin;
        this.profileManager = plugin.getProfileManager();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event){
        Inventory inventory = event.getPlayer().getInventory();
        ItemStack heldItem = inventory.getItem(event.getNewSlot());
        ItemStack previousItem = inventory.getItem(event.getPreviousSlot());
        log("held item changed!");
//        event.getPlayer().getInventory().getHeldItemSlot();

        //Let's check fist if we are changing FROM a weapon, so we can deduce what stats to change
        if (isEquipable(previousItem)){ //If the previous item was a equipable weapon, let's remove all it's stats from the player before checking anything
            ItemMeta heldItemMeta = previousItem.getItemMeta();
            PersistentDataContainer dataContainer = heldItemMeta.getPersistentDataContainer();

            if (dataContainer.has(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType())){ //Magic - Rare - Unique
                ModifierInformation modData = dataContainer.get(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType());
                if (modData.getModifierNames().contains("STR")){
                    applyAttributes(modData, event.getPlayer(), false); //Subtracting strength
                }
            } else{ //Common
//                return; //por enquanto nada, não estou mudando o base dmg ainda.
            }
        }

        if (isEquipable(heldItem)){
            ItemMeta heldItemMeta = heldItem.getItemMeta();
            PersistentDataContainer dataContainer = heldItemMeta.getPersistentDataContainer();

            if (dataContainer.has(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType())){
                ModifierInformation modData = dataContainer.get(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType());
                event.getPlayer().sendMessage(color("&7---------------------------"));
                for (String mod : modData.getModifierNames()){
//                    int[] currentModValue = modData.getMappedModifiers().get(mod);
//                    if (currentModValue.length == 2){
//                        event.getPlayer().sendMessage(color("&c"+ mod + ": +" + currentModValue[0] + "/" + currentModValue[1]));
//                    } else{
//                        event.getPlayer().sendMessage(color("&c"+ mod + ": +" + currentModValue[0]));
//                    }
                    if (mod.equalsIgnoreCase("STR")){
                        applyAttributes(modData, event.getPlayer(), true);
                    }
                }
            } else{
                int[] baseDmg = dataContainer.get(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY);
                event.getPlayer().sendMessage(color("&7---------------------------"));
                event.getPlayer().sendMessage(color("&cDMG: " + baseDmg[0] + "/" + baseDmg[1]));
            }
        }
    }
    @EventHandler
    public void onForcedSwap(InventoryClickEvent event){
        if (!(event.getWhoClicked() instanceof Player)){
            return;
        }
        if (event.getClickedInventory() == null){
            return;
        }
        InventoryAction attemptedAction = event.getAction();
        Player player = (Player) event.getWhoClicked();
        Inventory playerInventory = player.getInventory();

        if (event.getClickedInventory().equals(playerInventory)){
            ClickType click = event.getClick();

            if (click == ClickType.DOUBLE_CLICK && (isEquipable(event.getCurrentItem()) || isEquipable(player.getItemOnCursor()))){
                if (!(event.getSlot() == player.getInventory().getHeldItemSlot())){
                    return;
                }
//                msgPlayer(player, "no double-clickin equippable items into main hand");
                event.setCancelled(true);
                return;
            }

            if (click.isShiftClick()){
                ItemStack clickedItem = event.getCurrentItem();
                Material mainHandMaterial = player.getInventory().getItemInMainHand().getType();
                if (event.getSlot() == player.getInventory().getHeldItemSlot()){ //Se é um shift-Click no slot da main hand, cancele
//                    log("no shift-clicking main hand items...");
                    event.setCancelled(true);
                    return;
                }
                //Pegamos o item que foi clicado com o shift e rodamos umas task com 5ticks de delay para saber se o item na mão é igual ao clicado
                if (mainHandMaterial == Material.AIR){
                    new DelayedTask(new BukkitRunnable() {
                        @Override
                        public void run() {
//                            player.sendMessage("testing...................");
                            if (isEquipable(player.getInventory().getItemInMainHand())){
                                applyStats(player.getInventory().getItemInMainHand(), player, true);
                            }
                        }
                    }, 5L);
                }
            }

            if (attemptedAction == InventoryAction.HOTBAR_SWAP){
                event.setCancelled(true);
                return;
            }
            if (attemptedAction == InventoryAction.SWAP_WITH_CURSOR){
                if (!(event.getSlot() == player.getInventory().getHeldItemSlot())){ //se a tentativa não for no slot da mão, ignore
                    return;
                }

                //The swap is attempted at the main hand slot and its a equipable item. Let's apply the changes.
                // If the clicked item is equipable, sub its stats then add the new item stats. Else,  just add them.
                ItemStack clickedItem = player.getInventory().getItemInMainHand();
                ItemStack itemOnCursor = player.getItemOnCursor();

                //Check if item on cursor is even equipable:
                if (!isEquipable(player.getItemOnCursor())){
                    //If not equipable, just sub the clicked item stats from player
//                    player.sendMessage("item on hand is equipable.. overriding with a normal item");
                    applyStats(clickedItem, player, false);
                    return;
                }

                if (isEquipable(clickedItem)){
//                    player.sendMessage("item on hand is equipable.. changing items normally");
                    applyStats(clickedItem, player, false);
                    applyStats(itemOnCursor, player, true);
                    return;
                } else {
//                    player.sendMessage("item on hand is not equipable. applying cursor item stats");
                    applyStats(itemOnCursor, player, true);
                    return;
                }
            }
            // The attemted interaction is not a forced hotbar swap nor a swap with cursor, lets check if its to grab item from main hand

            // se ITEM CLICAVEL É EQUIPAVEL e ESTÁ NA MAIN HAND e NÃO TEM NADA NO CURSOR
            if (isEquipable(event.getCurrentItem()) && event.getSlot() == player.getInventory().getHeldItemSlot() && player.getItemOnCursor().getType() == Material.AIR){
                player.sendMessage("item-> no item ok");
                applyStats(event.getCurrentItem(), player, false);
                return;
            }
            if (isEquipable(player.getItemOnCursor()) && event.getSlot() == player.getInventory().getHeldItemSlot() && event.getCurrentItem().getType() == Material.AIR){
                player.sendMessage("no item ->item ok");
                applyStats(player.getItemOnCursor(), player, true);
                return;
            }
        }
    }

    private boolean isEquipable(ItemStack heldItem){
        if (!(heldItem != null && heldItem.hasItemMeta())){ //If its not custom or is null
            return false;
        }
        ItemMeta heldItemMeta = heldItem.getItemMeta();
        PersistentDataContainer dataContainer = heldItemMeta.getPersistentDataContainer();
        if (!dataContainer.has(new NamespacedKey(plugin, "state"), PersistentDataType.STRING)){ //If it doesn't have an "identifiable" state
            return false;
        }
        if (!dataContainer.get(new NamespacedKey(plugin, "state"), PersistentDataType.STRING).equalsIgnoreCase("IDED")){ //If it's not identified
            return false;
        }
        if (!dataContainer.get(new NamespacedKey(plugin, "type"), PersistentDataType.STRING).equalsIgnoreCase("WEAPON")){//If it's not a weapon
            return false;
        }
        return true;
    }
    private void applyStats(ItemStack heldItem, Player player, boolean adding){
        ItemMeta heldItemMeta = heldItem.getItemMeta();
        PersistentDataContainer dataContainer = heldItemMeta.getPersistentDataContainer();

        if (dataContainer.has(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType())){
            ModifierInformation modData = dataContainer.get(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType());
            player.sendMessage(color("&7---------------------------"));
            for (String mod : modData.getModifierNames()){
//                    int[] currentModValue = modData.getMappedModifiers().get(mod);
//                    if (currentModValue.length == 2){
//                        event.getPlayer().sendMessage(color("&c"+ mod + ": +" + currentModValue[0] + "/" + currentModValue[1]));
//                    } else{
//                        event.getPlayer().sendMessage(color("&c"+ mod + ": +" + currentModValue[0]));
//                    }
                if (mod.equalsIgnoreCase("STR")){
                    applyAttributes(modData, player, adding);
                }
            }
        } else{
            int[] baseDmg = dataContainer.get(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY);
            player.sendMessage(color("&7---------------------------"));
            player.sendMessage(color("&cDMG: " + baseDmg[0] + "/" + baseDmg[1]));
        }
    }

    private void applyAttributes(ModifierInformation modData, Player player, boolean adding){
        int signal;
        Profile playerProfile = profileManager.getPlayerProfile(player.getUniqueId());
        Attributes playerAttributes = playerProfile.getAttributes();
        Stats playerStats = playerProfile.getStats();

        if (adding){
            signal = 1;
        } else {
            signal = -1;
        }

        if (modData.getModifierNames().contains("STR")){ //APPLY STRENGTH
            int[] strength = modData.getMappedModifiers().get("STR"); //get attribute from item
            // Apply change to profile
            playerAttributes.setStrength(playerAttributes.getStrength() + (strength[0])*signal);
            log("STR changed: " + playerAttributes.getStrength());
            //Apply changes to health based on STR
            playerStats.setHealth(applyStrengthToHealth(playerStats.getHealth(), playerAttributes.getStrength(),adding) );
        }
//        if (modData.getModifierNames().contains("DEX")){ //APPLY DEXTERITY
//
//        }
//        if (modData.getModifierNames().contains("INT")){ //APPLY INTELLIGENCE
//            applyIntelligenceToWard(playerStats.getWard(), playerAttributes.getIntelligence());
//        }
    }
}
