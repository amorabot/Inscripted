package com.amorabot.inscripted.components.Items.modifiers.data;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;

public interface ModifierData {
    Affix getAffixType();
    default boolean isStandard(){return (this instanceof InscriptionData);}
    default boolean isHybrid(){return (this instanceof HybridInscriptionData);}
    default boolean isUnique(){
        return getAffixType().equals(Affix.UNIQUE);
    }
    default boolean isKeystone(){return (this instanceof KeystoneData);}
    default boolean isUniqueEffect(){return (this instanceof KeystoneData);}
}
