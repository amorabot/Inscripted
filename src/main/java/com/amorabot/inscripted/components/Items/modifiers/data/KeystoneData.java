package com.amorabot.inscripted.components.Items.modifiers.data;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.relic.enums.Keystones;

import java.io.Serializable;

public record KeystoneData(Affix getAffixType, Keystones keystone) implements ModifierData, Serializable {
}
