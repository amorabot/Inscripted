package com.amorabot.inscripted.skill;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.item.inscription.definition.TriggerTimes;
import com.amorabot.inscripted.item.inscription.definition.TriggerTypes;
import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.item.structure.Weapon.WeaponTypes;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.parsing.StatPool;
import com.amorabot.inscripted.skill.annotations.AttackSkill;
import com.amorabot.inscripted.skill.annotations.AuraSkill;
import com.amorabot.inscripted.skill.annotations.DurationSkill;
import com.amorabot.inscripted.skill.annotations.ProjectileSkill;
import com.amorabot.inscripted.skill.archetypes.axe.AxeBasicAttacks;
import com.amorabot.inscripted.skill.archetypes.axe.AxeMovement;
import com.amorabot.inscripted.skill.archetypes.axe.AxeUtility;
import com.amorabot.inscripted.skill.archetypes.bow.BowBasicAttacks;
import com.amorabot.inscripted.skill.archetypes.bow.BowMovement;
import com.amorabot.inscripted.skill.archetypes.dagger.DaggerBasicAttacks;
import com.amorabot.inscripted.skill.archetypes.dagger.DaggerMovement;
import com.amorabot.inscripted.skill.archetypes.mace.MaceBasicAttacks;
import com.amorabot.inscripted.skill.archetypes.mace.MaceMovement;
import com.amorabot.inscripted.skill.archetypes.mace.MaceSpecialAttacks;
import com.amorabot.inscripted.skill.archetypes.sword.SwordBasicAttacks;
import com.amorabot.inscripted.skill.archetypes.sword.SwordMovement;
import com.amorabot.inscripted.skill.archetypes.wand.WandBasicAttacks;
import com.amorabot.inscripted.skill.archetypes.wand.WandMovement;
import com.amorabot.inscripted.skill.casting.CastSource;
import com.amorabot.inscripted.skill.casting.CastType;
import com.amorabot.inscripted.skill.archetypes.item.ItemAuras;
import com.amorabot.inscripted.skill.routine.projectile.ProjectileGenerators;
import com.amorabot.inscripted.skill.type.Attack;
import com.amorabot.inscripted.skill.type.Aura;
import com.amorabot.inscripted.skill.type.Movement;
import com.amorabot.inscripted.skill.type.Utility;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.function.Consumer;
//TODO: Move annotation data to skills.yml file

@Getter
public enum Skills {
    //Basic attack skills
    FIST(null, CastType.NEUTRAL, new Tags[]{Tags.NONE},0),
    @AttackSkill( addedBaseDmg = {0,0, 0,0, 0,0, 0,0, 0,0}, dmgEffectiveness = {10, -10, -40, -40, -70}, dmgConversion = {0, 0, 0, 0} )
    BASIC_AXE_SLASH(AxeBasicAttacks::standardAxeSlash,CastType.BASIC_ATTACK, new Tags[]{Tags.MELEE},0),

    @AttackSkill( addedBaseDmg = {0,0, 0,0, 0,0, 0,0, 0,0}, dmgEffectiveness = {-10, -10, -10, -10, -60}, dmgConversion = {0, 0, 0, 0} )
    BASIC_SWORD_SLASH(SwordBasicAttacks::standardSwordSlash, CastType.BASIC_ATTACK, new Tags[]{Tags.MELEE},0),

    @AttackSkill( addedBaseDmg = {0,0, 0,0, 0,0, 0,0, 0,0}, dmgEffectiveness = {0, -40, -40, -40, -60}, dmgConversion = {0, 0, 0, 0} )
    @ProjectileSkill( baseProjectiles = 1, spread = ProjectileGenerators.CONE, defaultSteering = SteeringBehaviors.STRAIGHT_LINE, uniqueTarget = false )
    BASIC_BOW_SHOT(BowBasicAttacks::standardBowAttack, CastType.BASIC_ATTACK, new Tags[]{Tags.PROJECTILE}, 0),

    @AttackSkill( addedBaseDmg = {0,0, 0,0, 0,0, 0,0, 0,0}, dmgEffectiveness = {0, -20, -20, -20, -60}, dmgConversion = {0, 0, 0, 0} )
    BASIC_DAGGER_SLASH(DaggerBasicAttacks::standardDaggerSlash,CastType.BASIC_ATTACK, new Tags[]{Tags.MELEE},0),

    @AttackSkill( addedBaseDmg = {0,0, 0,0, 0,0, 0,0, 0,0}, dmgEffectiveness = {20, 0, 0, 0, -60}, dmgConversion = {0, 0, 0, 0} )
    @ProjectileSkill( baseProjectiles = 3, spread = ProjectileGenerators.SHOTGUN, defaultSteering = SteeringBehaviors.SEEK, uniqueTarget = true )
    BASIC_WAND_ATTACK(WandBasicAttacks::standardWandAttack, CastType.BASIC_ATTACK, new Tags[]{Tags.PROJECTILE}, 0),

    @AttackSkill( addedBaseDmg = {0,0, 0,0, 0,0, 0,0, 0,0}, dmgEffectiveness = {0, 0, 0, 0, -60}, dmgConversion = {0, 0, 0, 0} )
    BASIC_MACE_SLAM(MaceBasicAttacks::standardMaceSlam,CastType.BASIC_ATTACK, new Tags[]{Tags.PROJECTILE,Tags.SPELL},0),

    //Movement skills
    CHARGE(AxeMovement::charge,CastType.MOVEMENT, new Tags[0],10),
    LEAP(SwordMovement::leap,CastType.MOVEMENT, new Tags[0],3),//TODO: improve visuals
    ACROBATICS(BowMovement::acrobatics,CastType.MOVEMENT, new Tags[0],5),//TODO: improve visuals
    VANISH(DaggerMovement::vanish,CastType.MOVEMENT, new Tags[0],12),
    WARP(WandMovement::warp,CastType.MOVEMENT, new Tags[0],7),
    TECTONIC_PULL(MaceMovement::pull,CastType.MOVEMENT, new Tags[0],7),//TODO: improve visuals
    
    //Utility Skills
    @DurationSkill(duration = 12, refreshRate = 5) //TODO: castTime?
    WAR_BANNER(AxeUtility::warBanner,CastType.UTILITY, new Tags[0],7),
 
    //Special skills
    @AttackSkill( addedBaseDmg = {0,0, 0,0, 0,0, 0,0, 0,0}, dmgEffectiveness = {30, 10, 10, 10, -70}, dmgConversion = {0, 0, 0, 0} )
    EARTHQUAKE(MaceSpecialAttacks::earthquake,CastType.SPECIAL_ATTACK,new Tags[0],2),

    // Keystone Auras
    @AuraSkill( period = 1, toggleCooldown = -1 )
    PERMAFROST(ItemAuras::registerPermafrost, CastType.NEUTRAL, new Tags[]{Tags.AOE,Tags.AURA},0), //TODO: improve visuals
    @AttackSkill( addedBaseDmg = {0,0, 0,0, 0,0, 15,70, 0,0}, dmgEffectiveness = {0, 0, 50, 0, 0}, dmgConversion = {0, 0, 30, 0} )
    @AuraSkill( period = 1.5, toggleCooldown = -1 )
    THUNDERSTRUCK(ItemAuras::registerThunderstruck, CastType.NEUTRAL, new Tags[]{Tags.AOE,Tags.AURA},0),
    @AuraSkill( period = 0.2, toggleCooldown = -1 )
    RIGHTEOUS_FIRE(ItemAuras::registerRighteousFire, CastType.NEUTRAL, new Tags[]{Tags.AOE,Tags.AURA},0),//TODO: implement dot damage
    @AuraSkill( period = 10, toggleCooldown = -1 )
    WINDS_OF_CHANGE(ItemAuras::registerWindsOfChange, CastType.NEUTRAL, new Tags[]{Tags.AURA},0),
    @AuraSkill( period = 0.5, toggleCooldown = -1 )
    BERSERK(ItemAuras::registerBerserk, CastType.NEUTRAL, new Tags[]{Tags.AURA},0);



    private final Consumer<Skillcast> skillRoutine;
    private final CastType type;
    private final Tags[] skillTags;
    private final int cooldownInSeconds;
    private boolean ignoreOwner = true;

    //TODO: implement cast sound function, Archetype & variantID
    Skills(Consumer<Skillcast> routine, CastType type, Tags[] skillTags, int cooldown, boolean... ignoreOwner){
        this.skillRoutine = routine;
        this.type = type;
        this.skillTags = skillTags;
        this.cooldownInSeconds = cooldown;
        if (ignoreOwner!=null && ignoreOwner.length==1){
            this.ignoreOwner = ignoreOwner[0];
        }
    }


    public void cast(UUID casterID, CastSource source, WeaponAttackSpeeds speedModifier){
        final boolean persistent = this.isDuration();
        if (isAura()){
            if (source.equals(CastSource.ITEM) && getType().equals(CastType.NEUTRAL)){
                Utils.log("Item aura cast!");
            }
            new Aura(casterID,this,source,speedModifier).start(0,0);
            return;
        }
        switch (getType()){
            case BASIC_ATTACK, SPECIAL_ATTACK -> {
                if (persistent){
                    // Instantiate a persistent attack
                    return;
                }
                new Attack.Basic(casterID,this,source,speedModifier).start(0,0);
            }
            case MOVEMENT -> //Instantiate a Movement
            {
                new Movement(casterID,this,source,speedModifier).start(0,0);
                PlayerDataContainer.getDataContainerFor(casterID).onNotify(TriggerTimes.LATE, TriggerTypes.ON_MOVEMENT, Bukkit.getPlayer(casterID),new int[1]);
            }
            case UTILITY -> {
                if (isAura()){
                    // Instantiate a Aura
                    new Aura(casterID,this,source,speedModifier).start(0,0);
                }
                //Instantiate a Utility
                new Utility(casterID,this,source,speedModifier).start(0,0);
            }
            case NEUTRAL -> { // Item-related casts
                Utils.log("NEUTRAL CASTING");
//                if (source.equals(CastSource.ITEM)){
//                    Utils.log("Item Cast!");
//                }
//                new Aura(casterID,this,source,speedModifier).start(0,0);
            }
            default -> {
                Utils.error("Fucked skillcast :D");
            }
        }
        PlayerDataContainer.getDataContainerFor(casterID).onNotify(TriggerTimes.LATE, TriggerTypes.ON_CAST, Bukkit.getPlayer(casterID),new int[1]);

    }
    private static void invalidAbilityCast(Player caster){
        SoundAPI.playGenericSoundAtLocation(caster, caster.getLocation(), "block.note_block.basedrum", 0.9F, 1.0F);
    }
    public void applyBonusStats(StatPool globalPlayerStats){ //Skills with bonus stats should override this method
    }


    public AttackSkill getAttackSkillData(){
        return (AttackSkill) getSkillAnottationData(AttackSkill.class);
    }
    public boolean isAttackSkill(){
        return (getAttackSkillData() != null);
    }


    public ProjectileSkill getProjectileSkilLData(){
        return (ProjectileSkill) getSkillAnottationData(ProjectileSkill.class);
    }
    public boolean isProjectileSkill(){
        return (getProjectileSkilLData() != null);
    }

    public AuraSkill getAuraSkillData(){
        return (AuraSkill) getSkillAnottationData(AuraSkill.class);
    }
    public boolean isAura(){
        return getAuraSkillData() != null;
    }
    public DurationSkill getDurationSkillData(){
        return (DurationSkill) getSkillAnottationData(DurationSkill.class);
    }
    public boolean isDuration(){
        return getDurationSkillData() != null;
    }

    private Annotation getSkillAnottationData(Class<? extends Annotation> annotationClass){
        try {
            Field skill = Skills.class.getField(this.name());
            if (skill.isAnnotationPresent(annotationClass)){
                return skill.getAnnotation(annotationClass);
            }
        } catch (NoSuchFieldException e) {
            Utils.error("No Annotation("+annotationClass.getSimpleName()+") data for " + this.name());
        }
        return null;
    }

    public static BoundingBox getLargeHitbox(Player player){
        BoundingBox playerAABB = player.getBoundingBox();

        playerAABB.expand(0.25, 0.0, 0.25);
        playerAABB.expand(new Vector(0, 1, 0), 0.25);

        return playerAABB;
    }

    public static Skills mapSkillcast(WeaponTypes weapon, CastType type, int variant){
        switch (weapon){
            case AXE -> {
                switch (type){
                    case BASIC_ATTACK -> {
                        //Implement variants later
                        return BASIC_AXE_SLASH;
                    }
                    case MOVEMENT -> {
                        return CHARGE;
                    }
                }
            }
            case SWORD -> {
                switch (type){
                    case BASIC_ATTACK -> {
                        return BASIC_SWORD_SLASH;
                    }
                    case MOVEMENT -> {
                        return LEAP;
                    }
                }
            }
            case BOW -> {
                switch (type){
                    case BASIC_ATTACK -> {
                        return BASIC_BOW_SHOT;
                    }
                    case MOVEMENT -> {
                        return ACROBATICS;
                    }
                }
            }
            case DAGGER -> {
                switch (type){
                    case BASIC_ATTACK -> {
                        return BASIC_DAGGER_SLASH;
                    }
                    case MOVEMENT -> {
                        return VANISH;
                    }
                }
            }
            case WAND -> {
                switch (type){
                    case BASIC_ATTACK -> {
                        return BASIC_WAND_ATTACK;
                    }
                    case MOVEMENT -> {
                        return WARP;
                    }
                }
            }
            case MACE -> {
                switch (type){
                    case BASIC_ATTACK -> {
                        return BASIC_MACE_SLAM;
                    }
                    case MOVEMENT -> {
                        return TECTONIC_PULL;
                    }
                }
            }
        }
        //If there's no match, return null;
        return null;
    }
}