package pluginstudies.pluginstudies.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pluginstudies.pluginstudies.PluginStudies;
import pluginstudies.pluginstudies.components.Skills;

import java.util.Arrays;
import java.util.List;

import static pluginstudies.pluginstudies.utils.Utils.color;

public class UIManager {

    private Player player;
    private ProfileManager profileManager;

    private final String AS_MAIN_LABEL = "&8&lArmor Stand GUI";
    private final String AS_CUSTOMIZATION_LABEL = "&2&lCustomization";
    private final String SKILLS_LABEL = "&e&lBasic skills";
    private final String CONFIRM_MENU_LABEL = "&8&lConfirm changes";


    public UIManager(PluginStudies plugin, Player p){
        profileManager = plugin.getProfileManager();
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
        Inventory inventory = Bukkit.createInventory(player, 3 * 9, color("&8&lArmor Stand GUI"));

        ItemStack armorStand = new ItemStack(Material.ARMOR_STAND);
        ItemMeta armorStandMeta = armorStand.getItemMeta();
        armorStandMeta.setDisplayName(color("&c&lCustom Test Dummy builder"));
        armorStandMeta.setLore(Arrays.asList(color("&7"),color("&7Build your custom armor stand")));
        armorStand.setItemMeta(armorStandMeta);
        inventory.setItem(13, armorStand);

        ItemStack exitIndicator = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exitIndicator.getItemMeta();
        exitMeta.setDisplayName(color("&c&lClick to exit"));
        exitIndicator.setItemMeta(exitMeta);
        inventory.setItem(26, exitIndicator);

        player.openInventory(inventory);
    }

    public void openASCustomizationInterface(){
        Inventory inventory = Bukkit.createInventory(player, 4 * 9, color("&2&lCustomization"));

        ItemStack hasArmsIndicator = new ItemStack(Material.ARMOR_STAND);
        editItem(hasArmsIndicator, "&f&lHas Arms", Arrays.asList(
                color("&7"),
                color("&7Toggles the armor stand's arms")
        ));

        ItemStack glowsIndicator = new ItemStack(Material.NETHER_STAR);
        editItem(glowsIndicator, "&f&lGlows", Arrays.asList(
                color("&7"),
                color("&7Toggles the glowing effect")
        ));

        ItemStack hasArmorIndicator = new ItemStack(Material.LEATHER_CHESTPLATE);
        editItem(hasArmorIndicator, "&f&lHas Armor", Arrays.asList(
                color("&7"),
                color("&7Puts a armor set on the armor stand")
        ));

        ItemStack hasBase = new ItemStack(Material.SMOOTH_STONE_SLAB);
        editItem(hasBase, "&f&lHas a base", Arrays.asList(
                color("&7"),
                color("&7Toggles the base of the armor stand")
        ));

        ItemStack confirm = new ItemStack(Material.GREEN_WOOL);
        editItem(confirm, "&a&lConfirm", Arrays.asList(
                color("&7"),
                color("&7Confirms all changes and creates your armor stand.")
        ));

        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        editItem(cancel, "&4&lCancel", Arrays.asList(
                color("&7"),
                color("&7Scrap all changes")
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
        Inventory inventory = Bukkit.createInventory(player, 1*9, color("&8&lConfirm changes"));

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
        Skills skills = profileManager.getPlayerProfile(player.getUniqueId()).getSkills();


        Inventory skillsGUI = Bukkit.createInventory(null, 9*5, color("&e&lBasic skills"));
        skillsGUI.setItem(4, editItem(points, skills.getPoints(),
                Arrays.asList(
                        color("&fYou have " + skills.getPoints() + " points left"),
                        color("&7"),
                        color("Allocate points to enhance your abilities") )));
        skillsGUI.setItem(19, editItem(intelligence, skills.getIntelligence(), Arrays.asList(
                color("&7You have " + "&9" + skills.getIntelligence() + " &7points allocated"),
                color("&7"),
                color("&7Click here to allocate points"))));
        skillsGUI.setItem(20, editItem(agility, skills.getAgility(), Arrays.asList(
                color("&7You have " + "&a" + skills.getAgility() + " &7points allocated"),
                color("&7"),
                color("&7Click here to allocate points"))));
        skillsGUI.setItem(21, editItem(strength, skills.getStrength(), Arrays.asList(
                color("&7You have " + "&c" + skills.getStrength() + " &7points allocated"),
                color("&7"),
                color("&7Click here to allocate points"))));
        player.openInventory(skillsGUI);
    }

    private void editItem(ItemStack item, String displayName, List<String> lore){
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(color(displayName)); // TODO: unificar os métodos editItem()
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
