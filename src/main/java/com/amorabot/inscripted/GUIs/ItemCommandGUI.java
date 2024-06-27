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
import java.util.List;

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
        GUIButton weaponsButton = generateEquipmentSlotButton(21, ItemTypes.WEAPON);
        GUIButton bootsButton = generateEquipmentSlotButton(40, ItemTypes.BOOTS);
        GUIButton leggingsButton = generateEquipmentSlotButton(31, ItemTypes.LEGGINGS);
        GUIButton chestButton = generateEquipmentSlotButton(22, ItemTypes.CHESTPLATE);
        GUIButton helmetButton = generateEquipmentSlotButton(13, ItemTypes.HELMET);
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

    private GUIButton generateEquipmentSlotButton(int desiredSlot, ItemTypes slot){

        Item randomItem;
        if (slot.equals(ItemTypes.WEAPON)){
            randomItem = ItemBuilder.randomItem(slot, archetype.getWeaponType(), getIlvl(), getRarity(),false, false);
        } else {
            randomItem = ItemBuilder.randomItem(slot, archetype.getArmorType(), getIlvl(), getRarity(),false, false);
        }
        ItemStack itemIcon = randomItem.getItemForm(Inscripted.getPlugin());
        ItemMeta iconItemMeta = itemIcon.getItemMeta();
        iconItemMeta.setDisplayName(ColorUtils.translateColorCodes(
                getRarity().getColor()+ "&l" + getRarity().toString() +" &" +archetype.getColor()+"&l"+archetype+" "+slot));
        List<String> lore = new ArrayList<String>(){
            {
                add("");
                add(Utils.color("&8>| &lLEFT CLICK -> "+ getRarity().getColor() + getRarity().toString() +" Identified item"));
                add("");
                add(Utils.color("&8>| &lRIGHT CLICK -> "+ "&4" +"Unidentified item"));
            }
        };
        iconItemMeta.setLore(lore);
        itemIcon.setItemMeta(iconItemMeta);

        return new GUIButton(desiredSlot, itemIcon) {
            @Override
            public void leftClick(Player playerWhoClicked) {
                Item generatedItem;
                if (slot.equals(ItemTypes.WEAPON)){
                    generatedItem = ItemBuilder.randomItem(slot, archetype.getWeaponType(), getIlvl(), getRarity(),true, false);
                } else {
                    generatedItem = ItemBuilder.randomItem(slot, archetype.getArmorType(), getIlvl(), getRarity(),true, false);
                }
                if (generatedItem == null){
                    Utils.msgPlayer(playerWhoClicked, "Invalid item stats...");
                    return;
                }
                giveGeneratedItem(generatedItem, playerWhoClicked);
            }

            @Override
            public void rightClick(Player playerWhoClicked) {
                Item generatedItem;
                if (slot.equals(ItemTypes.WEAPON)){
                    generatedItem = ItemBuilder.randomItem(slot, archetype.getWeaponType(), getIlvl(), getRarity(),false, false);
                } else {
                    generatedItem = ItemBuilder.randomItem(slot, archetype.getArmorType(), getIlvl(), getRarity(),false, false);
                }
                if (generatedItem == null){
                    Utils.msgPlayer(playerWhoClicked, "Invalid item stats...");
                    return;
                }
                giveGeneratedItem(generatedItem, playerWhoClicked);
            }

            @Override
            public void shiftLeftClick(Player playerWhoClicked) {}
            @Override
            public void shiftRightClick(Player playerWhoClicked) {}
        };
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
