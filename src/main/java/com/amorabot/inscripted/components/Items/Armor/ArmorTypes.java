package com.amorabot.inscripted.components.Items.Armor;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Tiers;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.inscriptions.InscriptionTable;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.meta.trim.TrimMaterial;

import java.util.*;

public enum ArmorTypes implements ItemSubtype {

    //TODO: Rename enum constants and remove prefixes when changing item naming
    //Armored
    HEAVY_PLATING("Heavy",TrimMaterial.REDSTONE),
    //Ornate
    CARVED_PLATING("Ornate",TrimMaterial.GOLD),
    //Cloth
    LIGHT_CLOTH("Cloth",TrimMaterial.EMERALD),
    //Pelt
    RUNIC_LEATHER("Pelt",TrimMaterial.NETHERITE),
    //Silk
    ENCHANTED_SILK("Silk",TrimMaterial.LAPIS),
    //Runisteel
    RUNIC_STEEL("Runisteel",TrimMaterial.AMETHYST);


    private final InscriptionTable itemInscriptionTable;

    private static final double HELMET_MAIN_STAT_WEIGHT = 0.8;
    private static final double CHESTPLATE_MAIN_STAT_WEIGHT = 1.3;
    private static final double LEGGINGS_MAIN_STAT_WEIGHT = 1.2;
    private static final double BOOTS_MAIN_STAT_WEIGHT = 0.7;

    private final String prefix;
    @Getter
    private final TrimMaterial trimMaterial;

    private final Map<Tiers, String> names = new HashMap<>();
    private final Map<Tiers, Map<ItemTypes, Map<DefenceTypes, Integer>>> baseStats = new HashMap<>();
    public static final int percentHealthVariance = 10;

    ArmorTypes(String prefix, TrimMaterial trimMaterial){
        this.prefix = prefix;
        this.trimMaterial = trimMaterial;
        for (Tiers tier : Tiers.values()){
            this.names.put(tier, loadTierName(tier));
            this.baseStats.put(tier, loadBaseStats(tier));
        }
        this.itemInscriptionTable = new InscriptionTable(this.toString());
    }

    public String getSubtypePrefix() {
        return prefix;
    }
    public InscriptionTable getTableData(){
        return this.itemInscriptionTable;
    }

    public Material mapArmorBase(Tiers tier, ItemTypes armorBase){
        if (armorBase.equals(ItemTypes.WEAPON)){
            Utils.error("Invalid argument for armor mapping." + armorBase + " is not a armor type.");
            return null;
        }
        return getArmorMaterial(tier, armorBase);
    }
    private Material getArmorMaterial(Tiers tier, ItemTypes armorBase){
        String materialString = tier.getMaterial() + "_" + armorBase.toString();
        return Material.valueOf(materialString);
    }



    @Override
    public String loadTierName(Tiers tier) {
        String namePath = ArmorTypes.class.getSimpleName() + "." + this + "." + tier + "." + "NAME";
        return Inscripted.getPlugin().getConfig().getString(namePath);
    }
    @Override
    public String getTierName(Tiers tier){
        return this.names.getOrDefault(tier, "INVALID ARMOR");
    }

    public Map<ItemTypes, Map<DefenceTypes, Integer>> loadBaseStats(Tiers tier){
        FileConfiguration config = Inscripted.getPlugin().getConfig();
        Map<ItemTypes, Map<DefenceTypes, Integer>> baseStatData = new HashMap<>();

        for (ItemTypes armorSlot : ItemTypes.values()){
            if (armorSlot.equals(ItemTypes.WEAPON)){continue;}

            //Get to the armor subtype
            String subtypePath = ArmorTypes.class.getSimpleName() + "." + this + ".";

            //Get all the defence types that armor subtype has
            List<String> subtypeDefences = config.getStringList(subtypePath+DefenceTypes.class.getSimpleName());
            List<DefenceTypes> mappedSubtypeDefences = new ArrayList<>();
            for (String defString : subtypeDefences){
                try {
                    DefenceTypes mappedDef = DefenceTypes.valueOf(defString);
                    mappedSubtypeDefences.add(mappedDef);
                } catch (IllegalArgumentException exception){
                    Utils.error("Invalid argument for armor mapping.");
                    return new HashMap<>();
                }
            }

            Map<DefenceTypes, Integer> defMap = new HashMap<>();
            //Once the def.Types are known, lets fetch them individually and build the defMap for that subtype
            for (DefenceTypes def : mappedSubtypeDefences){
                String currDefencePath = subtypePath + "." + tier + "." + def;

                int mappedDefenceValue = config.getInt(currDefencePath);
                //Scale the base value based on the armor piece
                switch (armorSlot){
                    case HELMET -> mappedDefenceValue = (int) (mappedDefenceValue * HELMET_MAIN_STAT_WEIGHT);
                    case CHESTPLATE -> mappedDefenceValue = (int) (mappedDefenceValue * CHESTPLATE_MAIN_STAT_WEIGHT);
                    case LEGGINGS -> mappedDefenceValue = (int) (mappedDefenceValue * LEGGINGS_MAIN_STAT_WEIGHT);
                    case BOOTS -> mappedDefenceValue = (int) (mappedDefenceValue * BOOTS_MAIN_STAT_WEIGHT);
                    default -> {
                        mappedDefenceValue = 0;
                        Utils.error("Cant map base armor stats for " + armorSlot);
                    }
                }
                defMap.put(def, mappedDefenceValue);
            }
            //Adding the health value for that armor piece, for that given tier
            String healthPath = subtypePath + tier + "." + armorSlot;
            defMap.put(DefenceTypes.HEALTH, loadHealthValue(healthPath));

            //Now, the defMap needs to be associated with its armorSlot
            baseStatData.put(armorSlot,defMap);
        }
        return baseStatData;
    }

    public Map<DefenceTypes, Integer> mapBaseStats(Armor armorData){
        FileConfiguration config = Inscripted.getPlugin().getConfig();
        Map<DefenceTypes, Integer> defenceMap = new HashMap<>();

        ItemTypes slot = armorData.getCategory();
        ArmorTypes subtype = this;
        Tiers tier = armorData.getTier();

        String subtypePath = ArmorTypes.class.getSimpleName() + "." + subtype + ".";
        List<String> subtypeDefences = config.getStringList(subtypePath+DefenceTypes.class.getSimpleName());
        List<DefenceTypes> mappedSubtypeDefences = new ArrayList<>();
        for (String defString : subtypeDefences){
            try {
                DefenceTypes mappedDef = DefenceTypes.valueOf(defString);
                mappedSubtypeDefences.add(mappedDef);
            } catch (IllegalArgumentException exception){
                Utils.error("Invalid argument for armor mapping.");
                return defenceMap;
            }
        }

        for (DefenceTypes def : mappedSubtypeDefences){
            int ilvl = armorData.getIlvl();
            int tierMaxLevel = tier.getMaxLevel();
            Optional<Tiers> prevTier = tier.getPreviousTier();

            if (ilvl <= 0){
                defenceMap.put(def, 0);
                continue;
            }

            int mappedDefenceValue;

            if (prevTier.isPresent()){
                Tiers previousTier = prevTier.get();
                int prevTierMaxLvl = previousTier.getMaxLevel();

                int v1 = ilvl - prevTierMaxLvl;
                int v2 = tierMaxLevel - prevTierMaxLvl;
                float t = ((float) (v1)) /v2;

                int currMaxDef = baseStats.get(tier).get(slot).get(def);
                int prevMaxDef = baseStats.get(previousTier).get(slot).get(def);

                mappedDefenceValue = ( int ) Utils.getParametricValue(prevMaxDef, currMaxDef, t);
            } else {//Means its a T1 (has no previous tier)
                float t = ((float)(ilvl)) / tierMaxLevel ;

                int currMaxDef = baseStats.get(tier).get(slot).get(def);

                mappedDefenceValue = ( int ) Utils.getParametricValue(0, currMaxDef, t);
            }
            defenceMap.put(def, mappedDefenceValue);
        }
        defenceMap.put(DefenceTypes.HEALTH, armorData.getBaseHealth());
        return defenceMap;
    }
    public int mapHealthValue(Armor armorData){
        Map<DefenceTypes, Integer> cacheDefMap = baseStats.get(armorData.getTier()).get(armorData.getCategory());
        return cacheDefMap.get(DefenceTypes.HEALTH);
    }


    private int loadHealthValue(String path){
        return Inscripted.getPlugin().getConfig().getInt(path);
    }
}