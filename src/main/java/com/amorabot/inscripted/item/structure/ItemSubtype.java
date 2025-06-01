package com.amorabot.inscripted.item.structure;

import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
import com.amorabot.inscripted.item.inscription.table.InscriptionTable;

public interface ItemSubtype {
    InscriptionTable getTableData();

    Archetypes mapArchetype();
    String getSubtypeDisplayName(Item itemData);

    String loadTierName(Tiers tier);
    String getTierName(Tiers tier);
}
