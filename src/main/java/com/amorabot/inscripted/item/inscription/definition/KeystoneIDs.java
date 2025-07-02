package com.amorabot.inscripted.item.inscription.definition;

import com.amorabot.inscripted.utils.Utils;

public enum KeystoneIDs {
    FORBIDDEN_PACT(),
    LETHAL_STRIKES(),
    BLOOD_PACT(),
    ORGAN_FAILURE(),
    FIRE_ATTUNEMENT(),
    LIGHTNING_ATTUNEMENT(),
    COLD_ATTUNEMENT(),
    ELEMENTAL_BLESSING(),
    AGNOSTIC(),

    BERSERK(),
    WINDS_OF_CHANGE(),
    PERMAFROST(),
    THUNDERSTRUCK(),
    RIGHTEOUS_FIRE();

    KeystoneIDs(){
//        Utils.log(this.name());
    }
    /*
    Attributes -> Trigger, Description, statRule bool & applyStatRule() Override, skill bool and apply()
    apply() will need explicit overrides and skill keystones will use the private cast() method inside its logic,
    meant only for them to use. Any non-skill calls should be invalid
     */
}
