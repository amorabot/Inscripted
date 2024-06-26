package com.amorabot.inscripted.components.Items.modifiers.unique;


import com.amorabot.inscripted.components.Attack;
import com.amorabot.inscripted.components.DamageComponent;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.buffs.Buffs;
import com.amorabot.inscripted.components.buffs.categories.damage.DamageBuff;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerBuffManager;
import com.amorabot.inscripted.managers.PlayerPassivesManager;
import com.amorabot.inscripted.skills.AbilityRoutines;
import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.skills.item.StatToggleMonitors;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@Getter
public enum Keystones {
    //Add "early" boolean? -> Checking for early/late effect on profile
    //Add int[] values -> would indicate what fixed values need to be used when applying, if any

    FORBIDDEN_PACT(TriggerTimes.LATE, false, false,"The abyssal gaze","has changed you."," ",
            "Become immune to","☽ Abyssal DMG",
            "Your maximum ","❤ Health is 1") {
        @Override
        public void apply(UUID playerID) {
            Profile playerProfile = JSONProfileManager.getProfile(playerID);
            playerProfile.getHealthComponent().setCurrentHealth(1);
            playerProfile.getHealthComponent().setMaxHealth(1);
        }
    }, //Chaos inoc
    LETHAL_STRIKES(TriggerTimes.LATE, false, false,
            "You aim for the","weak spots."," ",
            "All Shred is con-","verted to Bleed","chance.") {
        @Override
        public void apply(UUID playerID) {
            Profile playerProfile = JSONProfileManager.getProfile(playerID);
            DamageComponent damageComponent = playerProfile.getDamageComponent();
            Attack playerAttackData = damageComponent.getHitData();
            int shred = playerAttackData.getShred();
            playerAttackData.setShred(0);
            playerAttackData.setBleedChance(playerAttackData.getBleedChance()+shred);
        }
    },
    PERMAFROST(TriggerTimes.EARLY, true, false,
            "Permanent","Slowness I", " ",
            "Every 3s, slow", "nearby entities"){
        @Override
        public void apply(UUID playerID) {
            if (needsInstantiation(playerID)){
                Player player = Bukkit.getPlayer(playerID);
                assert player != null;
                PlayerAbilities.PERMAFROST_PASSIVE.cast(player, null); //Creates a new runnable and store its ID in the passivesManager
            }
        }
    },
    WINDS_OF_CHANGE(TriggerTimes.EARLY, true, false,
            "You feel blessed","by your ancestry.","",
            "Gain Rejuvenate","every 1m,","healing you for","","(5%) Max. ❤ Health","over 2s") {
        @Override
        public void apply(UUID playerID) {
            if (needsInstantiation(playerID)){
                Player player = Bukkit.getPlayer(playerID);
                assert player != null;
                PlayerAbilities.WINDS_OF_CHANGE_PASSIVE.cast(player, null);
            }
        }
    },
    BERSERK(TriggerTimes.EARLY, true, true,
            "When below "+HealthComponent.LOW_LIFE_THRESHOLD+"%","❤ Health"," ",
            "gain 50% more","physical dmg.") {
        @Override
        public void apply(UUID playerID) {
            if (needsInstantiation(playerID)){
                Player player = Bukkit.getPlayer(playerID);
                assert player != null;
                int taskID = StatToggleMonitors.initializeBerserkToggleMonitorFor(player);
                PlayerPassivesManager.addKeystonePassive(playerID, this, taskID);
            }
        }
        @Override
        public void addExternalStat(UUID playerID){
            int[] berserkStatValues = new int[]{50};
            Profile playerProfile = JSONProfileManager.getProfile(playerID);
            playerProfile.getStats().updateExternalStat(
                    PlayerStats.PHYSICAL_DAMAGE,
                    ValueTypes.MULTIPLIER,
                    berserkStatValues);
        }
    },
    THUNDERSTRUCK(TriggerTimes.EARLY, true, false,
            "Every 1.5s, get","struck by", "lightning, dealing", " ",
            "50 + (15%) " + DamageTypes.LIGHTNING.getCharacter() + " DMG"){
        @Override
        public void apply(UUID playerID) {
            if (needsInstantiation(playerID)){
                Player player = Bukkit.getPlayer(playerID);
                assert player != null;
                PlayerAbilities.THUNDERSTRUCK_PASSIVE.cast(player, null);
            }
        }
    },
    BLOOD_PACT(TriggerTimes.CONDITIONAL, false, false,
            "Your cant rege-","nerate life.", "", "HEALS on you are", "twice as powerful") {
        @Override
        public void apply(UUID playerID) {
        }
    },
    ORGAN_FAILURE(TriggerTimes.CONDITIONAL, false, false,
            "When regenerating to","full life, Bleed", "for 80% of your max.","❤ Health") {
        @Override
        public void apply(UUID playerID) {
            Player player = Bukkit.getPlayer(playerID);
            if (player == null){return;}
            HealthComponent playerHP = JSONProfileManager.getProfile(playerID).getHealthComponent();
            DamageBuff bleed = new DamageBuff(Buffs.BLEED);
            int baseBleed = (int) (playerHP.getMaxHealth() * 0.2);
            int[] dot = bleed.convertBaseHit(baseBleed);
            bleed.createDamageTask(dot, player, true, player);

            PlayerBuffManager.addBuffToPlayer(bleed, player);
            Utils.msgPlayer(player, ColorUtils.translateColorCodes("&4&lORGAN FAILURE..."));
        }
    },
    FIRE_ATTUNEMENT(TriggerTimes.LATE, false, false,
            "You become one with","the ashes.","","Your fire resistance","is maxed.") {
        @Override
        public void apply(UUID playerID) {
            DefenceComponent def = JSONProfileManager.getProfile(playerID).getDefenceComponent();
            def.setFireResistance(def.getBaseElementalCap()+def.getFireCapMod());
        }
    },
    LIGHTNING_ATTUNEMENT(TriggerTimes.LATE, false, false,
            "You become one with","the storm.","","Your lightning res."," is maxed.") {
        @Override
        public void apply(UUID playerID) {
            DefenceComponent def = JSONProfileManager.getProfile(playerID).getDefenceComponent();
            def.setLightningResistance(def.getBaseElementalCap()+def.getLightningCapMod());
        }
    },
    COLD_ATTUNEMENT(TriggerTimes.LATE, false, false,
            "You become one with","the blizzard.","","Your cold resistance","is maxed.") {
        @Override
        public void apply(UUID playerID) {
            DefenceComponent def = JSONProfileManager.getProfile(playerID).getDefenceComponent();
            def.setColdResistance(def.getBaseElementalCap()+def.getColdCapMod());
        }
    },
    ELEMENTAL_BLESSING(TriggerTimes.LATE, false, false,
            "For whichever uncapped","elemental resistance is","higher, get 33% penetration", "for that element.") {
        @Override
        public void apply(UUID playerID) {
            Profile playerProfile = JSONProfileManager.getProfile(playerID);
            DefenceComponent playerDef = playerProfile.getDefenceComponent();
            Attack hitData = playerProfile.getDamageComponent().getHitData();

            int highestRes = 0;
            int[] playerElementalResistances = new int[]{
                    playerDef.getFireResistance(),
                    playerDef.getLightningResistance(),
                    playerDef.getColdResistance()
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
                        case FIRE -> hitData.setFirePen(hitData.getFirePen()+33);
                        case LIGHTNING -> hitData.setLightningPen(hitData.getLightningPen()+33);
                        case COLD -> hitData.setColdPen(hitData.getColdPen()+33);
                    }
                    Utils.log("Adding "+element+" penetration ("+this+")");
                } catch (IllegalArgumentException exception){
                    Utils.error("Couldn't map element index during "+ this + " execution.");
                }
            }
        }
    },
    AGNOSTIC(TriggerTimes.LATE, false, false,
            "Your connection to","the arcane is","severed.","","Your Elemental DMG is 0") {
        @Override
        public void apply(UUID playerID) {
            Profile playerProfile = JSONProfileManager.getProfile(playerID);
            Attack hitData = playerProfile.getDamageComponent().getHitData();
            Map<DamageTypes, int[]> playerDamage = hitData.getDamages();
            playerDamage.put(DamageTypes.FIRE, new int[2]);
            playerDamage.put(DamageTypes.LIGHTNING, new int[2]);
            playerDamage.put(DamageTypes.COLD, new int[2]);
        }
    };

    private final TriggerTimes compilationTime;
    private final boolean passiveTask;
    private final boolean statKeystone;
    private final String[] description;

    Keystones(TriggerTimes compilationTime, boolean hasTask, boolean isStatKeystone, String... description){
        this.compilationTime = compilationTime;
        this.passiveTask = hasTask;
        this.statKeystone = isStatKeystone;
        this.description = description;
    }

    public abstract void apply(UUID playerID);
    public boolean needsInstantiation(UUID playerID){
        //Checking if, for that player and for this keystone, a task is already instantiated
        return !PlayerPassivesManager.isPassiveRunning(playerID, this);
    }
    public void addExternalStat(UUID playerID){
    }
}
