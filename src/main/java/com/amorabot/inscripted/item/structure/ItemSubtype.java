package com.amorabot.inscripted.item.structure;

import com.amorabot.inscripted.components.Player.archetypes.Archetypes;

public interface ItemSubtype {
    Archetypes mapArchetype();
    String getSubtypeDisplayName(Item itemData);

    String loadTierName(Tiers tier);
    String getTierName(Tiers tier);
}
