package com.amorabot.inscripted.components.Items.Weapon;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Tiers;
import com.amorabot.inscripted.components.Items.Files.ResourcesJSONReader;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum WeaponTypes implements ItemSubtype {

    AXE{
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
    SWORD {
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
    BOW {
        @Override
        public Material mapWeaponBase(Tiers tier) {
            return Material.BOW;
        }
    },
    DAGGER {
        @Override
        public Material mapWeaponBase(Tiers tier) {
            return Material.SHEARS;
        }
    },
    WAND {
        @Override
        public Material mapWeaponBase(Tiers tier) {
            return Material.STICK;
        }
    },
    SCEPTRE {
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

    public static final int weaponDamageVariance = 10;
    private final Map<Tiers, int[]> damages = new HashMap<>();
    private final Map<Tiers, String> names = new HashMap<>();
    private Map<String, Map<String, Map<Integer, Integer>>> affixes;


    WeaponTypes(){
        for (Tiers tier : Tiers.values()){
            this.damages.put(tier, loadBaseDamages(tier));
            this.names.put(tier, loadTierName(tier));
        }
        loadAffixes();
    }


    public int[] mapBaseDamage(Tiers tier){
        return this.damages.getOrDefault(tier, new int[2]).clone();
    }
    public abstract Material mapWeaponBase(Tiers tier);
    private void loadAffixes(){
        this.affixes = ResourcesJSONReader.getModifierTableFor(ItemTypes.WEAPON, this);

        Utils.log("Modifiers loaded successfully!(" + this + ")");
    }
    public Map<String, Map<String, Map<Integer, Integer>>> getAffixes(){
        return this.affixes;
    }

    @Override
    public String loadTierName(Tiers tier) {
        String namePath = WeaponTypes.class.getSimpleName() + "." + this + "." + tier + "." + "NAME";
        return Inscripted.getPlugin().getConfig().getString(namePath);
    }
    @Override
    public String getTierName(Tiers tier) {
        return this.names.getOrDefault(tier, "INVALID WEAPON");
    }
    private int[] loadBaseDamages(Tiers tier){
        FileConfiguration config = Inscripted.getPlugin().getConfig();

        WeaponTypes subtype = this;
        String subtypePath = WeaponTypes.class.getSimpleName() + "." + subtype + ".";
        String tierBaseDamagePath = subtypePath + tier + "." + "BASE_DAMAGE";

        int[] dmgArray = new int[2];
        List<Integer> dmgList = config.getIntegerList(tierBaseDamagePath);
        for (int i = 0; i<dmgList.size(); i++){
            dmgArray[i] = dmgList.get(i);
        }
        return dmgArray;
    }
}
