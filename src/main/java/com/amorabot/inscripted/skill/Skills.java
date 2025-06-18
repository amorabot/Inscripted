package com.amorabot.inscripted.skill;

import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.player.profile.parsing.StatPool;
import com.amorabot.inscripted.skill.archetypes.axe.AxeBasicAttacks;
import com.amorabot.inscripted.skill.archetypes.dagger.DaggerBasicAttacks;
import com.amorabot.inscripted.skill.archetypes.sword.SwordBasicAttacks;
import com.amorabot.inscripted.skill.casting.CastSource;
import com.amorabot.inscripted.skill.casting.CastType;
import com.amorabot.inscripted.skill.type.Attack;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.function.Consumer;
//TODO: Move annotation data to skills.yml file

@Getter
public enum Skills {
    FIST(null, CastType.NEUTRAL, new Tags[]{Tags.NONE},0),
    @AttackSkill( addedBaseDmg = {0,0, 0,0, 0,0, 0,0, 0,0}, dmgEffectiveness = {110, 90, 60, 60, 30}, dmgConversion = {0, 0, 0, 0} )
    BASIC_AXE_SLASH(AxeBasicAttacks::standardAxeSlash,CastType.BASIC_ATTACK, new Tags[]{Tags.MELEE},0),
    @AttackSkill( addedBaseDmg = {0,0, 0,0, 0,0, 0,0, 0,0}, dmgEffectiveness = {90, 90, 90, 90, 40}, dmgConversion = {0, 0, 0, 0} )
    BASIC_SWORD_SLASH(SwordBasicAttacks::standardSwordSlash, CastType.BASIC_ATTACK, new Tags[]{Tags.MELEE},0),
    @AttackSkill( addedBaseDmg = {0,0, 0,0, 0,0, 0,0, 0,0}, dmgEffectiveness = {100, 80, 80, 80, 40}, dmgConversion = {0, 0, 0, 0} )
    BASIC_DAGGER_SLASH(DaggerBasicAttacks::standardDaggerSlash,CastType.BASIC_ATTACK, new Tags[]{Tags.MELEE},0);
//    BASIC_BOW_SHOT(CastType.BASIC_ATTACK, new Tags[]{Tags.PROJECTILE}),
//    BASIC_MACE_SLAM(CastType.BASIC_ATTACK, new Tags[]{Tags.PROJECTILE,Tags.SPELL});

    private final Consumer<Skillcast> skillRoutine;
    private final CastType type;
    private final Tags[] skillTags;
    private final int cooldownInSeconds;

    Skills(Consumer<Skillcast> routine, CastType type, Tags[] skillTags, int cooldown){
        this.skillRoutine = routine;
        this.type = type;
        this.skillTags = skillTags;
        this.cooldownInSeconds = cooldown;
    }


    public void cast(UUID casterID, CastSource source, WeaponAttackSpeeds speedModifier){
        final boolean persistent = false;
        switch (getType()){
            case BASIC_ATTACK, SPECIAL_ATTACK -> {
                if (persistent){
                    // Instantiate a persistent attack
                    return;
                }
                new Attack.Basic(casterID,this,source,speedModifier).start(0,0);
            }
            case MOVEMENT -> {
                //Instantiate a Movement
            }
            case UTILITY -> {
                if (persistent){
                    // Instantiate a Aura
                    return;
                }
                //Instantiate a Utility
            }
            case NEUTRAL -> {
                // xD
            }
            default -> Utils.error("Fucked skillcast :D");
        }
    }
    public void applyBonusStats(StatPool globalPlayerStats){ //Skills with bonus stats should override this method
    }


    public AttackSkill getAttackSkillData(){
        try {
            Field skill = Skills.class.getField(this.name());
            if (skill.isAnnotationPresent(AttackSkill.class)){
                return skill.getAnnotation(AttackSkill.class);
            }
        } catch (NoSuchFieldException e) {
            Utils.error("No AttackSkill data for " + this.name());
        }
        return null;
    }
    public boolean isAttackSkill(){
        return (getAttackSkillData() != null);
    }
}