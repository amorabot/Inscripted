package com.amorabot.inscripted.components.Items.modifiers;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.inscripted.components.Items.modifiers.data.HybridInscriptionData;
import com.amorabot.inscripted.components.Items.modifiers.data.InscriptionData;
import com.amorabot.inscripted.components.Items.modifiers.data.ModifierData;
import com.amorabot.inscripted.components.Items.modifiers.data.StatDefinition;
import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class Inscription implements Serializable {

    private InscriptionID inscription;
    private int tier; //0-maxTier
    private int maxTier;
    private double basePercentile; //0-1
    private boolean imbued = false;

    public Inscription(InscriptionID mod, int chosenTier, int highestTierAvailable){
        this.inscription = mod;
        //Capped to the mod's maximum tier
        this.tier = Math.min(chosenTier, mod.getTotalTiers());
        this.maxTier = highestTierAvailable; //its the mod's highest possible tier, given the item level that generated it
        this.basePercentile = Utils.getNormalizedValue();
    }
    public Inscription(InscriptionID uniqueMod){ //Unique constructor
        ModifierData modData = uniqueMod.getData();
        this.inscription = uniqueMod;
        if (modData.isKeystone() || modData.isUniqueEffect()){
            this.tier = 0;
            this.maxTier = 0;
            this.basePercentile = 1;
            this.imbued = true;
            return;
        }
        //Unique value constructor
        this.tier = uniqueMod.getTotalTiers();
        this.maxTier = uniqueMod.getTotalTiers();
        this.basePercentile = Utils.getNormalizedValue();
        this.imbued = false;
    }

    public int[] getMappedFinalValue(){ //Call for standard mods
        int[] tableValues = InscriptionID.fetchValuesFor(this).clone();
        tableValues = negateValuesArrayIf(!this.getInscription().isPositive(), tableValues);
        Utils.log("Fetched values ("+getInscription()+"): " + Arrays.toString(tableValues));
        InscriptionData modData = (InscriptionData) getInscription().getData();
        RangeTypes range = modData.getDefinitionData().rangeType();
        return RangeTypes.mapFinalValuesFor(range, tableValues, getBasePercentile());
    }
    public int[] getMappedFinalValue(int modIndex){ //Call for hybrid mods
        int[] tableValues = InscriptionID.fetchValuesFor(this).clone();
        tableValues = negateValuesArrayIf(!this.getInscription().isPositive(), tableValues);
        HybridInscriptionData modData = (HybridInscriptionData) getInscription().getData();
        StatDefinition[] defs = modData.getStatDefinitions();
        RangeTypes range = defs[modIndex].rangeType();
        List<int[]> tableValueForCurrentMod = modData.splitValuesArray(tableValues);
        return RangeTypes.mapFinalValuesFor(range, tableValueForCurrentMod.get(modIndex), getBasePercentile());
    }
    private int[] negateValuesArrayIf(boolean isNegative ,int[] tableValues){
        if (isNegative){
            return Arrays.stream(tableValues).map(value -> {
                return -value;
            }).toArray();
        }
        return tableValues;
    }

    public String getModifierDisplayName(String valuesColor, int padding) {
        String rawDisplayName = getInscription().getDisplayName();
        String detailsSuffix = getModDetails(padding);
        InscriptionID inscription = getInscription();
        ModifierData modData = inscription.getData();

        String finalDisplayName;

        if (modData.isStandard()){
            InscriptionData inscriptionData = (InscriptionData) modData;
            int[] mappedValues = this.getMappedFinalValue();
            RangeTypes range = inscriptionData.getDefinitionData().rangeType();
            String mappedDisplayName = range.substitutePlaceholders(mappedValues, rawDisplayName, valuesColor);
            if (isImbued()){
                finalDisplayName = "&6" + ColorUtils.decolor(mappedDisplayName) + detailsSuffix;
                return finalDisplayName.indent(padding);
            }
            finalDisplayName = "&7"+(mappedDisplayName + detailsSuffix);
        }else if (modData.isHybrid()){
            HybridInscriptionData hybridInscriptionData = (HybridInscriptionData) modData;
            StatDefinition[] modDataArray = hybridInscriptionData.getStatDefinitions();
            int numberOfMods = modDataArray.length;
            String[] templates = rawDisplayName.split(HybridInscriptionData.HYBRID_SEPARATOR);
            String[] mappedDisplayNames = new String[numberOfMods];
            for (int i = 0; i < numberOfMods; i++){
                StatDefinition currentInscription = modDataArray[i];
                int[] currentMappedValues = this.getMappedFinalValue(i);
                String currentMappedDisplayName = currentInscription.rangeType().substitutePlaceholders(currentMappedValues,templates[i],valuesColor);
                mappedDisplayNames[i] = currentMappedDisplayName;
            }
            StringBuilder displayNameBuilder = new StringBuilder();
            for (int k = 0; k<numberOfMods; k++){
                String s = mappedDisplayNames[k];
                displayNameBuilder.append(s);
                if (k==numberOfMods-1){continue;}
                displayNameBuilder.append(HybridInscriptionData.HYBRID_SEPARATOR);
            }
            if (isImbued()){
                finalDisplayName = "&6" + ColorUtils.decolor(displayNameBuilder.toString()) + detailsSuffix;
                return finalDisplayName.indent(padding);
            }
            finalDisplayName = "&7"+(displayNameBuilder + detailsSuffix);
        } else {
            if (modData.isKeystone() || modData.isUniqueEffect()){
                return "";
            }
            finalDisplayName = "its fucked up";
        }
        return finalDisplayName.indent(padding);
    }
    public String getImplicitDisplayName(Archetypes archetype, int padding) {
        String archetypeColor = "&" + archetype.getColor();
        String rawDisplayName = getInscription().getDisplayName();
        String detailsSuffix = getModDetails(padding);
        InscriptionID inscription = getInscription();
        ModifierData modData = inscription.getData();

        String finalDisplayName;

        if (modData instanceof InscriptionData inscriptionData){
            int[] mappedValues = this.getMappedFinalValue();
            RangeTypes range = inscriptionData.getDefinitionData().rangeType();
            String mappedDisplayName = range.substitutePlaceholders(mappedValues, rawDisplayName, "");
            finalDisplayName = archetypeColor
                    + Utils.convertToPrettyString(Utils.decolor(mappedDisplayName).replace("&7", ""))
                    + detailsSuffix;
        }else if (modData instanceof HybridInscriptionData hybridInscriptionData){
            StatDefinition[] modDataArray = hybridInscriptionData.getStatDefinitions();
            int numberOfMods = modDataArray.length;
            String[] templates = rawDisplayName.split(HybridInscriptionData.HYBRID_SEPARATOR);
            String[] mappedDisplayNames = new String[numberOfMods];
            for (int i = 0; i < numberOfMods; i++){
                StatDefinition currentInscription = modDataArray[i];
                int[] currentMappedValues = this.getMappedFinalValue(i);
                String currentMappedDisplayName = currentInscription.rangeType().substitutePlaceholders(currentMappedValues,templates[i],"");
                mappedDisplayNames[i] = currentMappedDisplayName;
            }
            StringBuilder displayNameBuilder = new StringBuilder();
            for (int k = 0; k<numberOfMods; k++){
                String s = mappedDisplayNames[k];
                displayNameBuilder.append(s);
                if (k==numberOfMods-1){continue;}
                displayNameBuilder.append(HybridInscriptionData.HYBRID_SEPARATOR);
            }
            finalDisplayName = archetypeColor
                    + Utils.convertToPrettyString(Utils.decolor(displayNameBuilder.toString()).replace("&7", ""))
                    + detailsSuffix;
        } else {
            finalDisplayName = "its fucked up";
        }
        return finalDisplayName;
    }
    private String getModDetails(int padding){
        StringBuilder modDetails = new StringBuilder();
        modDetails.append(" &8");
        modDetails.append(getInscription().getData().getAffixType().getRuneIcon()).append(" ");
        if (!this.getInscription().getData().isUnique()){
            modDetails.append(Utils.getRomanChar(this.tier));
            if (this.tier == this.maxTier){modDetails.append("*");}
        }
        modDetails.append(" ".repeat(padding));

        return modDetails.toString();
    }
    public void imbue(){
        this.imbued = true;
    }
}
