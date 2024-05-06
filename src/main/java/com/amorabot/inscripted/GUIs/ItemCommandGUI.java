package com.amorabot.inscripted.GUIs;

import com.amorabot.inscripted.GUIs.modules.GUI;
import com.amorabot.inscripted.GUIs.modules.GUIButton;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Tiers;
import com.amorabot.inscripted.components.Items.ItemBuilder;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemCommandGUI extends GUI{

    public static final String TITLE = Utils.color("&7&lItem selection menu:");

    private final Archetypes archetype;
    private final int ilvl;
    private final ItemRarities rarity;


//    public ItemCommandGUI(Player owner, int rows, boolean editable, boolean renderNulls) {
    public ItemCommandGUI(Player owner,
                          Archetypes archetype, int ilvl, ItemRarities rarity,
                          int rows, boolean editable, boolean renderNulls) {

        super(rows, editable, renderNulls);

        this.archetype = archetype;
        this.ilvl = ilvl;
        this.rarity = rarity;

        this.inventory = renderInventory(createButtons());
    }


    @Override
    public Inventory renderInventory(GUIButton[] UIButtons){
        Inventory cleanInventory = Inscripted.getPlugin().getServer().createInventory(this, 9*getRows()); // CAUSING PACKET/VISUAL ISSUES

        if (renderNulls()){
            ItemStack nullItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta itemMeta = nullItem.getItemMeta();
            itemMeta.setDisplayName(" ");
            nullItem.setItemMeta(itemMeta);

            for (int i = 0; i < cleanInventory.getSize(); i++){
                cleanInventory.setItem(i, nullItem);
            }
        }

        for (GUIButton currentButton : UIButtons) {
            setGUIButton(currentButton.getSlot(), currentButton);
            cleanInventory.setItem(currentButton.getSlot(), currentButton.getIcon());
        }

        return cleanInventory;
    }

    @Override
    public GUIButton[] createButtons() {
        GUIButton[] buttons = new GUIButton[6];

        GUIButton helmetButton = new GUIButton(
                13,
                Material.IRON_HELMET,1,
                false, ColorUtils.translateColorCodes("&"+getArchetype().getColor() + "&lProcedural helmet"),
                new ArrayList<String>(){
                    {
                        add("");
                        add(Utils.color("&7>&lLEFT CLICK -> "+ getRarity().getColor() + getRarity().toString() +" Identified &7helmet"));
                        add("");
                        add(Utils.color("&7>&lRIGHT CLICK ->"+ "&4" +" Unidentified &7helmet"));
                    }
                }
        ) {
            @Override
            public void leftClick(Player playerWhoClicked) {
                Armor armor = ( Armor ) ItemBuilder.randomItem(ItemTypes.HELMET,archetype.getArmorType(), ilvl, getRarity(), true, false);
                if (armor == null){
                    Utils.msgPlayer(playerWhoClicked, "Invalid armor stats...");
                    return;
                }
                giveGeneratedItem(armor, playerWhoClicked);
            }

            @Override
            public void rightClick(Player playerWhoClicked) {
                Armor armor = ( Armor ) ItemBuilder.randomItem(ItemTypes.HELMET,archetype.getArmorType(), ilvl, getRarity(), false, false);
                if (armor == null){
                    Utils.msgPlayer(playerWhoClicked, "Invalid armor stats...");
                    return;
                }
                giveGeneratedItem(armor, playerWhoClicked);
            }

            @Override
            public void shiftLeftClick(Player playerWhoClicked) {}
            @Override
            public void shiftRightClick(Player playerWhoClicked) {}
        };

        GUIButton chestButton = new GUIButton(
                22,
                Material.IRON_CHESTPLATE,1,
                false, ColorUtils.translateColorCodes("&"+getArchetype().getColor() + "&lProcedural chestplate"),
                new ArrayList<String>(){
                    {
                        add("");
                        add(Utils.color("&7>&lLEFT CLICK -> "+ getRarity().getColor() + getRarity().toString() +" Identified &7chestplate"));
                        add("");
                        add(Utils.color("&7>&lRIGHT CLICK ->"+ "&4" +" Unidentified &7chestplate"));
                    }
                }
        ) {
            @Override
            public void leftClick(Player playerWhoClicked) {
                Armor armor = ( Armor ) ItemBuilder.randomItem(ItemTypes.CHESTPLATE,archetype.getArmorType(), ilvl, getRarity(), true, false);
                if (armor == null){
                    Utils.msgPlayer(playerWhoClicked, "Invalid armor stats...");
                    return;
                }
                giveGeneratedItem(armor, playerWhoClicked);
            }

            @Override
            public void rightClick(Player playerWhoClicked) {
                Armor armor = ( Armor ) ItemBuilder.randomItem(ItemTypes.CHESTPLATE,archetype.getArmorType(), ilvl, getRarity(), false, false);
                if (armor == null){
                    Utils.msgPlayer(playerWhoClicked, "Invalid armor stats...");
                    return;
                }
                giveGeneratedItem(armor, playerWhoClicked);
            }

            @Override
            public void shiftLeftClick(Player playerWhoClicked) {}
            @Override
            public void shiftRightClick(Player playerWhoClicked) {}
        };
        GUIButton leggingsButton = new GUIButton(
                31,
                Material.IRON_LEGGINGS,1,
                false, ColorUtils.translateColorCodes("&"+getArchetype().getColor() + "&lProcedural leggings"),
                new ArrayList<String>(){
                    {
                        add("");
                        add(Utils.color("&7>&lLEFT CLICK -> "+ getRarity().getColor() + getRarity().toString() +" Identified &7leggings"));
                        add("");
                        add(Utils.color("&7>&lRIGHT CLICK ->"+ "&4" +" Unidentified &7leggings"));
                    }
                }
        ) {
            @Override
            public void leftClick(Player playerWhoClicked) {
                Armor armor = ( Armor ) ItemBuilder.randomItem(ItemTypes.LEGGINGS,archetype.getArmorType(), ilvl, getRarity(), true, false);
                if (armor == null){
                    Utils.msgPlayer(playerWhoClicked, "Invalid armor stats...");
                    return;
                }
                giveGeneratedItem(armor, playerWhoClicked);
            }

            @Override
            public void rightClick(Player playerWhoClicked) {
                Armor armor = ( Armor ) ItemBuilder.randomItem(ItemTypes.LEGGINGS,archetype.getArmorType(), ilvl, getRarity(), false, false);
                if (armor == null){
                    Utils.msgPlayer(playerWhoClicked, "Invalid armor stats...");
                    return;
                }
                giveGeneratedItem(armor, playerWhoClicked);
            }

            @Override
            public void shiftLeftClick(Player playerWhoClicked) {}
            @Override
            public void shiftRightClick(Player playerWhoClicked) {}
        };
        GUIButton bootsButton = new GUIButton(
                40, Material.IRON_BOOTS,1,
                false, ColorUtils.translateColorCodes("&"+getArchetype().getColor() + "&lProcedural boots"),
                new ArrayList<String>(){
                    {
                        add("");
                        add(Utils.color("&7>&lLEFT CLICK -> "+ getRarity().getColor() + getRarity().toString() +" Identified &7boots"));
                        add("");
                        add(Utils.color("&7>&lRIGHT CLICK ->"+ "&4" +" Unidentified &7boots"));
                    }
                }
        ) {
            @Override
            public void leftClick(Player playerWhoClicked) {
                Armor armor = ( Armor ) ItemBuilder.randomItem(ItemTypes.BOOTS,archetype.getArmorType(), ilvl, getRarity(), true, false);
                if (armor == null){
                    Utils.msgPlayer(playerWhoClicked, "Invalid armor stats...");
                    return;
                }
                giveGeneratedItem(armor, playerWhoClicked);
            }

            @Override
            public void rightClick(Player playerWhoClicked) {
                Armor armor = ( Armor ) ItemBuilder.randomItem(ItemTypes.BOOTS,archetype.getArmorType(), ilvl, getRarity(), false, false);
                if (armor == null){
                    Utils.msgPlayer(playerWhoClicked, "Invalid armor stats...");
                    return;
                }
                giveGeneratedItem(armor, playerWhoClicked);
            }

            @Override
            public void shiftLeftClick(Player playerWhoClicked) {}
            @Override
            public void shiftRightClick(Player playerWhoClicked) {}
        };
        GUIButton weaponsButton = new GUIButton(21,
                Material.IRON_SWORD,1,
                true, ColorUtils.translateColorCodes("&"+getArchetype().getColor() + "&lProcedural weapon"),
                new ArrayList<String>(){
                    {
                        add("");
                        add(Utils.color("&7>&lLEFT CLICK -> "+ getRarity().getColor() + getRarity().toString() +" Identified &7weapon"));
                        add("");
                        add(Utils.color("&7>&lRIGHT CLICK -> "+ "&4" +"Unidentified &7weapon"));
                    }
                }
        ) {
            @Override
            public void leftClick(Player playerWhoClicked) {
                Weapon weapon = ( Weapon ) ItemBuilder.randomItem(ItemTypes.WEAPON, archetype.getWeaponType(), ilvl, getRarity(),  true, false);
                if (weapon == null){
                    Utils.msgPlayer(playerWhoClicked, "Invalid weapon stats...");
                    return;
                }
                giveGeneratedItem(weapon, playerWhoClicked);
            }

            @Override
            public void rightClick(Player playerWhoClicked) {
                Weapon weapon = ( Weapon ) ItemBuilder.randomItem(ItemTypes.WEAPON, archetype.getWeaponType(), ilvl, getRarity(),  false, false);
                if (weapon == null){
                    Utils.msgPlayer(playerWhoClicked, "Invalid weapon stats...");
                    return;
                }
                giveGeneratedItem(weapon, playerWhoClicked);
            }

            @Override
            public void shiftLeftClick(Player playerWhoClicked) {}
            @Override
            public void shiftRightClick(Player playerWhoClicked) {}
        };
        GUIButton itemDescription = new GUIButton(23,
                Material.WRITABLE_BOOK,1,
                true, Utils.color("&f&lItem presets:"),
                new ArrayList<String>(){
                    {
                        add("");
                        add(ColorUtils.translateColorCodes(" &7-Archetype: &"+ getArchetype().getColor() + getArchetype().toString()));
                        add("");
                        add(Utils.color(" &7-Item level: &f"+ getIlvl()));
                        add("");
                        add(Utils.color(" &7-Tier: &f"+ Tiers.mapItemLevel(getIlvl()).toString()));
                        add("");
                        add(Utils.color(" &7-Rarity: " + getRarity().getColor() + getRarity().toString()));
                    }
                }
        ) {
            @Override
            public void leftClick(Player playerWhoClicked) {}
            @Override
            public void rightClick(Player playerWhoClicked) {}
            @Override
            public void shiftLeftClick(Player playerWhoClicked) {}
            @Override
            public void shiftRightClick(Player playerWhoClicked) {}
        };

        buttons[0] = helmetButton;
        buttons[1] = chestButton;
        buttons[2] = leggingsButton;
        buttons[3] = bootsButton;
        buttons[4] = weaponsButton;
        buttons[5] = itemDescription;
        return buttons;
    }

    private void giveGeneratedItem(Item item, Player player){
        player.getInventory().addItem(item.getItemForm(Inscripted.getPlugin()));
    }

    public Archetypes getArchetype() {
        return archetype;
    }

    public int getIlvl() {
        return ilvl;
    }

    public ItemRarities getRarity() {
        return rarity;
    }
}
