package com.amorabot.inscripted.components.Items.DataStructures.Enums;

public enum TargetStats {
    STRENGTH,
    STRENGTH_DEXTERITY,
    DEXTERITY,
    DEXTERITY_INTELLIGENCE,
    INTELLIGENCE,
    INTELLIGENCE_STRENGTH,
    STAMINA,
    STAMINA_REGEN,
    HEALTH,
    HEALTH_REGEN,
    WARD,
    WARD_HEALTH,
    DODGE,
    DODGE_WARD,
    ARMOR,
    ARMOR_DODGE,
    ARMOR_WARD,
    DODGE_HEALTH,
    ARMOR_HEALTH,
    FIRE_RESISTANCE,
    COLD_RESISTANCE,
    LIGHTNING_RESISTANCE,
    ABYSSAL_RESISTANCE,
    PHYSICAL_DAMAGE,
    /*
    * Modifiers compostos como PHYSICALDAMAGE_ACCURACY devem seguir essa regra de nomenclatura pois, durante o processo
    * de compilação dos stats, todos os modificadores que tem como target PHYSICAL_DAMAGE são reconhecidos pela key "PHYSICALDAMAGE"
    *
    * Isso simplifica o processo de redirecionamento de stats hibridos onde o nome teria mais de uma "_", permitindo separar imediatamente
    * o modificador em 2 tokens coerentes para o compilador, "PHYSICALDAMAGE" e "ACCURACY".
    *
    * O mesmo resultado poderia ser obtido ao dar para o compilador o contexto de que se trata de um modificador hibrido com nome composto
    * (No caso, PHYSICAL_DAMAGE) e efetuar as devidas transformacoes na string. Mas para fins de simplicidade e estrutura, mods hibridos
    * com nomes compostos devem ser declarados já em sua forma simplificada.
     */
    PHYSICALDAMAGE_ACCURACY,
    FIRE_DAMAGE,
    COLD_DAMAGE,
    LIGHTNING_DAMAGE,
    ELEMENTAL_DAMAGE,
    ABYSSAL_DAMAGE,
    ACCURACY,
    BLEED,
    CRITICAL,
//    BASE_CRITICAL,
    CRITICAL_DAMAGE,
    LIFE_ON_HIT,
    LIFESTEAL,
    SHRED,
    MAELSTROM,
    WALK_SPEED,
}
