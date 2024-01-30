package com.amorabot.inscripted.components.Items.DataStructures;

import com.amorabot.inscripted.utils.Utils;

import java.io.Serializable;
import java.util.Objects;

public class Modifier implements Serializable {

    private ModifierIDs modifier;
    private int tier; //0-maxTier
    private int maxTier;
    private double basePercentile; //0-1
    private boolean imbued = false; //mods are not fractured/imbued by default, but can be altered

    public Modifier(ModifierIDs mod, int tier, int highestTierAvailable){
        this.modifier = mod;
        //Capped to the mod's maximum tier
        this.tier = Math.min(tier, mod.getNumberOfTiers());
        this.maxTier = highestTierAvailable; //its the mod's highest possible tier, given the item level that generated it
        this.basePercentile = Utils.getNormalizedValue();
    }
    public int getTier() {
        return tier;
    }
    public int getModsHighestTier(){
        return maxTier;
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
    public boolean isImbued(){
        return imbued;
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
        StringBuilder modifierDisplayName;
        if (isImbued()){
            modifierDisplayName = new StringBuilder("&6");
        } else {
            modifierDisplayName = new StringBuilder("&7");
        }
        modifierDisplayName.append(this.getModifierID().getDisplayName());
        modifierDisplayName.append(getModDetails(indent));
        String dispName = modifierDisplayName.toString();

        int[] mappedValues = ModifierIDs.getModifierValuesFor(this);
        switch (this.getModifierID().getRangeType()){
            case SINGLE_VALUE -> {}
            case SINGLE_RANGE -> {
                int value = Utils.getRoundedParametricValue(mappedValues[0], mappedValues[1], this.basePercentile);
                if (isImbued()){
                    dispName = (dispName.replace("@value1@", ""+value)).indent(indent);
                    return dispName;
                }
                dispName = (dispName.replace("@value1@", valuesColor + value +"&7"));
            }
            case DOUBLE_RANGE -> {
                int value1 = Utils.getRoundedParametricValue(mappedValues[0], mappedValues[1], this.basePercentile);
                int value2 = Utils.getRoundedParametricValue(mappedValues[2], mappedValues[3], this.basePercentile);
                if (isImbued()){
                    dispName = (dispName
                            .replace("@value1@", ""+value1)
                            .replace("@value2@", ""+value2)).indent(indent);
                    return dispName;
                }
                dispName = (dispName
                        .replace("@value1@", valuesColor+value1+"&7")
                        .replace("@value2@", valuesColor+value2+"&7"));
            }
        }
        return dispName.indent(indent);
    }
    private String getModDetails(int lineEndIndent){
        StringBuilder modDetails = new StringBuilder();
        modDetails.append(" &8");
        switch (this.getModifierID().getAffixType()){
            case PREFIX -> modDetails.append("ᚴ ");
            case SUFFIX -> modDetails.append("ᚭ ");
            case UNIQUE -> modDetails.append("ᛟ ");
            default -> modDetails.append("0 ");
        }
        modDetails.append(Utils.getRomanChar(this.tier));
        if (this.tier == this.maxTier){
            modDetails.append("*");
        }
        modDetails.append(" ".repeat(lineEndIndent));

        return modDetails.toString();
    }
}
