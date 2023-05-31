package com.amorabot.rpgelements.Crafting.Weapons.Modifiers;

import static com.amorabot.rpgelements.Crafting.CraftingUtils.getRandomNumber;
import static com.amorabot.rpgelements.utils.Utils.log;

public record TierData (int ilvl, int... rangeValues){
    public TierData(int ilvl, int... rangeValues){
        this.ilvl = ilvl;
        if (rangeValues.length>4){
            this.rangeValues = new int[]{rangeValues[0],rangeValues[1],rangeValues[2],rangeValues[3]};
        } else {
            this.rangeValues = rangeValues;
        }
    }
    public int[] getRandomValue(){
        int rangeSize = this.rangeValues.length;
        switch (rangeSize){
            case 0: //Serialization error
                log("invalid value generation attempt: this tier's values are empty");
                break;
            case 1: // return [value]
                return this.rangeValues;
            case 2: // return [valueFromRange]
                return new int[]{getRandomNumber(rangeValues[0], rangeValues[1])};
            case 4: // return [valueFromRange1, valueFromRange2]
                return new int[]{getRandomNumber(rangeValues[0], rangeValues[1]), getRandomNumber(rangeValues[2], rangeValues[3])};
        }
        return null;
    }
}
