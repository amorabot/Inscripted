package com.amorabot.inscripted.item.inscription.definition;

public enum KeystoneIDs {
    PERMAFROST(),
    THUNDERSTRUCK(),
    RIGHTEOUS_FIRE();

    /*
    Attributes -> Trigger, Description, statRule bool & applyStatRule() Override, skill bool and apply()
    apply() will need explicit overrides and skill keystones will use the private cast() method inside its logic,
    meant only for them to use. Any non-skill calls should be invalid
     */
}
