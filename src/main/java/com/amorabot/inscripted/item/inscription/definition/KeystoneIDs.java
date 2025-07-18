package com.amorabot.inscripted.item.inscription.definition;

import com.amorabot.inscripted.combat.buffs.Buffs;
import com.amorabot.inscripted.combat.buffs.PlayerBuffManager;
import com.amorabot.inscripted.combat.buffs.categories.damage.DamageBuff;
import com.amorabot.inscripted.item.inscription.language.ValueType;
import com.amorabot.inscripted.item.structure.Weapon.DamageTypes;
import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.component.DefenceComponent;
import com.amorabot.inscripted.player.profile.component.HealthComponent;
import com.amorabot.inscripted.player.profile.parsing.StatPool;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.casting.CastSource;
import com.amorabot.inscripted.skill.type.Aura;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public enum KeystoneIDs {
    FORBIDDEN_PACT(TriggerTimes.LATE, true, "") {
        @Override
        public void applyKeystoneStatRule(PlayerDataContainer playerData, StatPool currentPlayerStats) {
            currentPlayerStats.setBaseStatValue(Stats.HEALTH, ValueType.FLAT,new int[]{1});
            currentPlayerStats.setBaseStatValue(Stats.HEALTH, ValueType.INCREASED,new int[]{0});
            currentPlayerStats.setMultiplier(Stats.HEALTH, 1D);
        }
    },
    LETHAL_STRIKES(TriggerTimes.LATE, true, "") {
        @Override
        public void applyKeystoneStatRule(PlayerDataContainer playerData, StatPool currentPlayerStats) {
            double[] shred = currentPlayerStats.calculateStatValue(Stats.SHRED);
            currentPlayerStats.setMultiplier(Stats.SHRED, 0);
            currentPlayerStats.insertValue(Stats.BLEED,ValueType.PERCENTAGE,new int[]{(int) shred[0]});
        }
    },
    BLOOD_PACT(TriggerTimes.CONDITIONAL, true, "") {
        @Override
        public void applyKeystoneStatRule(PlayerDataContainer playerData, StatPool currentPlayerStats) {
            Utils.log("Template Rule for " + this);
        }
    },
    ORGAN_FAILURE(TriggerTimes.CONDITIONAL, true, "") {
        @Override
        public void applyKeystoneStatRule(PlayerDataContainer playerData, StatPool currentPlayerStats) {
            HealthComponent playerHP = playerData.getProfile().getHealthComponent();
            DamageBuff bleed = new DamageBuff(Buffs.BLEED);
            int baseBleed = (int) (playerHP.getMaxHealth() * 0.2); // 4x 20% life
            int[] dot = bleed.convertBaseHit(baseBleed);
            Player strokinPlayer = Bukkit.getPlayer(playerData.getPlayerID());
            bleed.createDamageTask(dot, strokinPlayer, true, strokinPlayer);

            PlayerBuffManager.addBuffToPlayer(bleed, playerData.getPlayerID());
            assert strokinPlayer != null;
            strokinPlayer.sendMessage(Component.text("ORGAN FAILURE..."));
        }
    },
    FIRE_ATTUNEMENT(TriggerTimes.LATE, true, "") {
        @Override
        public void applyKeystoneStatRule(PlayerDataContainer playerData, StatPool currentPlayerStats) {
            int cappedRes = ((int) currentPlayerStats.calculateStatValue(Stats.MAX_FIRE_RESISTANCE)[0]) + DefenceComponent.getResistanceCap();
            currentPlayerStats.setBaseStatValue(Stats.FIRE_RESISTANCE, ValueType.PERCENTAGE,new int[]{cappedRes});
            currentPlayerStats.setMultiplier(Stats.FIRE_RESISTANCE, 1D);
        }
    },
    LIGHTNING_ATTUNEMENT(TriggerTimes.LATE, true, "") {
        @Override
        public void applyKeystoneStatRule(PlayerDataContainer playerData, StatPool currentPlayerStats) {
            int cappedRes = ((int) currentPlayerStats.calculateStatValue(Stats.MAX_LIGHTNING_RESISTANCE)[0]) + DefenceComponent.getResistanceCap();
            currentPlayerStats.setBaseStatValue(Stats.LIGHTNING_RESISTANCE, ValueType.PERCENTAGE,new int[]{cappedRes});
            currentPlayerStats.setMultiplier(Stats.LIGHTNING_RESISTANCE, 1D);
        }
    },
    COLD_ATTUNEMENT(TriggerTimes.LATE, true, "") {
        @Override
        public void applyKeystoneStatRule(PlayerDataContainer playerData, StatPool currentPlayerStats) {
            int cappedRes = ((int) currentPlayerStats.calculateStatValue(Stats.MAX_COLD_RESISTANCE)[0]) + DefenceComponent.getResistanceCap();
            currentPlayerStats.setBaseStatValue(Stats.COLD_RESISTANCE, ValueType.PERCENTAGE,new int[]{cappedRes});
            currentPlayerStats.setMultiplier(Stats.COLD_RESISTANCE, 1D);
        }
    },
    ELEMENTAL_BLESSING(TriggerTimes.LATE, true, "") {
        @Override
        public void applyKeystoneStatRule(PlayerDataContainer playerData, StatPool currentPlayerStats) {
            int highestRes = 0;
            int[] playerElementalResistances = new int[]{
                    (int) currentPlayerStats.calculateStatValue(Stats.FIRE_RESISTANCE)[0],
                    (int) currentPlayerStats.calculateStatValue(Stats.LIGHTNING_RESISTANCE)[0],
                    (int) currentPlayerStats.calculateStatValue(Stats.COLD_RESISTANCE)[0]
            };
            for (int resist : playerElementalResistances){
                if (resist >= highestRes){
                    highestRes = resist;
                }
            }

            //Once the highest value has been found, add elemental pen. for each value corresponding to it
            for (int i = 0; i < playerElementalResistances.length; i++){
                int res = playerElementalResistances[i];
                if (res != highestRes){continue;}
                try {
                    DamageTypes element = DamageTypes.values()[1+i];
                    switch (element){
                        case FIRE -> currentPlayerStats.insertValue(Stats.FIRE_PENETRATION,ValueType.PERCENTAGE,new int[]{33});
                        case LIGHTNING -> currentPlayerStats.insertValue(Stats.LIGHTNING_PENETRATION,ValueType.PERCENTAGE,new int[]{33});
                        case COLD -> currentPlayerStats.insertValue(Stats.COLD_PENETRATION,ValueType.PERCENTAGE,new int[]{33});
                    }
                    Utils.log("Adding "+element+" penetration ("+this+")");
                } catch (IllegalArgumentException exception){
                    Utils.error("Couldn't map element index during "+ this + " execution.");
                }
            }
        }
    },
    AGNOSTIC(TriggerTimes.LATE, true, "") {
        @Override
        public void applyKeystoneStatRule(PlayerDataContainer playerData, StatPool currentPlayerStats) {
            currentPlayerStats.setMultiplier(Stats.FIRE_DAMAGE, 0);
            currentPlayerStats.setMultiplier(Stats.LIGHTNING_DAMAGE, 0);
            currentPlayerStats.setMultiplier(Stats.COLD_DAMAGE, 0);
        }
    },

    BERSERK(TriggerTimes.EARLY, false, ""),
    WINDS_OF_CHANGE(TriggerTimes.EARLY, false, ""),
    PERMAFROST(TriggerTimes.EARLY, false, ""),
    THUNDERSTRUCK(TriggerTimes.EARLY, false, ""),
    RIGHTEOUS_FIRE(TriggerTimes.EARLY, false, "");

    private final TriggerTimes triggerTime;
    private final String description;
    private final boolean rule; // If it's not a Rule, then it must be a Skill


    KeystoneIDs(TriggerTimes triggerTime,boolean isRule,String description){
        this.triggerTime = triggerTime;
        this.rule = isRule;
        this.description = description;
    }

    public void apply(PlayerDataContainer playerData, StatPool currentPlayerStats){
        if (isRule()){
            applyKeystoneStatRule(playerData,currentPlayerStats);
            return;
        }
        castKeystoneSkill(playerData.getPlayerID());
    }
    public void applyKeystoneStatRule(PlayerDataContainer playerData, StatPool currentPlayerStats){
        //Empty body to be implemented by rule Keystones
        if (!rule){
            Utils.error(this + " is not a Keystone Rule.");
            return;
        }
        Utils.log("Unimplemented rule for " + this);
    }

    public void castKeystoneSkill(UUID playerID){
        Skills skill = getKeystoneSkill();
        if (skill==null || isRule()){
            Utils.error("Unable to cast " + this);
            return;
        }
        new Aura(playerID,skill, CastSource.ITEM, WeaponAttackSpeeds.HEAVY).start(2,0);
        Utils.log("Keystone aura toggle for " + this);
    }

    private Skills getKeystoneSkill(){
        try {
            return Skills.valueOf(this.name());
        } catch (IllegalArgumentException e) {
            Utils.error("No registered skill for " + this);
            return null;
        }
    }
}
