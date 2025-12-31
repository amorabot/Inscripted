package com.amorabot.inscripted.item.relic;

import java.io.Serializable;

public record UniqueInscriptionDTO(String definition, int[] values) implements Serializable {
}
