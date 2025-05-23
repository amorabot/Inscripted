package com.amorabot.inscripted.item.inscription.table;

import com.amorabot.inscripted.file.InscriptionDataManager;
import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.inscription.language.AffixType;

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

    private Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> tableData = new HashMap<>();

    InscriptionSubtable(boolean preloaded){
        if (preloaded){this.tableData = InscriptionDataManager.loadSubtable(this.toString());}
    }


    public Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> getSubtableData(){
        if (!tableData.isEmpty()){return this.tableData;}
        this.tableData = InscriptionDataManager.loadSubtable(this.toString());
        return this.tableData;
    }

}
