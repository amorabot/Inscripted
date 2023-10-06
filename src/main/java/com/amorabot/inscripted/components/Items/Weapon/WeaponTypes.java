package com.amorabot.inscripted.components.Items.Weapon;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Tiers;
import com.amorabot.inscripted.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.inscripted.components.Items.Files.ModifiersJSON;
import com.amorabot.inscripted.utils.CraftingUtils;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public enum WeaponTypes implements AffixTableSelector {

    AXE("#b01330",
            List.of("Training axe", "Oak axe", "Spruce axe", "Wooden hatchet"),
            List.of("Stone axe", "Pointy debris axe", "Woodsplitter", "Jade Hatchet"),
            List.of("War-Axe", "Steel axe", "Cleaver", "Tempered axe"),
            List.of("Shining Opal axe", "Crystal axe", "Ancient axe", "Crystal War-Axe"),
            List.of("Noble axe", "Gilded War-Axe", "Runic-Gold axe", "Warlord axe")
            ){
        @Override
        public int[] mapDamage(int ilvl) { //Todo: re-map damages and map according to tier
            if (ilvl > 0 && ilvl <= 11){
                return new int[]{6,11};
            } else if (ilvl <= 30){
                return new int[]{11,21};
            } else if (ilvl <= 45){
                return new int[]{27,50};
            } else if (ilvl <= 60){
                return new int[]{35,65};
            } else if (ilvl <= 80){
                return new int[]{38,114};
            } else {
                return new int[]{60,150};
            }
        }
        @Override
        public Material mapWeaponBase(Tiers tier) {
            switch (tier){
                case T1 -> {
                    return Material.WOODEN_AXE;
                }
                case T2 -> {
                    return Material.STONE_AXE;
                }
                case T3 -> {
                    return Material.IRON_AXE;
                }
                case T4 -> {
                    return Material.DIAMOND_AXE;
                }
                case T5 -> {
                    return Material.GOLDEN_AXE;
                }
                default -> {
                    return Material.NETHERITE_AXE;
                }
            }
        }
    },
    SWORD("#F9B147",
            List.of("Training sword", "Oak sword", "Spruce sword", "Dried spike foil"),
            List.of("Stone sword", "Whalebone rapier", "Stalagmite foil", "Granite blade"),
            List.of("Greatsword", "Steel sword", "Scimitar", "Tempered sword"),
            List.of("Shining Opal blade", "Crystal sword", "Ancient sword", "Crystal Greatsword"),
            List.of("Engraved sword", "Onate Greatsword", "Runic-gold sword", "Jeweled foil")
            ) {
        @Override
        public int[] mapDamage(int ilvl) {
            if (ilvl > 0 && ilvl <= 11){
                return new int[]{3,9};
            } else if (ilvl <= 30){
                return new int[]{10,17};
            } else if (ilvl <= 45){
                return new int[]{25,32};
            } else if (ilvl <= 60){
                return new int[]{30,55};
            } else if (ilvl <= 80){
                return new int[]{33,90};
            } else {
                return new int[]{45,120};
            }
        }
        @Override
        public Material mapWeaponBase(Tiers tier) {
            switch (tier){
                case T1 -> {
                    return Material.WOODEN_SWORD;
                }
                case T2 -> {
                    return Material.STONE_SWORD;
                }
                case T3 -> {
                    return Material.IRON_SWORD;
                }
                case T4 -> {
                    return Material.DIAMOND_SWORD;
                }
                case T5 -> {
                    return Material.GOLDEN_SWORD;
                }
                default -> {
                    return Material.NETHERITE_SWORD;
                }
            }
        }
    },
    //#89ae59  #71C45A
    BOW("#89AE59",
            List.of("Training bow", "Oak bow", "Spruce bow", "Twig shortbow"),
            List.of("Reinforced bow", "Whalebone shortbow", "Longbow", "Crude bow"),
            List.of("Compound bow", "Steelwood bow", "Citadel bow", "Ranger shortbow"),
            List.of("Opal-Tipped bow", "Crystalwood bow", "Ancient bow", "Crystal shortbow"),
            List.of("Royal crossbow", "Imperial shortbow", "Runic-gold bow", "Gilded crossbow")
            ) {
        @Override
        public int[] mapDamage(int ilvl) {
            if (ilvl > 0 && ilvl <= 11){
                return new int[]{6,11};
            } else if (ilvl <= 30){
                return new int[]{11,21};
            } else if (ilvl <= 45){
                return new int[]{27,50};
            } else if (ilvl <= 60){
                return new int[]{35,65};
            } else if (ilvl <= 80){
                return new int[]{38,114};
            } else {
                return new int[]{60,150};
            }
        }
        @Override
        public Material mapWeaponBase(Tiers tier) {
            return Material.BOW;
        }
    },
    DAGGER("#18A383",
            List.of("Training dagger", "Oak dagger", "Spruce dagger", "Dry spike stiletto"),
            List.of("Stone dagger", "Bone dagger", "Glass-tipped shank", "Sabertooth dagger"),
            List.of("Skinning knife", "Steel dagger", "Sai", "Tempered dagger"),
            List.of("Shining Opal dagger", "Crystal dagger", "Ancient dagger", "Crystal ambusher"),
            List.of("Ornate dagger", "Imperial ambusher", "Runic-gold dagger", "Jeweled knife")
            ) {
        @Override
        public int[] mapDamage(int ilvl) {
            if (ilvl > 0 && ilvl <= 11){
                return new int[]{6,11};
            } else if (ilvl <= 30){
                return new int[]{11,21};
            } else if (ilvl <= 45){
                return new int[]{27,50};
            } else if (ilvl <= 60){
                return new int[]{35,65};
            } else if (ilvl <= 80){
                return new int[]{38,114};
            } else {
                return new int[]{60,150};
            }
        }
        @Override
        public Material mapWeaponBase(Tiers tier) {
            return Material.SHEARS;
        }
    },
    WAND("#496FE3",
            List.of("Training wand", "Oak wand", "Spruce wand", "Twig wand"),
            List.of("Reinforced wand", "Bone wand", "Wooden focus wand", "Simple wand"),
            List.of("Iron focus wand", "Steel handle wand", "Stable iron wand", "Goat horn"),
            List.of("Opal wand", "Crystal focus wand", "Ancient wand", "Sage wand"),
            List.of("Profane wand", "Crystalwood wand", "Runic focus wand", "Demon horn")
            ) {
        @Override
        public int[] mapDamage(int ilvl) {
            if (ilvl > 0 && ilvl <= 11){
                return new int[]{6,11};
            } else if (ilvl <= 30){
                return new int[]{11,21};
            } else if (ilvl <= 45){
                return new int[]{27,50};
            } else if (ilvl <= 60){
                return new int[]{35,65};
            } else if (ilvl <= 80){
                return new int[]{38,114};
            } else {
                return new int[]{60,150};
            }
        }
        @Override
        public Material mapWeaponBase(Tiers tier) {
            return Material.STICK;
        }
    },
    SCEPTRE("#A735D4",
            List.of("Training sceptre", "Oak sceptre", "Spruce sceptre", "Wooden sceptre"),
            List.of("Stone sceptre", "Pointy debris sceptre", "Ritualistic sceptre", "Jade sceptre"),
            List.of("War-Sceptre", "Steel sceptre", "Shamanic sceptre", "Tempered sceptre"),
            List.of("Shining Opal sceptre", "Crystal sceptre", "Ancient sceptre", "Crystal War-Sceptre"),
            List.of("Profane sceptre", "Gilded War-Sceptre", "Runic-Gold sceptre", "Ambar sceptre")
            ) {
        @Override
        public int[] mapDamage(int ilvl) {
            if (ilvl > 0 && ilvl <= 11){
                return new int[]{6,11};
            } else if (ilvl <= 30){
                return new int[]{11,21};
            } else if (ilvl <= 45){
                return new int[]{27,50};
            } else if (ilvl <= 60){
                return new int[]{35,65};
            } else if (ilvl <= 80){
                return new int[]{38,114};
            } else {
                return new int[]{60,150};
            }
        }
        @Override
        public Material mapWeaponBase(Tiers tier) {
            switch (tier){
                case T1 -> {
                    return Material.WOODEN_SHOVEL;
                }
                case T2 -> {
                    return Material.STONE_SHOVEL;
                }
                case T3 -> {
                    return Material.IRON_SHOVEL;
                }
                case T4 -> {
                    return Material.DIAMOND_SHOVEL;
                }
                case T5 -> {
                    return Material.GOLDEN_SHOVEL;
                }
                default -> {
                    return Material.NETHERITE_SHOVEL;
                }
            }
        }
    };
    private final List<String> tier1Names;
    private final List<String> tier2Names;
    private final List<String> tier3Names;
    private final List<String> tier4Names;
    private final List<String> tier5Names;
    private final String color;
    private Map<String, Map<Integer, int[]>> basicPrefixes;
    private Map<String, Map<Integer, int[]>> basicSuffixes;
    WeaponTypes(String defaultColor, List<String> t1, List<String> t2, List<String> t3, List<String> t4, List<String> t5){
        this.tier1Names = t1;
        this.tier2Names = t2;
        this.tier3Names = t3;
        this.tier4Names = t4;
        this.tier5Names = t5;

        this.color = defaultColor;
        loadAllAffixes();
    }
    public String getRandomName(Tiers tier){
        switch (tier){
            case T1 -> {
                int rand = CraftingUtils.getRandomNumber(0, this.tier1Names.size()-1);
                return tier1Names.get(rand);
            }
            case T2 -> {
                int rand = CraftingUtils.getRandomNumber(0, this.tier2Names.size()-1);
                return tier2Names.get(rand);
            }
            case T3 -> {
                int rand = CraftingUtils.getRandomNumber(0, this.tier3Names.size()-1);
                return tier3Names.get(rand);
            }
            case T4 -> {
                int rand = CraftingUtils.getRandomNumber(0, this.tier4Names.size()-1);
                return tier4Names.get(rand);
            }
            case T5 -> {
                int rand = CraftingUtils.getRandomNumber(0, this.tier5Names.size()-1);
                return tier5Names.get(rand);
            }
            default -> {
                return "";
            }
        }
    }
    public String getDefaulNameColor(){
        return color;
    }
    public abstract int[] mapDamage(int ilvl);
    public abstract Material mapWeaponBase(Tiers tier);
    private void loadAllAffixes(){
        loadBasicAffixes();
    }
    private void loadBasicAffixes(){
        Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>> modifiersJSON = ModifiersJSON.getBasicModifiers();
        this.basicPrefixes = getAffixTable(modifiersJSON, ItemTypes.WEAPON, Affix.PREFIX);
        this.basicSuffixes = getAffixTable(modifiersJSON, ItemTypes.WEAPON, Affix.SUFFIX);
        Utils.log("Modifiers loaded successfully!(" + this + ")");
    }
    public Map<String, Map<Integer, int[]>> getBasicPrefixes(){
        return this.basicPrefixes;
    }
    public Map<String, Map<Integer, int[]>> getBasicSuffixes(){
        return this.basicSuffixes;
    }
}
