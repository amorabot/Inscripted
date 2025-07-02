package com.amorabot.inscripted.item.relic;

import com.amorabot.inscripted.item.inscription.definition.InscriptionDefinition;

import java.io.Serializable;

public record ParsedUniqueInscription(InscriptionDefinition definition, int[] values) implements Serializable {
}
