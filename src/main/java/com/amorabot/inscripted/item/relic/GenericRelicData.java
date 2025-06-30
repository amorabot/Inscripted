package com.amorabot.inscripted.item.relic;

import java.util.List;

public record GenericRelicData(String name, int itemLevel,
                               List<UniqueInscriptionDTO> inscriptions,
                               List<String> flavorText) {
}
