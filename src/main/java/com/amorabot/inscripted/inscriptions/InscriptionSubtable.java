package com.amorabot.inscripted.inscriptions;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;

import java.util.HashMap;
import java.util.Map;

public enum InscriptionSubtable {
    GENERIC_WEAPON(false),
    GENERIC_ARMOR(false),
    STRENGTH_WEAPON(false),
    STRENGTH_ARMOR(false),
    DEXTERITY_WEAPON(false),
    DEXTERITY_ARMOR(false),
    INTELLIGENCE_WEAPON(false),
    INTELLIGENCE_ARMOR(false);

    private Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> tableData = new HashMap<>();

    InscriptionSubtable(boolean preloaded){
        if (preloaded){this.tableData = InscriptionDataManager.loadSubtable(this.toString());}
    }


    public Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> getSubtableData(){
        if (!tableData.isEmpty()){return this.tableData;}
        this.tableData = InscriptionDataManager.loadSubtable(this.toString());
        return this.tableData;
    }

}
