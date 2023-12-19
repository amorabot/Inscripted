package com.amorabot.inscripted.components.Items.DataStructures;

import com.amorabot.inscripted.utils.Utils;

import java.io.Serializable;
import java.util.Objects;

public class Modifier implements Serializable {

    private ModifierIDs modifier;
    private int tier; //0-maxTier
    private double basePercentile; //0-1
    private boolean imbued = false; //mods are not fractured/imbued by default, but can be altered

    public Modifier(ModifierIDs mod, int tier){
        this.modifier = mod;
        //Capped to the mod's maximum tier
        this.tier = Math.min(tier, mod.getNumberOfTiers());
        this.basePercentile = Utils.getNormalizedValue();
    }
    public int getTier() {
        return tier;
    }
    public void setTier(byte tier) {
        this.tier = tier;
    }
    public ModifierIDs getModifierID() {
        return modifier;
    }
    public void setModifier(ModifierIDs modifier) {
        this.modifier = modifier;
    }
    public void setBasePercentile(byte basePercentile) {
        this.basePercentile = basePercentile;
    }
    public double getBasePercentile() {
        return this.basePercentile;
    }
    public void imbue(){
        this.imbued = true;
    }

    public int getModifierOrdinal(){
        return getModifierID().ordinal();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Modifier comparedMod = (Modifier) o;
        return modifier == comparedMod.modifier;
    }
    @Override
    public int hashCode() {
        return Objects.hash(modifier);
    }

    public String getModifierDisplayName(String valuesColor, int indent) {
        String modCategoryRune;
        switch (this.getModifierID().getAffixType()){
            case PREFIX -> modCategoryRune = "ᚴ";
            case SUFFIX -> modCategoryRune = "ᚭ";
            case UNIQUE -> modCategoryRune = "ᛟ";
            default -> modCategoryRune = "0";
        }
        String modifierDisplayName = "&7" + this.getModifierID().getDisplayName() + " &8" + modCategoryRune + " " + Utils.getRomanChar(this.tier) + " ".repeat(indent);
        int[] mappedValues = ModifierIDs.getModifierValuesFor(this);
        switch (this.getModifierID().getRangeType()){
            case SINGLE_VALUE -> {}
            case SINGLE_RANGE -> {
                int value = Utils.getRoundedParametricValue(mappedValues[0], mappedValues[1], this.basePercentile);
                modifierDisplayName = (modifierDisplayName
                        .replace("@value1@", valuesColor + value +"&7")).indent(indent);
            }
            case DOUBLE_RANGE -> {
                int value1 = Utils.getRoundedParametricValue(mappedValues[0], mappedValues[1], this.basePercentile);
                int value2 = Utils.getRoundedParametricValue(mappedValues[2], mappedValues[3], this.basePercentile);
                modifierDisplayName = (modifierDisplayName
                        .replace("@value1@", valuesColor+value1+"&7")
                        .replace("@value2@", valuesColor+value2+"&7")).indent(indent);
            }
        }
        return modifierDisplayName;
    }
}
