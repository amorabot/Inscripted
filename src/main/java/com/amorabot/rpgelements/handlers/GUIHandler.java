package com.amorabot.rpgelements.handlers;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.managers.UIManager;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
//import pluginstudies.pluginstudies.managers.ProfileManager;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GUIHandler implements Listener {

    private com.amorabot.rpgelements.managers.UIManager UIManager;
//    private ProfileManager profileManager;
    private RPGElements plugin;

    private HashMap<UUID, ArmorStand> playersArmorStands = new HashMap<>(); //Note this data does not persist through restarts

    public GUIHandler(RPGElements p){
        plugin = p;
        Bukkit.getPluginManager().registerEvents(this, plugin);
//        profileManager = plugin.getProfileManager();
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event){

        if (!(event.getWhoClicked() instanceof Player)){
            return;
        }
        if (event.getClickedInventory() == null){
            return;
        }
        if (event.getCurrentItem() == null){
            return;
        }

        Player player = (Player) event.getWhoClicked();
//        Inventory playerInventory = player.getInventory();
        UIManager = new UIManager(plugin, player);

//        if (event.getClickedInventory().equals(inventory)){
//            return;
//        }

//        event.setCancelled(true);

        if (checkLabel(event, UIManager.getASMain())){ // ---------Armor Stand Main GUI---------
            event.setCancelled(true);
            // 1. Initial Armor Stand builder Interface Behaviour
            Material item = event.getCurrentItem().getType(); //retorna o material do item clicado

            switch (item){
                case ARMOR_STAND:
                    UIManager.closeInterface();
                    UIManager.openASCustomizationInterface();
                    break;
                case BARRIER:
                    UIManager.closeInterface();
                    break;
                default:
                    break;
            }

        }else if(checkLabel(event, UIManager.getASCustomize())){ // ---------Armor Stand Customization UI---------
            event.setCancelled(true);
            // 2. Customize Interface Behaviour
            Material item = event.getCurrentItem().getType();

            //Create a armor stand that corresponds to a specific player. Any changes to the player's armor stand
            //can be made accessing the playersArmorStands variable
            if (!(playersArmorStands.containsKey(player.getUniqueId()))){
                ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
                stand.setVisible(false);
                playersArmorStands.put(player.getUniqueId(), stand);
            }

            switch (item){
                case ARMOR_STAND:
                    Utils.msgPlayer(player, "testing hasArms");
                    UIManager.closeInterface();
                    UIManager.openConfirmMenu(item);
                    break;
                case NETHER_STAR:
                    Utils.msgPlayer(player, "testing hasGlow");
                    UIManager.closeInterface();
                    UIManager.openConfirmMenu(item);
                    break;
                case LEATHER_CHESTPLATE:
                    Utils.msgPlayer(player, "testing hasArmor");
                    UIManager.closeInterface();
                    UIManager.openConfirmMenu(item);
                    break;
                case SMOOTH_STONE_SLAB:
                    Utils.msgPlayer(player, "testing hasBase");
                    UIManager.closeInterface();
                    UIManager.openConfirmMenu(item);
                    break;
                case GREEN_WOOL:
                    if (playersArmorStands.containsKey(player.getUniqueId())){
                    playersArmorStands.get(player.getUniqueId()).setVisible(true);
                    Utils.msgPlayer(player, "armor stand created");
                    playersArmorStands.remove(player.getUniqueId()); //Once created, is unavailable for customization
                    }
                    UIManager.closeInterface();
                    UIManager.openMainASInterface();
                    break;
                case RED_WOOL:
                    if(playersArmorStands.containsKey(player.getUniqueId())){
                        playersArmorStands.get(player.getUniqueId()).setVisible(true);
                        playersArmorStands.get(player.getUniqueId()).remove(); //First we remove the entity itself
                        playersArmorStands.remove(player.getUniqueId()); //Then the key in the hashmap
                        Utils.msgPlayer(player, "cancelling dummy creation");
                    }
                    UIManager.closeInterface();
                    UIManager.openMainASInterface();
                    break;
            }

        }else if (checkLabel(event, UIManager.getConfirmLabel())){// ---------Confirmation GUI---------
            event.setCancelled(true);
            // 3.1 Confirmation GUI Behaviour
            Material clickedItem = event.getCurrentItem().getType();
            Material selectedChangeIndicator = event.getView().getItem(4).getType();


            switch (clickedItem){
                case GREEN_WOOL:
                    //Confirms the selected option and returns to the Customize GUI
                    switch (selectedChangeIndicator){
                        case ARMOR_STAND:
                            // We want to confirm the toggling of the armor stand's arms
                            if (!playersArmorStands.get(player.getUniqueId()).hasArms()){ //If no arms, give arms
                                playersArmorStands.get(player.getUniqueId()).setArms(true);
                            } else {
                                playersArmorStands.get(player.getUniqueId()).setArms(false); //Else, remove arms
                            }
                            break;
                        case NETHER_STAR:
                            // If we click confirm and the option is Glow, lets toggle it
                            if (!playersArmorStands.get(player.getUniqueId()).isGlowing()){ //If no glow, make it glow
                                playersArmorStands.get(player.getUniqueId()).setGlowing(true);
                            } else {
                                playersArmorStands.get(player.getUniqueId()).setGlowing(false); //Else, remove glow
                            }
                            break;
                        case LEATHER_CHESTPLATE:
                            break;
                        case SMOOTH_STONE_SLAB:
                            // We want to toggle the armor's stand base
                            if (!playersArmorStands.get(player.getUniqueId()).hasBasePlate()){
                                playersArmorStands.get(player.getUniqueId()).setBasePlate(true);
                            }else {
                                playersArmorStands.get(player.getUniqueId()).setBasePlate(false);
                            }
                            break;
                    }
                    Utils.msgPlayer(player, "Action confirmed, returning to the builder...");
                    UIManager.closeInterface();
                    UIManager.openASCustomizationInterface();
                    break;
                case RED_WOOL:
                    //Cancels any changes, goes back to the Customize GUI
                    UIManager.closeInterface();
                    UIManager.openASCustomizationInterface();
                    break;
                default:
                    break;
            }

        }else if(checkLabel(event, UIManager.getSkillsLabel())){//  ---------Skill allocation UI---------
            //TODO: re-implementar skill allocation
//            event.setCancelled(true);
//
////            ItemStack clickedItem = event.getCurrentItem(); //também é possível usar os materiais no switch
//            int slot = event.getSlot();
//
//            ClickType click = event.getClick();
//            if (click != ClickType.LEFT && click != ClickType.RIGHT){
////                log("testando L/R");
//                return;
//            }
//
//            Attributes attributes = profileManager.getPlayerProfile(player.getUniqueId()).getAttributes();
//            int points = attributes.getPoints(); int intelligence = attributes.getIntelligence();
//            int agility = attributes.getAgility(); int strength = attributes.getStrength();
//
//            switch (slot){ //só tem 3 slots desejados, vamos checar qual foi e então agir de acordo
//                case(19):
//                    if ((click == ClickType.RIGHT && intelligence == 0) || (click == ClickType.LEFT && intelligence == 10)){
//                        //se tentarmos passar do minimo ou do máximo de pontos, nada ocorre
//                        return;
//                    }
//
//                    //agora podemos considerar apenas os clicks, sem se preocupar com os casos extremos
//                    // LEFT -> adicionar pontos, RIGHT -> retirar
//                    if (click == ClickType.LEFT){
//                        //clicou com o left, ainda tem pontos?
//                        if (points == 0){
//                            // se não, retorne
//                            return;
//                        } else { // o player tem pontos, então vamos atualizá-los
//                            attributes.setPoints(points-1);
//                            attributes.setIntelligence(intelligence+1);
//                        }
//                    }else{ //se não é left, é right
//                        //se queremos desalocar pontos, basta alterar os stats, já que não podemos descer abaixo de 0
//                        attributes.setPoints(points+1);
//                        attributes.setIntelligence(intelligence-1);
//                    }
//                    break;
//                case(20):
//                    if ((click == ClickType.RIGHT && agility == 0) || (click == ClickType.LEFT && agility == 10)){
//                        return; //TODO arrumar sistema de allocation
//                    }
//
//                    if(click == ClickType.LEFT){
//                        if(points ==0){
//                            return;
//                        }else {
//                            attributes.setPoints(points-1);
//                            attributes.setAgility(agility+1);
//                        }
//                    }else{
//                        attributes.setPoints(points+1);
//                        attributes.setAgility(agility-1);
//                    }
//                    break;
//                case(21):
//                    if ((click == ClickType.RIGHT && strength == 0) || (click == ClickType.LEFT && strength == 10)){
//                        return;
//                    }
//
//                    if(click == ClickType.LEFT){
//                        if(points ==0){
//                            return;
//                        }else {
//                            attributes.setPoints(points-1);
//                            attributes.setStrength(strength+1);
//                        }
//                    }else{
//                        attributes.setPoints(points+1);
//                        attributes.setStrength(strength-1);
//                    }
//                    break;
//            }
//            Inventory skillsInventory = event.getInventory();
//            //vamos pegar os itens do inventário e atualizálos ao fim dos clicks, mesmo que não haja nada
//            ItemStack pointsItem = skillsInventory.getItem(4);
//            ItemStack intItem = skillsInventory.getItem(19);
//            ItemStack agiItem = skillsInventory.getItem(20);
//            ItemStack strItem = skillsInventory.getItem(21);
//
//            skillsInventory.setItem(4, editItem(pointsItem.clone(), attributes.getPoints(), Arrays.asList(
//                    color("&fYou have " + attributes.getPoints() + " points left"),
//                    color("&7"),
//                    color("Allocate points to enhance your abilities") )));
//            skillsInventory.setItem(19, editItem(intItem.clone(), attributes.getIntelligence(), Arrays.asList(
//                    color("&7You have " + "&9" + attributes.getIntelligence() + " &7points allocated"),
//                    color("&7"),
//                    color("&7Click here to allocate more"))));
//            skillsInventory.setItem(20, editItem(agiItem.clone(), attributes.getAgility(), Arrays.asList(
//                    color("&7You have " + "&a" + attributes.getAgility() + " &7points allocated"),
//                    color("&7"),
//                    color("&7Click here to allocate more"))));
//            skillsInventory.setItem(21, editItem(strItem.clone(), attributes.getStrength(), Arrays.asList(
//                    color("&7You have " + "&c" + attributes.getStrength() + " &7points allocated"),
//                    color("&7"),
//                    color("&7Click here to allocate more"))));
//
//            //1- Aplicar mudança de vida com base na Strength (valor base é 20 (double) )
//            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20D + attributes.getStrength());
//            //2- Aplicar mudança de speed baseado na Agility  (valor base é 0.2 (float) )
//            player.setWalkSpeed((float) (0.2 + ((attributes.getAgility()) / 10)*0.2));
//            //3- TODO Aplicar mudanças da Intelligence

        }

        else {

        }

    }

    public boolean checkLabel(Event event, String label){
        if (!(event instanceof InventoryClickEvent)){
            return false;
        }
        InventoryClickEvent clickEvent = (InventoryClickEvent) event;
        return clickEvent.getView().getTitle().equalsIgnoreCase(Utils.color(label));
    }
    private ItemStack editItem(ItemStack item, int amount, List<String> lore){
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
