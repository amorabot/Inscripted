package com.amorabot.inscripted.managers;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class UIManager {

    private Player player;
//    private ProfileManager profileManager;

    private final String AS_MAIN_LABEL = "&8&lArmor Stand GUI";
    private final String AS_CUSTOMIZATION_LABEL = "&2&lCustomization";
    private final String SKILLS_LABEL = "&e&lBasic skills";
    private final String CONFIRM_MENU_LABEL = "&8&lConfirm changes";


    public UIManager(Inscripted plugin, Player p){
//        profileManager = plugin.getProfileManager();
        player = p;
    }

    public String getASMain(){
        return AS_MAIN_LABEL;
    }
    public String getASCustomize(){
        return AS_CUSTOMIZATION_LABEL;
    }
    public String getConfirmLabel(){
        return CONFIRM_MENU_LABEL;
    }
    public String getSkillsLabel() {
        return SKILLS_LABEL;
    }

    public void closeInterface(){
        player.closeInventory();
    }

    public void openMainASInterface(){
        Inventory inventory = Bukkit.createInventory(player, 3 * 9, Utils.color("&8&lArmor Stand GUI"));

        ItemStack armorStand = new ItemStack(Material.ARMOR_STAND);
        ItemMeta armorStandMeta = armorStand.getItemMeta();
        armorStandMeta.setDisplayName(Utils.color("&c&lCustom Test Dummy builder"));
        armorStandMeta.setLore(Arrays.asList(Utils.color("&7"), Utils.color("&7Build your custom armor stand")));
        armorStand.setItemMeta(armorStandMeta);
        inventory.setItem(13, armorStand);

        ItemStack exitIndicator = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exitIndicator.getItemMeta();
        exitMeta.setDisplayName(Utils.color("&c&lClick to exit"));
        exitIndicator.setItemMeta(exitMeta);
        inventory.setItem(26, exitIndicator);

        player.openInventory(inventory);
    }

    public void openASCustomizationInterface(){
        Inventory inventory = Bukkit.createInventory(player, 4 * 9, Utils.color("&2&lCustomization"));

        ItemStack hasArmsIndicator = new ItemStack(Material.ARMOR_STAND);
        editItem(hasArmsIndicator, "&f&lHas Arms", Arrays.asList(
                Utils.color("&7"),
                Utils.color("&7Toggles the armor stand's arms")
        ));

        ItemStack glowsIndicator = new ItemStack(Material.NETHER_STAR);
        editItem(glowsIndicator, "&f&lGlows", Arrays.asList(
                Utils.color("&7"),
                Utils.color("&7Toggles the glowing effect")
        ));

        ItemStack hasArmorIndicator = new ItemStack(Material.LEATHER_CHESTPLATE);
        editItem(hasArmorIndicator, "&f&lHas Armor", Arrays.asList(
                Utils.color("&7"),
                Utils.color("&7Puts a armor set on the armor stand")
        ));

        ItemStack hasBase = new ItemStack(Material.SMOOTH_STONE_SLAB);
        editItem(hasBase, "&f&lHas a base", Arrays.asList(
                Utils.color("&7"),
                Utils.color("&7Toggles the base of the armor stand")
        ));

        ItemStack confirm = new ItemStack(Material.GREEN_WOOL);
        editItem(confirm, "&a&lConfirm", Arrays.asList(
                Utils.color("&7"),
                Utils.color("&7Confirms all changes and creates your armor stand.")
        ));

        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        editItem(cancel, "&4&lCancel", Arrays.asList(
                Utils.color("&7"),
                Utils.color("&7Scrap all changes")
        ));

        inventory.setItem(10, hasArmsIndicator);
        inventory.setItem(12, glowsIndicator);
        inventory.setItem(14, hasArmorIndicator);
        inventory.setItem(16, hasBase);

        inventory.setItem(30, confirm);
        inventory.setItem(32, cancel);

        player.openInventory(inventory);
    }

    public void openConfirmMenu(Material selectedIndicator){
        Inventory inventory = Bukkit.createInventory(player, 1*9, Utils.color("&8&lConfirm changes"));

        ItemStack confirm = new ItemStack(Material.GREEN_WOOL);
        editItem(confirm, "&a&lConfirm", null);

        ItemStack itemIndicator = new ItemStack(selectedIndicator);
        switch (itemIndicator.getType()){
            case ARMOR_STAND:
                editItem(itemIndicator, "&f&lToggle arms?", null);
                break;
            case NETHER_STAR:
                editItem(itemIndicator, "&f&lToggle glow?", null);
                break;
            case LEATHER_CHESTPLATE:
                editItem(itemIndicator, "&f&lEquip armor?", null);
                break;
            case SMOOTH_STONE_SLAB:
                editItem(itemIndicator, "&f&lToggle base?", null);
                break;
        }

        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        editItem(cancel, "&4&lCancel", null);

        inventory.setItem(2, confirm);
        inventory.setItem(4, itemIndicator);
        inventory.setItem(6, cancel);

        player.openInventory(inventory);
    }

    public void openSkillsUI(ItemStack points, ItemStack intelligence, ItemStack agility, ItemStack strength){
//        Attributes attributes = profileManager.getPlayerProfile(player.getUniqueId()).getAttributes();
//
//
//        Inventory skillsGUI = Bukkit.createInventory(null, 9*5, color("&e&lBasic skills"));
//        skillsGUI.setItem(4, editItem(points, attributes.getPoints(),
//                Arrays.asList(
//                        color("&fYou have " + attributes.getPoints() + " points left"),
//                        color("&7"),
//                        color("Allocate points to enhance your abilities") )));
//        skillsGUI.setItem(19, editItem(intelligence, attributes.getIntelligence(), Arrays.asList(
//                color("&7You have " + "&9" + attributes.getIntelligence() + " &7points allocated"),
//                color("&7"),
//                color("&7Click here to allocate points"))));
//        skillsGUI.setItem(20, editItem(agility, attributes.getAgility(), Arrays.asList(
//                color("&7You have " + "&a" + attributes.getAgility() + " &7points allocated"),
//                color("&7"),
//                color("&7Click here to allocate points"))));
//        skillsGUI.setItem(21, editItem(strength, attributes.getStrength(), Arrays.asList(
//                color("&7You have " + "&c" + attributes.getStrength() + " &7points allocated"),
//                color("&7"),
//                color("&7Click here to allocate points"))));
//        player.openInventory(skillsGUI);
    }

    private void editItem(ItemStack item, String displayName, List<String> lore){
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(Utils.color(displayName)); // TODO: unificar os métodos editItem()
        itemMeta.setLore(lore);

        item.setItemMeta(itemMeta);
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
