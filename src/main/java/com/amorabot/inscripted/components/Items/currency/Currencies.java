package com.amorabot.inscripted.components.Items.currency;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierIDs;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierManager;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.components.Items.ItemBuilder;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.events.FunctionalItemAccessInterface;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static com.amorabot.inscripted.events.FunctionalItemAccessInterface.*;
import static com.amorabot.inscripted.utils.Utils.color;

public enum Currencies {

    SCROLL_OF_WISDOM("&fScroll of knowledge",Material.PAPER,
            List.of("&7Reveals the inscriptions  ",
                    "&7on &nUnidentified&7 items",
                    "",
                    "&8This scroll brings forth",
                    "&8the hidden power within"
            )) {
        @Override
        public boolean apply(ItemStack itemData) {
            //TODO: play enchanting sound
            ItemMeta itemMeta = itemData.getItemMeta();
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            if (isItemType(WEAPON_TAG,dataContainer)){
                if (isIdentified(WEAPON_TAG, dataContainer)){
                    return false;
                }
                Weapon weaponData = deserializeWeaponData(dataContainer);
                assert weaponData != null;
                weaponData.identify();
                serializeItem(weaponData, dataContainer);
                itemData.setItemMeta(itemMeta);
                weaponData.imprint(itemData, weaponData.getSubtype());
                return true;
            }
            if (isItemType(ARMOR_TAG,dataContainer)){
                if (isIdentified(ARMOR_TAG, dataContainer)){
                    return false;
                }
                Armor armorData = deserializeArmorData(dataContainer);
                assert armorData != null;
                armorData.identify();
                serializeItem(armorData, dataContainer);
                itemData.setItemMeta(itemMeta);
                armorData.imprint(itemData,armorData.getSubype());
                return true;
            }
            return false;
        }
    },
    AUGMENT("Orb of augmentation", Material.LIGHT_BLUE_DYE,
            List.of("&7Enhances the rarity  ",
                    "&7of a common item",
                    "&f&lCOMMON &8>> &9&lMAGIC",
                    "",
                    "&8This magic orb can",
                    "&8enhance mundane",
                    "&8items and give them",
                    "&8magical properties."
            )) {
        @Override
        public boolean apply(ItemStack itemData) {
            ItemMeta itemMeta = itemData.getItemMeta();
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            if (FunctionalItemAccessInterface.isCorrupted(dataContainer)){
                return false;
            }
            if (isItemType(WEAPON_TAG,dataContainer)){
                if (!isIdentified(WEAPON_TAG, dataContainer)){
                    return false;
                }
                Weapon weaponData = deserializeWeaponData(dataContainer);
                assert weaponData != null;

                //Orb routine
                if (!improvedRarityOrbUsage(ItemRarities.COMMON, ItemRarities.MAGIC, weaponData, weaponData.getSubtype())){return false;}
                //---------------------------------

                serializeItem(weaponData, dataContainer);
                itemData.setItemMeta(itemMeta);
                weaponData.imprint(itemData, weaponData.getSubtype());
                return true;
            }
            if (isItemType(ARMOR_TAG,dataContainer)){
                if (!isIdentified(ARMOR_TAG, dataContainer)){
                    return false;
                }
                Armor armorData = deserializeArmorData(dataContainer);
                assert armorData != null;

                //Orb routine
                if (!improvedRarityOrbUsage(ItemRarities.COMMON, ItemRarities.MAGIC, armorData, armorData.getSubype())){return false;}
                //---------------------------------

                serializeItem(armorData, dataContainer);
                itemData.setItemMeta(itemMeta);
                armorData.imprint(itemData,armorData.getSubype());
                return true;
            }
            return false;
        }
    },
    ALTERATION("Orb of alteration",Material.LAPIS_LAZULI,
            List.of("&7Rerolls the inscriptions  ",
                    "&7on a &9&lMAGIC &7item",
                    "",
                    "&8This magic-filled orb",
                    "&8allows experimentation",
                    "&8with new inscriptions",
                    "&8on already magic items."
            )) {
        @Override
        public boolean apply(ItemStack itemData) {
            ItemMeta itemMeta = itemData.getItemMeta();
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            if (FunctionalItemAccessInterface.isCorrupted(dataContainer)){
                return false;
            }
            if (isItemType(WEAPON_TAG,dataContainer)){
                if (!isIdentified(WEAPON_TAG, dataContainer)){
                    return false;
                }
                Weapon weaponData = deserializeWeaponData(dataContainer);
                assert weaponData != null;

                //Orb usage
                if (!weaponData.getRarity().equals(ItemRarities.MAGIC)){
                    return false;
                }
                reroll(weaponData, weaponData.getSubtype());
                //----------

                serializeItem(weaponData, dataContainer);
                itemData.setItemMeta(itemMeta);
                weaponData.imprint(itemData, weaponData.getSubtype());
                return true;
            }
            if (isItemType(ARMOR_TAG,dataContainer)){
                if (!isIdentified(ARMOR_TAG, dataContainer)){
                    return false;
                }
                Armor armorData = deserializeArmorData(dataContainer);
                assert armorData != null;

                //Orb usage
                if (!armorData.getRarity().equals(ItemRarities.MAGIC)){
                    return false;
                }
                reroll(armorData, armorData.getSubype());
                //----------

                serializeItem(armorData, dataContainer);
                itemData.setItemMeta(itemMeta);
                armorData.imprint(itemData,armorData.getSubype());
                return true;
            }
            return false;
        }
    },
    REGAL("Regal orb", Material.YELLOW_DYE,
            List.of("&7Enhances the rarity",
                    "&7of a magic item",
                    "&9&lMAGIC &8>> &e&lRARE",
                    "",
                    "&8This luxurious orb can  ",
                    "&8enhance magical items",
                    "&8and greatly increase",
                    "&8the amount of inscrip-",
                    "&8tions they can hold."
            )) {
        @Override
        public boolean apply(ItemStack itemData) {
            ItemMeta itemMeta = itemData.getItemMeta();
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            if (FunctionalItemAccessInterface.isCorrupted(dataContainer)){
                return false;
            }
            if (isItemType(WEAPON_TAG,dataContainer)){
                if (!isIdentified(WEAPON_TAG, dataContainer)){
                    return false;
                }
                Weapon weaponData = deserializeWeaponData(dataContainer);
                assert weaponData != null;

                //Orb routine
                if (!improvedRarityOrbUsage(ItemRarities.MAGIC, ItemRarities.RARE, weaponData, weaponData.getSubtype())){return false;}
                //---------------------------------

                serializeItem(weaponData, dataContainer);
                itemData.setItemMeta(itemMeta);
                weaponData.imprint(itemData, weaponData.getSubtype());
                return true;
            }
            if (isItemType(ARMOR_TAG,dataContainer)){
                if (!isIdentified(ARMOR_TAG, dataContainer)){
                    return false;
                }
                Armor armorData = deserializeArmorData(dataContainer);
                assert armorData != null;

                //Orb routine
                if (!improvedRarityOrbUsage(ItemRarities.MAGIC, ItemRarities.RARE, armorData, armorData.getSubype())){return false;}
                //---------------------------------

                serializeItem(armorData, dataContainer);
                itemData.setItemMeta(itemMeta);
                armorData.imprint(itemData,armorData.getSubype());
                return true;
            }
            return false;
        }
    },
    CHAOS("Orb of chaos",Material.HONEYCOMB,
            List.of("&7Rerolls the inscriptions",
                    "&7on a &e&lRARE &7item",
                    "",
                    "&8This chaotic orb scrambles  ",
                    "&8all inscriptions on a rare",
                    "&8item. Something exceptional",
                    "&8may come out of madness."
            )) {
        @Override
        public boolean apply(ItemStack itemData) {
            ItemMeta itemMeta = itemData.getItemMeta();
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            if (FunctionalItemAccessInterface.isCorrupted(dataContainer)){
                return false;
            }
            if (isItemType(WEAPON_TAG,dataContainer)){
                if (!isIdentified(WEAPON_TAG, dataContainer)){
                    return false;
                }
                Weapon weaponData = deserializeWeaponData(dataContainer);
                assert weaponData != null;

                //Orb usage
                if (!weaponData.getRarity().equals(ItemRarities.RARE)){
                    return false;
                }
                reroll(weaponData, weaponData.getSubtype());
                //----------

                serializeItem(weaponData, dataContainer);
                itemData.setItemMeta(itemMeta);
                weaponData.imprint(itemData, weaponData.getSubtype());
                return true;
            }
            if (isItemType(ARMOR_TAG,dataContainer)){
                if (!isIdentified(ARMOR_TAG, dataContainer)){
                    return false;
                }
                Armor armorData = deserializeArmorData(dataContainer);
                assert armorData != null;

                //Orb usage
                if (!armorData.getRarity().equals(ItemRarities.MAGIC)){
                    return false;
                }
                reroll(armorData, armorData.getSubype());
                //----------

                serializeItem(armorData, dataContainer);
                itemData.setItemMeta(itemMeta);
                armorData.imprint(itemData,armorData.getSubype());
                return true;
            }
            return false;
        }
    };

    public static final NamespacedKey PDC_ID = new NamespacedKey(Inscripted.getPlugin(), "INSCRIPTED_CURRENCY");
    public static final String CURRENCY_LORE_TAG = "&a&lCURRENCY";
    private final String displayName;
    private final Material item;
    private final List<String> description;

    Currencies(String displayName, Material itemIcon, List<String> descriptionLore){
        this.displayName = displayName;
        this.item = itemIcon;
        List<String> finalLore = new ArrayList<>();
        for (String s : descriptionLore){
            finalLore.add(color(s.indent(2)));
        }
        finalLore.add("");
        finalLore.add(color(CURRENCY_LORE_TAG));

        this.description = finalLore;
    }

    public String getDisplayName() {
        return displayName;
    }

    public abstract boolean apply(ItemStack itemData);

    public ItemStack get(int amount){
        ItemStack currency = getItemForm();
        currency.setAmount(amount);
        return currency;
    }
    private ItemStack getItemForm(){
        ItemStack currency = new ItemStack(item);
        ItemMeta itemMeta = currency.getItemMeta();

        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(PDC_ID, PersistentDataType.STRING, this.toString());

        itemMeta.setLore(description);
        itemMeta.setDisplayName(color("&a"+displayName));

        currency.setItemMeta(itemMeta);

        return currency;
    }
    public static boolean isCurrency(ItemStack item){
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        return dataContainer.has(PDC_ID);
    }






    public <SubType extends Enum<SubType> & ItemSubtype> boolean improvedRarityOrbUsage(ItemRarities from, ItemRarities to, Item itemData, SubType itemSubtype){
        if (!itemData.getRarity().equals(from)){
            return false;
        }
        Map<ModifierIDs, Map<Integer, Integer>> affixTable;
        itemData.setRarity(to);
        if (ItemBuilder.isPrefix()){ //Choose wether the new mod is a prefix or not
            //Load the respective mod table
            affixTable = ItemBuilder.getAffixTableOf(Affix.PREFIX, itemData.getCategory(), itemSubtype);
        } else {
            affixTable = ItemBuilder.getAffixTableOf(Affix.SUFFIX, itemData.getCategory(), itemSubtype);
        }
        //Lets add the mods blocked by the item's level
        Set<ModifierIDs> illegalMods = new HashSet<>(ModifierManager.checkForIllegalMods(affixTable, itemData.getIlvl()));
        ItemBuilder.addModTo(itemData, affixTable, illegalMods);
        return true;
    }

    public <SubType extends Enum<SubType> & ItemSubtype> void reroll(Item itemData, SubType subType){
        Map<ModifierIDs, Map<Integer, Integer>> prefixes = new HashMap<>();
        Map<ModifierIDs, Map<Integer, Integer>> suffixes = new HashMap<>();
        Set<ModifierIDs> illegalMods = new HashSet<>();
        ItemBuilder.fillAllPrerequisiteTablesFor(itemData.getCategory(), subType, itemData.getIlvl(), prefixes, suffixes, illegalMods);

        itemData.getModifierList().clear();
        switch (itemData.getRarity()){
            case MAGIC -> ItemBuilder.addNewMagicModSet(itemData, prefixes, suffixes, illegalMods);
            case RARE -> ItemBuilder.addNewRareModSet(itemData, prefixes, suffixes, illegalMods);
        }
    }
}
