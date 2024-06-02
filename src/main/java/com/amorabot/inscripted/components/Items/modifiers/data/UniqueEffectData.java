package com.amorabot.inscripted.components.Items.modifiers.data;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.modifiers.unique.Effects;

import java.io.Serializable;

public record UniqueEffectData(Affix getAffixType, Effects uniqueEffect) implements ModifierData, Serializable {
}
