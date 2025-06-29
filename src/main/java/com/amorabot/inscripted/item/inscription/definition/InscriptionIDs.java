package com.amorabot.inscripted.item.inscription.definition;

import static com.amorabot.inscripted.item.inscription.definition.Stats.*;
import static com.amorabot.inscripted.item.inscription.language.ValueType.*;
import com.amorabot.inscripted.item.inscription.language.DefinitionScanner;
import com.amorabot.inscripted.item.inscription.language.InscriptionSyntaxException;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Objects;

@Getter
public enum InscriptionIDs {
    //Prefixes
    HEALTH("+ local PREFIX: flat single_roll health", 12),
    HEALTH_PERCENT("+ local PREFIX: increased single_roll health",6),
    HEALTH_REGEN_PERCENT("+ global PREFIX: percentage single_roll health_regen",6),
    ARMOR("+ local PREFIX: flat single_roll armor",10),
    ARMOR_PERCENT("+ local PREFIX: increased single_roll armor",6),
    DODGE("+ local PREFIX: flat single_roll dodge",4),
    SOUL("+ local PREFIX: flat single_roll soul",12),
    SOUL_PERCENT("+ local PREFIX: increased single_roll soul",6),
    STRENGTH_PERCENT("+ global PREFIX: increased single_roll strength",3),
    DEXTERITY_PERCENT("+ global PREFIX: increased single_roll dexterity",3),
    INTELLIGENCE_PERCENT("+ global PREFIX: increased single_roll intelligence",3),
    WALK_SPEED("+ global PREFIX: flat single_roll walk_speed",4),
    STAMINA("+ global PREFIX: flat single_roll stamina",6),
    PERCENT_PHYSICAL("+ local PREFIX: increased single_roll physical_damage",8),
    PERCENT_ELEMENTAL("+ local PREFIX: increased single_roll elemental_damage",8),
    ADDED_PHYSICAL("+ local PREFIX: flat double_roll physical_damage",9),
    ADDED_FIRE("+ local PREFIX: flat double_roll fire_damage",10),
    ADDED_COLD("+ local PREFIX: flat double_roll cold_damage",10),
    ADDED_LIGHTNING("+ local PREFIX: flat double_roll lightning_damage",10),
    ADDED_ABYSSAL("+ local PREFIX: flat double_roll physical_damage",1),
    LOCAL_FIRE_PERCENT("+ local PREFIX: increased single_roll fire_damage",5),
    LOCAL_COLD_PERCENT("+ local PREFIX: increased single_roll cold_damage",5),
    LOCAL_LIGHTNING_PERCENT("+ local PREFIX: increased single_roll lightning_damage",5),
    //TODO: Change to Armor Penetration
    SHRED("+ global PREFIX: percentage single_roll shred",5),
    //TODO: Change to Elemental Penetration
    MAELSTROM("+ global PREFIX: percentage single_roll maelstrom",5),
    //Hybrid Prefixes
    HYBRID_PHYS_ACC("+ local PREFIX: increased single_roll physical_damage & flat single_roll accuracy",8),
    ARMOR_HEALTH("+ local PREFIX: flat single_roll armor & flat single_roll health",4),
    SOUL_HEALTH("+ local PREFIX: flat single_roll soul & flat single_roll health",4),
    DODGE_HEALTH("+ local PREFIX: flat single_roll dodge & flat single_roll health",4),
    ARMOR_SOUL("+ local PREFIX: flat single_roll armor & flat single_roll soul",4),
    ARMOR_DODGE("+ local PREFIX: flat single_roll armor & flat single_roll dodge",4),
    DODGE_SOUL("+ local PREFIX: flat single_roll dodge & flat single_roll soul",4),
    //Meta Prefixes
    @MetaInscription(convertedStat = STRENGTH, convertedValueType = FLAT, rate = 8)
    STRENGTH_TO_FIRE_DMG("+ meta PREFIX: flat double_roll fire_damage",3),

    //Suffixes
    FLAT_STRENGTH("+ global SUFFIX: flat single_roll strength", 8),
    FLAT_DEXTERITY("+ global SUFFIX: flat single_roll dexterity", 8),
    FLAT_INTELLIGENCE("+ global SUFFIX: flat single_roll intelligence", 8),
    HEALTH_REGEN("+ global SUFFIX: flat single_roll health_regen", 8),
    FIRE_RESISTANCE("+ global SUFFIX: percentage single_roll fire_resistance", 8),
    COLD_RESISTANCE("+ global SUFFIX: percentage single_roll cold_resistance", 8),
    LIGHTNING_RESISTANCE("+ global SUFFIX: percentage single_roll lightning_resistance", 8),
    ABYSSAL_RESISTANCE("+ global SUFFIX: percentage single_roll abyssal_resistance", 8),
    LIFE_ON_HIT("+ global SUFFIX: flat single_roll life_on_hit", 5),
    ACCURACY("+ global SUFFIX: flat single_roll accuracy", 5),
    STAMINA_REGEN("+ global SUFFIX: increased single_roll stamina_regen", 6),
    BLEEDING("+ global SUFFIX: percentage single_roll bleed", 4),
    CRITICAL_CHANCE("+ global SUFFIX: percentage single_roll critical_chance", 6),
    COOLDOWN_REDUCTION("+ global SUFFIX: percentage single_roll cooldown_reduction", 4),

    //=====IMPLICITS=====
    MARAUDER_AXE("+ global IMPLICIT: percentage constant shred", 5),
    GLADIATOR_SWORD("+ global IMPLICIT: increased constant accuracy", 5),
    MERCENARY_BOW("+ global IMPLICIT: flat constant dodge", 5),
    ROGUE_DAGGER("+ global IMPLICIT: percentage constant critical_damage", 5),
    SORCERER_WAND("+ global IMPLICIT: percentage constant maelstrom", 5),
    TEMPLAR_MACE("+ global IMPLICIT: increased constant elemental_damage", 5),

    MARAUDER_ARMORED("+ global IMPLICIT: flat single_roll strength", 5),
    GLADIATOR_ORNATE("+ global IMPLICIT: flat single_roll strength & flat single_roll dexterity", 5),
    MERCENARY_CLOTH("+ global IMPLICIT: flat single_roll dexterity", 5),
    ROGUE_PELT("+ global IMPLICIT: flat single_roll dexterity & flat single_roll intelligence", 5),
    SORCERER_SILK("+ global IMPLICIT: flat single_roll intelligence", 5),
    TEMPLAR_RUNISTEEL("+ global IMPLICIT: flat single_roll intelligence & flat single_roll strength", 5),

    PERMAFROST("keystone: PERMAFROST",0),
    THUNDERSTRUCK("keystone: THUNDERSTRUCK",0),
    RIGHTEOUS_FIRE("keystone: RIGHTEOUS_FIRE",0);


    private final InscriptionDefinition definitionData;
    private final int tiers;

    InscriptionIDs(String definitionString, int tiers){
        String finalDefinitionString = definitionString;
        if (hasMetadata()){
            finalDefinitionString += getMetadataDefinitionSection(
                    Objects.requireNonNull(getMetaAnnotationData())
            );
        }
        DefinitionScanner scanner = new DefinitionScanner(finalDefinitionString);
        this.definitionData = scanner.run();
        this.tiers = tiers;
    }

    public boolean hasMetadata(){
        return getMetaAnnotationData() != null;
    }
    public MetaInscription getMetaAnnotationData(){
        try {
            Field inscriptionEnumField = InscriptionIDs.class.getField(this.name());
            if (inscriptionEnumField.isAnnotationPresent(MetaInscription.class)){
                return inscriptionEnumField.getAnnotation(MetaInscription.class);
            }
            return null;
        } catch (NoSuchFieldException e) {
            throw new InscriptionSyntaxException("No Metadata defined for " + this.name());
        }
    }
    private String getMetadataDefinitionSection(MetaInscription metaAnnotation){
        StringBuilder builder = new StringBuilder("<<");
        builder.append(metaAnnotation.rate()).append(" ");
        builder.append(metaAnnotation.convertedValueType()).append(" ");
        builder.append(metaAnnotation.convertedStat());
        return builder.toString();
    }

    public boolean isEffect(){
        return getDefinitionData() instanceof InscriptionDefinition.Effect;
    }
    public boolean isKeystone(){
        return getDefinitionData() instanceof InscriptionDefinition.Keystone;
    }
}
