package com.amorabot.inscripted.components.Items.currency;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.Armor.ArmorTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Implicits;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.inscripted.components.Items.DataStructures.Modifier;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierIDs;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierManager;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.components.Items.ItemBuilder;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.events.FunctionalItemAccessInterface;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.CraftingUtils;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static com.amorabot.inscripted.events.FunctionalItemAccessInterface.*;
import static com.amorabot.inscripted.utils.Utils.color;

public enum Currencies { //TODO: Functional programming solution for orb routines?

    SCROLL_OF_WISDOM("&fScroll of knowledge",Material.PAPER,
            List.of("&7Reveals the inscriptions  ",
                    "&7on &nUnidentified&7 items",
                    "",
                    "&8This scroll brings forth",
                    "&8the hidden power within"
            )) {
        @Override
        public boolean apply(ItemStack itemStack, Item itemData, Player player) {
            if (itemData == null){return false;}
            if (itemData.isIdentified()){return false;}

            //Orb routine
            itemData.identify();
            SoundAPI.playEnchantingSoundFor(player, player.getLocation(), true);
            return true;
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
        public boolean apply(ItemStack itemStack, Item itemData, Player player) {
            if (itemData == null){return false;}
            if (itemData.isCorrupted()){return false;}
            if (!itemData.isIdentified()){return false;}

            if (itemData instanceof Weapon){
                WeaponTypes subType = ((Weapon)itemData).getSubtype();
                //Orb routine
                if (!improvedRarityOrbUsage(ItemRarities.COMMON, ItemRarities.MAGIC, itemData, subType)){return false;}
            } else if (itemData instanceof Armor) {
                ArmorTypes subType = ((Armor)itemData).getSubype();
                //Orb routine
                if (!improvedRarityOrbUsage(ItemRarities.COMMON, ItemRarities.MAGIC, itemData, subType)){return false;}
            } else {
                return false;
            }
            SoundAPI.playEnchantingSoundFor(player, player.getLocation(), true);
            return true;
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
        public boolean apply(ItemStack itemStack, Item itemData, Player player) {
            if (itemData == null){return false;}
            if (itemData.isCorrupted()){return false;}
            if (!itemData.isIdentified()){return false;}

            if (!itemData.getRarity().equals(ItemRarities.MAGIC)){return false;}
            if (itemData instanceof Weapon){
                WeaponTypes subType = ((Weapon)itemData).getSubtype();
                //Orb routine
                reroll(itemData, subType);
            } else if (itemData instanceof Armor) {
                ArmorTypes subType = ((Armor)itemData).getSubype();
                //Orb routine
                reroll(itemData, subType);
            } else {
                return false;
            }
            SoundAPI.playEnchantingSoundFor(player, player.getLocation(), true);
            return true;
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
        public boolean apply(ItemStack itemStack, Item itemData, Player player) {
            if (itemData == null){return false;}
            if (itemData.isCorrupted()){return false;}
            if (!itemData.isIdentified()){return false;}

            if (itemData instanceof Weapon){
                WeaponTypes subType = ((Weapon)itemData).getSubtype();
                //Orb routine
                if (!improvedRarityOrbUsage(ItemRarities.MAGIC, ItemRarities.RARE, itemData, subType)){return false;}
            } else if (itemData instanceof Armor) {
                ArmorTypes subType = ((Armor)itemData).getSubype();
                //Orb routine
                if (!improvedRarityOrbUsage(ItemRarities.MAGIC, ItemRarities.RARE, itemData, subType)){return false;}
            } else {
                return false;
            }
            SoundAPI.playEnchantingSoundFor(player, player.getLocation(), true);
            return true;
        }
    },
    CHAOS("Orb of chaos",Material.ORANGE_DYE,
            List.of("&7Rerolls the inscriptions",
                    "&7on a &e&lRARE &7item",
                    "",
                    "&8This chaotic orb scrambles  ",
                    "&8all inscriptions on a rare",
                    "&8item. Something exceptional",
                    "&8may come out of madness."
            )) {
        @Override
        public boolean apply(ItemStack itemStack, Item itemData, Player player) {
            if (itemData == null){return false;}
            if (itemData.isCorrupted()){return false;}
            if (!itemData.isIdentified()){return false;}

            if (!itemData.getRarity().equals(ItemRarities.RARE)){return false;}
            if (itemData instanceof Weapon){
                WeaponTypes subType = ((Weapon)itemData).getSubtype();
                //Orb routine
                reroll(itemData, subType);
            } else if (itemData instanceof Armor) {
                ArmorTypes subType = ((Armor)itemData).getSubype();
                //Orb routine
                reroll(itemData, subType);
            } else {
                return false;
            }
            SoundAPI.playEnchantingSoundFor(player, player.getLocation(), true);
            return true;
        }
    },
    MAGIC_CHISEL("Magic chisel",Material.INK_SAC,
          List.of("&7Adds one random inscription  ",
                    "&7on a &9&lMAGIC &7item",
                  "",
                  "&8This magic-infused chisel",
                  "&8is the perfect tool to shape",
                  "&8magic items to one's needs."
    )) {
        @Override
        public boolean apply(ItemStack itemStack, Item itemData, Player player) {
            if (itemData == null){return false;}
            if (itemData.isCorrupted()){return false;}
            if (!itemData.isIdentified()){return false;}

            if (!itemData.getRarity().equals(ItemRarities.MAGIC)){return false;}
            if (itemData instanceof Weapon){
                WeaponTypes subType = ((Weapon)itemData).getSubtype();
                //Orb routine
                if (!addRandomInscriptionTo(itemData, subType)){return false;}
            } else if (itemData instanceof Armor) {
                ArmorTypes subType = ((Armor)itemData).getSubype();
                //Orb routine
                if (!addRandomInscriptionTo(itemData, subType)){return false;}
            } else {
                return false;
            }
            SoundAPI.playEnchantingSoundFor(player, player.getLocation(), true);
            return true;
        }
    },
    PRISTINE_CHISEL("Pristine chisel",Material.GLOW_INK_SAC,
            List.of("&7Adds one random inscription  ",
                    "&7on a &e&lRARE &7item",
                    "",
                    "&8This exceptional chisel is",
                    "&8ideal for tinkering with",
                    "&8highly unstable items."
            )) {
        @Override
        public boolean apply(ItemStack itemStack, Item itemData, Player player) {
            if (itemData == null){return false;}
            if (itemData.isCorrupted()){return false;}
            if (!itemData.isIdentified()){return false;}

            if (!itemData.getRarity().equals(ItemRarities.RARE)){return false;}
            if (itemData instanceof Weapon){
                WeaponTypes subType = ((Weapon)itemData).getSubtype();
                //Orb routine
                if (!addRandomInscriptionTo(itemData, subType)){return false;}
            } else if (itemData instanceof Armor) {
                ArmorTypes subType = ((Armor)itemData).getSubype();
                //Orb routine
                if (!addRandomInscriptionTo(itemData, subType)){return false;}
            } else {
                return false;
            }
            SoundAPI.playEnchantingSoundFor(player, player.getLocation(), true);
            return true;
        }
    },
    NULLIFYING("Nullifying orb",Material.WHITE_DYE,
            List.of("&7Clears all non-imbued inscr.  ",
                    "&7on a &9&lMAGIC &7or &e&lRARE &7item",
                    "",
                    "&8This orb can cleanse the",
                    "&8magical properties of an",
                    "&8item, removing all non",
                    "&8inbued inscriptions on it."
            )) {
        @Override
        public boolean apply(ItemStack itemStack, Item itemData, Player player) {
            if (itemData == null){return false;}
            if (itemData.isCorrupted()){return false;}
            if (!itemData.isIdentified()){return false;}

            ItemRarities rarity = itemData.getRarity();
            if (!rarity.equals(ItemRarities.MAGIC) && !rarity.equals(ItemRarities.RARE)){return false;}
            if (itemData.getModifierList().size() == 1 && itemData.getModifierList().get(0).isImbued()){return false;}

            List<Modifier> modList = itemData.getModifierList();
            Modifier imbuedMod = null;
            for (Modifier mod : modList){
                if (mod.isImbued()){
                    imbuedMod = mod;
                }
            }
            modList.clear();
            if (imbuedMod!=null){
                modList.add(imbuedMod);
                itemData.setRarity(ItemRarities.MAGIC);
            } else {
                itemData.setRarity(ItemRarities.COMMON);
                itemData.getModifierList().clear();
            }
            SoundAPI.playEnchantingSoundFor(player, player.getLocation(), true);
            return true;
        }
    },
    IMBUIMENT_SEAL("Imbuement seal",Material.BLAZE_POWDER,
            List.of("&7Seals a random inscription onto ",
                    "&7a item, turning it &6&nImbued&7.",
                    "",
                    "&8This singular seal is capable",
                    "&8of engraving a inscription",
                    "&8onto a item's very core,",
                    "&8making it carry an immutable mark.  ",
                    "",
                    "&8*Imbued: Permanent and immutable."
            )) {
        @Override
        public boolean apply(ItemStack itemStack, Item itemData, Player player) {
            if (itemData == null){return false;}
            if (itemData.isCorrupted()){return false;}
            if (!itemData.isIdentified()){return false;}

            ItemRarities rarity = itemData.getRarity();
            if (!rarity.equals(ItemRarities.MAGIC) && !rarity.equals(ItemRarities.RARE)){return false;}
            List<Modifier> modList = itemData.getModifierList();
            for (Modifier mod : modList){//If theres a imbued mod already, invalid use
                if (mod.isImbued()){
                    return false;
                }
            }
            int chosenModIndex = CraftingUtils.getRandomNumber(0, modList.size()-1);
            Modifier chosenMod = modList.get(chosenModIndex);
            chosenMod.imbue();
            SoundAPI.playEnchantingSoundFor(player, player.getLocation(), true);
            return true;
        }
    },
    PROFANE_TENDRILS("&4Profane tendrils",Material.NETHER_WART,
            List.of("&7Use this on a item to  ",
                    "&7to turn it &4&nCorrupted&7.",
                    "",
                    "&8Those pulsating, vile,",
                    "&8tendrils look severed.",
                    "&8Its strong miasma can",
                    "&8corrupt even the most",
                    "&8righteous of warriors.",
                    "",
                    "&8*Corrupted: The item can  ",
                    "&8recieve profane power,",
                    "&8but at a cost..."
            )) {
        @Override
        public boolean apply(ItemStack itemStack, Item itemData, Player player) {
            if (itemData == null){return false;}
            if (itemData.isCorrupted()){return false;}
            if (!itemData.isIdentified()){return false;}

            //For corruption orbs, the changes are applied later (the event that triggered it handles that behaviour)
            itemData.corrupt();
            int outcome = rollCorruptionOutcome();
            switch (outcome){
                case -1:
                    //Failed
                    itemStack.setAmount(0);
                    SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "block.fire.extinguish", 0.3f, 0.5f);
                    SoundAPI.playBreakSoundFor(player);
                    break;
                case 0:
                    //Neutral (Nothing happens)
                    SoundAPI.playEnchantingSoundFor(player, player.getLocation(), false);
                    break;
                case 1:
                    //Successful corruption
                    //Add extra fluff
                    SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "block.amethyst_block.step", 1.0f, 0.7f);
                    break;
            }

            if (itemData instanceof Weapon weaponData){
                if (outcome == 1){
                    weaponData.setImplicit(Implicits.getImplicitFor(weaponData.getSubtype(), weaponData.isCorrupted()));
                }
            } else if (itemData instanceof Armor armorData) {
                if (outcome == 1){
                    armorData.setImplicit(Implicits.getImplicitFor(armorData.getSubype(), armorData.isCorrupted()));
                }
            } else {
                return false;
            }
            return true;
        }
    },
    SHARPENING_WHETSTONE("Sharpening whetstone",Material.CLAY_BALL,
            List.of("&7Can be used on weapons  ",
                    "&7to increase their quality,",
                    "&7improving their &6base",
                    "&6physical damage \uD83D\uDDE1 \uD83C\uDFF9&7.",
                    "",
                    "&8Those whetstones are",
                    "&8commonly used by bla-",
                    "&8cksmiths for polishing",
                    "&8and sharpening wea-",
                    "&8pons of any kind.",
                    "&8Seeking perfection  ",
                    "&8comes with a risk.",
                    "",
                    "&8*Quality: +5% on a item's",
                    "&8main stat per +1 quality.",
                    "&8Maximum quality is +10."
            )) {
        @Override
        public boolean apply(ItemStack itemStack, Item itemData, Player player) {
            if (itemData == null){return false;}
            if (itemData.isCorrupted()){return false;}
            if (!itemData.isIdentified()){return false;}

            if (itemData instanceof Weapon){
                switch (itemData.improveQuality()){
                    case -1:
                        return false;
                    case 0:
                        Utils.log("quality :D");
                        break;
                    case 1:
                        break;
                }
            }else {
                return false;
            }
            SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "item.axe.scrape", 0.5f, 1.7f);
            return true;
        }
    },
    ARMOR_SCRAP("Armor scrap",Material.PRISMARINE_CRYSTALS,
            List.of("&7Can be used on armors  ",
                    "&7to increase their quality,",
                    "&7improving their base",
                    "&7defensive stats "+ DefenceTypes.HEALTH.getTextColor() + DefenceTypes.HEALTH.getSpecialChar() + " "
                            + DefenceTypes.WARD.getTextColor() + DefenceTypes.WARD.getSpecialChar() + " "
                            + DefenceTypes.ARMOR.getTextColor() + DefenceTypes.ARMOR.getSpecialChar() + " "
                            + DefenceTypes.DODGE.getTextColor() + DefenceTypes.DODGE.getSpecialChar() + " "
                            +"&7.",
                    "",
                    "&8Those armor fragments",
                    "&8can be used to reinforce",
                    "&8any armor piece.",
                    "&8That process may render",
                    "&8it useless if done with-",
                    "&8out caution.",
                    "",
                    "&8*Quality: +5% on a item's",
                    "&8main stat per +1 quality.",
                    "&8Maximum quality is +10."
            )) {
        @Override
        public boolean apply(ItemStack itemStack, Item itemData, Player player) {
            if (itemData == null){return false;}
            if (itemData.isCorrupted()){return false;}
            if (!itemData.isIdentified()){return false;}

            if (itemData instanceof Armor){
                switch (itemData.improveQuality()){
                    case -1:
                        return false;
                    case 0:
                        Utils.log("quality na armadura :D");
                        break;
                    case 1:
                        break;
                }
            }else {
                return false;
            }
            SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "item.axe.scrape", 0.5f, 1.7f);
            return true;
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
            finalLore.add(ColorUtils.translateColorCodes(s.indent(2)));
        }
        finalLore.add("");
        finalLore.add(color(CURRENCY_LORE_TAG));

        this.description = finalLore;
    }

    public String getDisplayName() {
        return displayName;
    }

    public abstract boolean apply(ItemStack itemStack, Item itemData, Player player);

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

        switch (itemData.getRarity()){
            case MAGIC -> ItemBuilder.addNewMagicModSet(itemData, prefixes, suffixes, illegalMods);
            case RARE -> ItemBuilder.addNewRareModSet(itemData, prefixes, suffixes, illegalMods);
        }
    }

    public <SubType extends Enum<SubType> & ItemSubtype> boolean addRandomInscriptionTo(Item itemData, SubType subType){
        Map<ModifierIDs, Map<Integer, Integer>> prefixes = new HashMap<>();
        Map<ModifierIDs, Map<Integer, Integer>> suffixes = new HashMap<>();
        Set<ModifierIDs> illegalMods = new HashSet<>();
        ItemBuilder.fillAllPrerequisiteTablesFor(itemData.getCategory(), subType, itemData.getIlvl(), prefixes, suffixes, illegalMods);

        List<Modifier> itemMods = itemData.getModifierList();

        switch (itemData.getRarity()){
            case MAGIC -> {
                if (itemMods.size() >= ItemRarities.MAGIC.getMaxMods()){
                    return false;
                }
                if (ItemBuilder.isPrefix()){
                    ItemBuilder.addModTo(itemData, prefixes, illegalMods);
                } else {
                    ItemBuilder.addModTo(itemData, suffixes, illegalMods);
                }
                return true;
            }
            case RARE -> {
                if (itemMods.size() >= ItemRarities.RARE.getMaxMods()){
                    return false;
                }
                int p = 0;
                int s = 0;
                for (Modifier mod : itemMods){
                    if (mod.getModifierID().getAffixType().equals(Affix.PREFIX)){
                        p++;
                    } else {
                        s++;
                    }
                }
                boolean isPrefix = ItemBuilder.isPrefix();
                if (isPrefix && p < 3){ //If a prefix is chosen and theyre not full
                    ItemBuilder.addModTo(itemData, prefixes, illegalMods);
                } else if (isPrefix) { //If its a prefix and theyre full, add a suffix
                    ItemBuilder.addModTo(itemData, suffixes, illegalMods);
                } else if (s < 3) { //If its not a prefix, add a suffix if theyre not full
                    ItemBuilder.addModTo(itemData, suffixes, illegalMods);
                } else {//If its not a prefix and suffixes are full, add a prefix anyway
                    ItemBuilder.addModTo(itemData, prefixes, illegalMods);
                }
                return true;
            }
        }
        //Functionality not mapped, thus invalid
        return false;
    }

    public static <SubType extends Enum<SubType> & ItemSubtype> void applyChanges(ItemStack item, Item itemData, SubType itemSubtype){
        serializeItem(item, itemData);
        itemData.imprint(item,itemSubtype);
    }
    public static int rollCorruptionOutcome(){
        //60-20-20, 60% Nothing happens, 20% Brick, 20% Upgrade
        float neutralOutcome = 0.6F;
        float vanishOutcome = 0.2F;
//        float successOutcome = 1 - (neutralOutcome + vanishOutcome);
        double roll = Math.random();
        if (roll <= neutralOutcome){
            return 0;
        } else if (roll <= (neutralOutcome+vanishOutcome)) {
            return -1;
        } else {
            return 1;
        }
    }
}
