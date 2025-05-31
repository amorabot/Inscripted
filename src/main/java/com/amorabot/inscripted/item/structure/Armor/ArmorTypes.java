package com.amorabot.inscripted.item.structure.Armor;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
import com.amorabot.inscripted.item.structure.EquipmentSlots;
import com.amorabot.inscripted.item.structure.Item;
import com.amorabot.inscripted.item.structure.Tiers;
import com.amorabot.inscripted.item.structure.ItemSubtype;
import com.amorabot.inscripted.item.inscription.table.InscriptionTable;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.meta.trim.TrimMaterial;

import java.util.*;

public enum ArmorTypes implements ItemSubtype {

    ARMORED(TrimMaterial.REDSTONE),
    ORNATE(TrimMaterial.GOLD),
    CLOTH(TrimMaterial.EMERALD),
    PELT(TrimMaterial.NETHERITE),
    SILK(TrimMaterial.LAPIS),
    RUNISTEEL(TrimMaterial.AMETHYST);

    private final InscriptionTable itemInscriptionTable;

    private static final double HELMET_MAIN_STAT_WEIGHT = 0.8;
    private static final double CHESTPLATE_MAIN_STAT_WEIGHT = 1.3;
    private static final double LEGGINGS_MAIN_STAT_WEIGHT = 1.2;
    private static final double BOOTS_MAIN_STAT_WEIGHT = 0.7;

    @Getter
    private final TrimMaterial trimMaterial;

    private final Map<Tiers, String> names = new HashMap<>();
    private final Map<Tiers, Map<EquipmentSlots, Map<DefenceTypes, Integer>>> baseStats = new HashMap<>();
    public static final int BASE_VARIANCE = 10;

    ArmorTypes(TrimMaterial trimMaterial){
        this.trimMaterial = trimMaterial;
        for (Tiers tier : Tiers.values()){
            this.names.put(tier, loadTierName(tier));
            this.baseStats.put(tier, loadBaseStats(tier));
        }
        this.itemInscriptionTable = new InscriptionTable(this.toString());
//        itemInscriptionTable.debug();
    }

    public InscriptionTable getTableData(){
        return this.itemInscriptionTable;
    }

    public Material mapArmorBase(Tiers tier, EquipmentSlots armorSlot){
        if (armorSlot.equals(EquipmentSlots.WEAPON)){
            Utils.error("Invalid argument for armor mapping." + armorSlot + " is not a armor type.");
            return null;
        }
        return getArmorMaterial(tier, armorSlot);
    }
    private Material getArmorMaterial(Tiers tier, EquipmentSlots armorSlot){
        String materialString = tier.getMaterial() + "_" + armorSlot.toString();
        return Material.valueOf(materialString);
    }
    public int getBaseHealthValue(Tiers tier, EquipmentSlots slot){
        return baseStats.get(tier).get(slot).getOrDefault(DefenceTypes.HEALTH,0);
    }

    private Map<EquipmentSlots, Map<DefenceTypes, Integer>> loadBaseStats(Tiers tier){
        FileConfiguration config = Inscripted.getPlugin().getConfig();
        Map<EquipmentSlots, Map<DefenceTypes, Integer>> baseStatData = new HashMap<>();

        for (EquipmentSlots armorSlot : EquipmentSlots.values()){
            if (armorSlot.equals(EquipmentSlots.WEAPON)){continue;}

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
            int healthValue = Inscripted.getPlugin().getConfig().getInt(healthPath);
            defMap.put(DefenceTypes.HEALTH, healthValue);

            //Now, the defMap needs to be associated with its armorSlot
            baseStatData.put(armorSlot,defMap);
        }
        return baseStatData;
    }

    public Map<DefenceTypes, Integer> buildArmorDefences(int ilvl, Tiers tier, EquipmentSlots slot, int baseArmorHealth){
        FileConfiguration config = Inscripted.getPlugin().getConfig();
        Map<DefenceTypes, Integer> defenceMap = new HashMap<>();

        ArmorTypes subtype = this;

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

        defenceMap.put(DefenceTypes.HEALTH, baseArmorHealth);
        return defenceMap;
    }


    // ItemSubtype Implementations
    @Override
    public Archetypes mapArchetype() {
        for (Archetypes arch : Archetypes.values()){
            if (arch.equals(Archetypes.NONE)){continue;}
            if (arch.getArmorType().equals(this)){return arch;}
        }
        return Archetypes.NONE;
    }

    @Override
    public String getSubtypeDisplayName(Item itemData) {
        //Its safe to assume itemData is a ArmorInstance
        Armor armorData = (Armor) itemData;
        return armorData.getSlot().name();
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
}