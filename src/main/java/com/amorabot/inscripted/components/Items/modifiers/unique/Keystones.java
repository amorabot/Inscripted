package com.amorabot.inscripted.components.Items.modifiers.unique;


import com.amorabot.inscripted.components.Attack;
import com.amorabot.inscripted.components.DamageComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerPassivesManager;
import com.amorabot.inscripted.skills.AbilityRoutines;
import com.amorabot.inscripted.skills.PlayerAbilities;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public enum Keystones {
    //Add "early" boolean? -> Checking for early/late effect on profile
    //Add int[] values -> would indicate what values need to be used when applying, if any

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
                PlayerAbilities.PERMAFROST.cast(player); //Creates a new runnable and store its ID in the passivesManager
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
                int taskID = AbilityRoutines.initializeBerserkToggleMonitorFor(player);
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
                PlayerAbilities.THUNDERSTRUCK.cast(player);
            }
        }
    },
    BLOOD_PACT(TriggerTimes.LATE, false, false,
            "Your health rege-","neration is 0", "", "HEALS on you are", "twice as powerful") {
        @Override
        public void apply(UUID playerID) {
            Profile playerProfile = JSONProfileManager.getProfile(playerID);
            playerProfile.getHealthComponent().setHealthRegen(0);
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
