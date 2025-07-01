package com.amorabot.inscripted.GUIs;

import com.amorabot.inscripted.GUIs.components.*;
import com.amorabot.inscripted.GUIs.modules.GUI;
import com.amorabot.inscripted.GUIs.modules.GUIButton;
import com.amorabot.inscripted.item.structure.Armor.Armor;
import com.amorabot.inscripted.item.structure.Armor.ArmorTypes;
import com.amorabot.inscripted.item.structure.EquipmentSlots;
import com.amorabot.inscripted.item.structure.ItemRarities;
import com.amorabot.inscripted.item.structure.Tiers;
import com.amorabot.inscripted.item.structure.Weapon.Weapon;
import com.amorabot.inscripted.item.structure.Weapon.WeaponTypes;
import com.amorabot.inscripted.player.Archetypes;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemCommandGUI extends GUI {

    private final Player player;
    private Tiers selectedTier;
    private ItemRarities selectedRarity;
    private Archetypes selectedArchetype;
    private ItemTypeButton.ItemType selectedItemType;

    public ItemCommandGUI(Player player, Archetypes initialArchetype, int itemLevel, ItemRarities initialRarity) {
        super(6, false, true);
        this.player = player;
        this.selectedTier = Tiers.mapItemLevel(itemLevel);
        this.selectedRarity = initialRarity != ItemRarities.RELIC ? initialRarity : ItemRarities.RUNIC;
        this.selectedArchetype = initialArchetype != Archetypes.NONE ? initialArchetype : Archetypes.MARAUDER;
        this.selectedItemType = ItemTypeButton.ItemType.WEAPON;
        
        this.inventory = renderInventory(createButtons());
    }

    @Override
    public Inventory renderInventory(GUIButton[] buttons) {
        Inventory inv = Bukkit.createInventory(this, 9 * getRows(), "§8Item Creation Interface");
        
        for (GUIButton button : buttons) {
            if (button != null) {
                inv.setItem(button.getSlot(), button.getIcon());
            }
        }
        
        // Fill empty slots with glass panes if renderNulls is enabled
        if (renderNulls()) {
            ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            filler.getItemMeta().setDisplayName(" ");
            for (int i = 0; i < inv.getSize(); i++) {
                if (inv.getItem(i) == null) {
                    inv.setItem(i, filler);
                }
            }
        }
        
        return inv;
    }

    @Override
    public GUIButton[] createButtons() {
        List<GUIButton> buttons = new ArrayList<>();

        // Tier Selection (Row 1)
        buttons.add(new GUIButton(0, Material.NAME_TAG, 1, false, "§e§lTier Selection", 
            List.of("§7Choose the item tier", "§7(determines level and power)")) {
            @Override public void leftClick(Player player) {}
            @Override public void rightClick(Player player) {}
            @Override public void shiftLeftClick(Player player) {}
            @Override public void shiftRightClick(Player player) {}
        });

        int tierSlot = 1;
        for (Tiers tier : Tiers.values()) {
            buttons.add(new TierSelectorButton(tierSlot++, tier, tier == selectedTier, this::selectTier));
        }

        // Rarity Selection (Row 2)
        buttons.add(new GUIButton(9, Material.NAME_TAG, 1, false, "§d§lRarity Selection", 
            List.of("§7Choose the item rarity", "§7(determines modifiers)")) {
            @Override public void leftClick(Player player) {}
            @Override public void rightClick(Player player) {}
            @Override public void shiftLeftClick(Player player) {}
            @Override public void shiftRightClick(Player player) {}
        });

        int raritySlot = 10;
        for (ItemRarities rarity : ItemRarities.values()) {
            if (rarity != ItemRarities.RELIC) {
                buttons.add(new RaritySelectorButton(raritySlot++, rarity, rarity == selectedRarity, this::selectRarity));
            }
        }

        // Archetype Selection (Row 3)
        buttons.add(new GUIButton(18, Material.NAME_TAG, 1, false, "§6§lArchetype Selection", 
            List.of("§7Choose the item archetype", "§7(determines weapon/armor type)")) {
            @Override public void leftClick(Player player) {}
            @Override public void rightClick(Player player) {}
            @Override public void shiftLeftClick(Player player) {}
            @Override public void shiftRightClick(Player player) {}
        });

        int archetypeSlot = 19;
        for (Archetypes archetype : Archetypes.values()) {
            if (archetype != Archetypes.NONE) {
                buttons.add(new ArchetypeSelectorButton(archetypeSlot++, archetype, archetype == selectedArchetype, this::selectArchetype));
            }
        }

        // Item Type Selection (Row 4)
        buttons.add(new GUIButton(27, Material.NAME_TAG, 1, false, "§b§lItem Type Selection", 
            List.of("§7Choose what to generate")) {
            @Override public void leftClick(Player player) {}
            @Override public void rightClick(Player player) {}
            @Override public void shiftLeftClick(Player player) {}
            @Override public void shiftRightClick(Player player) {}
        });

        int typeSlot = 28;
        for (ItemTypeButton.ItemType itemType : ItemTypeButton.ItemType.values()) {
            buttons.add(new ItemTypeButton(typeSlot++, itemType, itemType == selectedItemType, this::selectItemType));
        }

        // Generate Button (Row 6)
        boolean canGenerate = selectedTier != null && selectedRarity != null && selectedArchetype != null && selectedItemType != null;
        buttons.add(new GenerateButton(49, canGenerate, this::generateItems));

        // Return Button
        buttons.add(new ReturnButton(53));

        // Convert to array and set buttons
        GUIButton[] buttonArray = buttons.toArray(new GUIButton[0]);
        for (GUIButton button : buttonArray) {
            setGUIButton(button.getSlot(), button);
        }

        return buttonArray;
    }

    private void selectTier(Tiers tier) {
        this.selectedTier = tier;
        refreshGUI();
    }

    private void selectRarity(ItemRarities rarity) {
        this.selectedRarity = rarity;
        refreshGUI();
    }

    private void selectArchetype(Archetypes archetype) {
        this.selectedArchetype = archetype;
        refreshGUI();
    }

    private void selectItemType(ItemTypeButton.ItemType itemType) {
        this.selectedItemType = itemType;
        refreshGUI();
    }

    private void refreshGUI() {
        this.inventory = renderInventory(createButtons());
        player.openInventory(this.inventory);
    }

    private void generateItems() {
        try {
            int itemLevel = selectedTier.getMaxLevel();
            List<ItemStack> generatedItems = new ArrayList<>();

            switch (selectedItemType) {
                case WEAPON -> {
                    WeaponTypes weaponType = selectedArchetype.getWeaponType();
                    Weapon weapon = new Weapon(itemLevel, weaponType, selectedRarity, true, false);
                    generatedItems.add(weapon.getItemForm());
                }
                case ARMOR -> {
                    ArmorTypes armorType = selectedArchetype.getArmorType();
                    for (EquipmentSlots slot : EquipmentSlots.values()) {
                        if (slot == EquipmentSlots.WEAPON) continue;
                        Armor armor = new Armor(itemLevel, armorType, selectedRarity, true, false, slot);
                        generatedItems.add(armor.getItemForm());
                    }
                }
                case ALL -> {
                    // Generate weapon
                    WeaponTypes weaponType = selectedArchetype.getWeaponType();
                    Weapon weapon = new Weapon(itemLevel, weaponType, selectedRarity, true, false);
                    generatedItems.add(weapon.getItemForm());
                    
                    // Generate armor set
                    ArmorTypes armorType = selectedArchetype.getArmorType();
                    for (EquipmentSlots slot : EquipmentSlots.values()) {
                        if (slot == EquipmentSlots.WEAPON) continue;
                        Armor armor = new Armor(itemLevel, armorType, selectedRarity, true, false, slot);
                        generatedItems.add(armor.getItemForm());
                    }
                }
            }

            // Add items to player inventory
            for (ItemStack item : generatedItems) {
                player.getInventory().addItem(item);
            }

            Utils.msgPlayer(player, "§a§l✓ §aGenerated " + generatedItems.size() + " items!");
            player.closeInventory();

        } catch (Exception e) {
            Utils.error("Failed to generate items for player " + player.getName() + ": " + e.getMessage());
            Utils.msgPlayer(player, "§c§lError generating items! Check console for details.");
        }
    }
}