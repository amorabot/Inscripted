//package com.amorabot.inscripted.components.Mobs;
//
////import com.amorabot.inscripted.components.*;
//import com.amorabot.inscripted.components.Items.relic.enums.Effects;
//import com.amorabot.inscripted.components.Items.relic.enums.Keystones;
//import com.amorabot.inscripted.components.Items.relic.enums.TriggerTimes;
//import com.amorabot.inscripted.components.Items.relic.enums.TriggerTypes;
//import com.amorabot.inscripted.player.profile.component.AttackData;
//import com.amorabot.inscripted.player.profile.component.DefenceComponent;
//import com.amorabot.inscripted.player.profile.component.HealthComponent;
//import com.amorabot.inscripted.utils.Utils;
//import lombok.Getter;
//import org.bukkit.entity.LivingEntity;
//
//import java.io.Serializable;
//import java.util.HashSet;
//import java.util.Set;
//
//@Getter
//public class MobStats implements Serializable{
//
//    private final int mobLevel;
//    private final AttackData mobHit;
//    private final HealthComponent mobHealth;
//    private final DefenceComponent mobDefence;
//    private final Set<Keystones> mobKeystones = new HashSet<>();
//    private final Set<Effects> mobEffects = new HashSet<>();
//
//    public MobStats(int mobLevel, AttackData mobHit, HealthComponent mobHP, DefenceComponent mobDef){
//        this.mobLevel = mobLevel;
//        this.mobHit = mobHit;
//        this.mobHealth = mobHP;
//        this.mobDefence = mobDef;
//    }
//}
