package com.amorabot.inscripted.combat.buffs;

import com.amorabot.inscripted.combat.CombatEffects;
import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.item.inscription.language.ValueType;
import com.amorabot.inscripted.item.structure.Weapon.DamageTypes;
import com.amorabot.inscripted.combat.buffs.categories.damage.Damage;
import com.amorabot.inscripted.combat.buffs.categories.healing.Healing;
import com.amorabot.inscripted.combat.buffs.categories.stat.Stat;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

//Note: A negative healing buff is just a true-damage DPS :D

@Getter
public enum Buffs {

    @Damage(baseDamageType = DamageTypes.PHYSICAL, period = 20, timesApplied = 4)
    BLEED("Bleed",true, "You are bleeding..."){
        @Override
        public void effectOn(Player player){
            CombatEffects.deathEffect(player);
        }
    },
    @Stat(amount = {20}, valueType = ValueType.INCREASED, targetStat = Stats.ARMOR, durationInSeconds = 5)
    FORTIFY("Fortify",false, "You feel... protected"){
        @Override
        public void effectOn(Player player){
            ParticlePlotter.plotCircleAt(player.getLocation().toVector(), player.getWorld(),Particle.CRIT,0.7f,7);
        }
    },
    @Stat(amount = {20}, valueType = ValueType.PERCENTAGE, targetStat = Stats.MELEE_DAMAGE, durationInSeconds = 8)
    ADRENALINE("Adrenaline",false, "You feel a sudden rush of blood..."){
        @Override
        public void effectOn(Player player){
            ParticlePlotter.plotCircleAt(player.getLocation().toVector(), player.getWorld(),Particle.RAID_OMEN,0.7f,5);
        }
    },
    @Healing(baseHealing = 1, healingType = ValueType.PERCENTAGE, period = 8, timesApplied = 5)
    REJUVENATE("Rejuvenate",false, "You feel... renewed"),
    @Stat(amount = {30}, valueType = ValueType.FLAT, targetStat = Stats.WALK_SPEED, durationInSeconds = 10)
    TAILWIND("Tailwind",false, "The wind is on your favor"){
        @Override
        public void effectOn(Player player){
            ParticlePlotter.spawnParticleAt(player.getLocation().toVector(),player.getWorld(), Particle.END_ROD);
        }
    },
    @Stat(amount = {30}, valueType = ValueType.INCREASED, targetStat = Stats.ARMOR, durationInSeconds = 12)
    HUNTING_GROUNDS_EXPOSURE("Exposure",true, "You feel... vulnerable"){
        @Override
        public void effectOn(Player player){
            Vector overHead = player.getLocation().clone().add(0,2.1,0).toVector();
            ParticlePlotter.spawnColoredParticleAt(overHead,player.getWorld(), 209, 27, 6, 0.5f, 1);
            ParticlePlotter.spawnParticleAt(overHead, player.getWorld(), Particle.SMOKE);
        }
    },
    @Stat(amount = {50}, valueType = ValueType.FLAT, targetStat = Stats.ACCURACY, durationInSeconds = 10)
    HUNTING_GROUNDS_PRECISION("Precision",false, "You can see clearly and precisely"){
        @Override
        public void effectOn(Player player){
            ParticlePlotter.spawnColoredParticleAt(player.getLocation().clone().add(0,2.1,0).toVector(),player.getWorld(), 94, 156, 53, 0.5f, 1);
        }
    },
    @Stat(amount = {40}, valueType = ValueType.MULTIPLIER, targetStat = Stats.PHYSICAL_DAMAGE, durationInSeconds = 1)
    BERSERK("Berserk",false, "Rage fills you from within"){
        @Override
        public void effectOn(Player player){
            ParticlePlotter.spawnParticleAt(
                    player.getLocation().toVector().clone().add(new Vector(0,1.5D, 0)),
                    player.getWorld(), Particle.ANGRY_VILLAGER);
        }
    },
    @Stat(amount = {30}, valueType = ValueType.FLAT, targetStat = Stats.WALK_SPEED, durationInSeconds = 10)
    MAIM("Maim",true, "Your feel heavy..."){
        @Override
        public void effectOn(Player player){
            ParticlePlotter.spawnOffsetColoredParticleAt(player.getLocation().toVector(),player.getWorld(),
                    255, 30,30,
                    0.2f, 2, 1, 0, 1
                    );
        }
    };

    @Getter
    private final String alias;
    private final boolean debuff;
    private final String applyMessage;

    Buffs(String buffAlias, boolean isDebuff, String message){
        this.alias = buffAlias;
        this.debuff = isDebuff;
        this.applyMessage = message;
    }



    public void effectOn(Player player){
    } // What in the abstraction is this

    public Annotation getBuffAnnotationData(){ //Exclusive to 1 for now
        try {
            Field enumConstant = Buffs.class.getField(this.name());

            if (enumConstant.isAnnotationPresent(Stat.class)){
                return enumConstant.getAnnotation(Stat.class);
            }

            if (enumConstant.isAnnotationPresent(Damage.class)){
                return enumConstant.getAnnotation(Damage.class);
            }

            if (enumConstant.isAnnotationPresent(Healing.class)){
                return enumConstant.getAnnotation(Healing.class);
            }
        } catch (NoSuchFieldException e) {
            Utils.error("Unable to find enum constant field for: " + this.name() + " @"+this.getClass().getSimpleName());
        }
        return null;
    }

    public boolean isHealingBuff(){
        Annotation annotationMetadata = getBuffAnnotationData();
        return annotationMetadata instanceof Healing;
    }
    public boolean isDamageBuff(){
        Annotation annotationMetadata = getBuffAnnotationData();
        return annotationMetadata instanceof Damage;
    }
    public boolean isStatBuff(){
        Annotation annotationMetadata = getBuffAnnotationData();
        return annotationMetadata instanceof Stat;
    }
}
