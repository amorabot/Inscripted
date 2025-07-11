package com.amorabot.inscripted.combat.buffs;

import com.amorabot.inscripted.APIs.damageAPI.CombatEffects;
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
    BLEED(true){
        @Override
        public void effectOn(Player player){
            CombatEffects.deathEffect(player);
        }
    },
    @Stat(amount = {20}, valueType = ValueType.INCREASED, targetStat = Stats.ARMOR, durationInSeconds = 5)
    FORTIFY(false),
    @Healing(baseHealing = 1, healingType = ValueType.PERCENTAGE, period = 8, timesApplied = 5)
    REJUVENATE(false),
    @Stat(amount = {30}, valueType = ValueType.FLAT, targetStat = Stats.WALK_SPEED, durationInSeconds = 10)
    TAILWIND(false){
        @Override
        public void effectOn(Player player){
            ParticlePlotter.spawnParticleAt(player.getLocation().toVector(),player.getWorld(), Particle.END_ROD);
        }
    },
    @Stat(amount = {40}, valueType = ValueType.MULTIPLIER, targetStat = Stats.PHYSICAL_DAMAGE, durationInSeconds = 1)
    BERSERK(false){
        @Override
        public void effectOn(Player player){
            ParticlePlotter.spawnParticleAt(
                    player.getLocation().toVector().clone().add(new Vector(0,1.5D, 0)),
                    player.getWorld(), Particle.ANGRY_VILLAGER);
        }
    },
    @Stat(amount = {30}, valueType = ValueType.FLAT, targetStat = Stats.WALK_SPEED, durationInSeconds = 10)
    MAIM(true){
        @Override
        public void effectOn(Player player){
            ParticlePlotter.spawnOffsetColoredParticleAt(player.getLocation().toVector(),player.getWorld(),
                    255, 30,30,
                    0.2f, 2, 1, 0, 1
                    );
        }
    };

    private final boolean debuff;

    Buffs(boolean isDebuff){
        this.debuff = isDebuff;
    }



    public void effectOn(Player player){
    }

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
