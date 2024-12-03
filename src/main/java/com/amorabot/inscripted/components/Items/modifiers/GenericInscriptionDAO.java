package com.amorabot.inscripted.components.Items.modifiers;

import com.amorabot.inscripted.components.Items.modifiers.data.ModifierData;

public record GenericInscriptionDAO(String displayName,
                                    ModifierData data,
                                    int totalTiers,
                                    boolean global,
                                    boolean meta,
                                    boolean positive) {
}
