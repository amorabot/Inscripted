package com.amorabot.inscripted.tasks;

import com.amorabot.inscripted.item.inscription.definition.KeystoneIDs;
import com.amorabot.inscripted.player.profile.component.HealthComponent;
import com.amorabot.inscripted.tasks.base.PlayerboundTask;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.amorabot.inscripted.player.PlayerDataContainer.getPlayerEquipment;
import static com.amorabot.inscripted.player.PlayerDataContainer.getProfile;

public class RegenerationTask extends PlayerboundTask {
    private static final Map<UUID, Long> soulRegenCooldownMap = new HashMap<>();

    public static final long regenTimerCooldown = 20;
    private static final int secondsBeforeWardRecharge = 4;

    public RegenerationTask(UUID playerID){
        super(playerID);
    }

    @Override
    protected void taskRoutine(Player player) {
        player.setFoodLevel(20);
        player.setSaturation(1);

        HealthComponent playerHealth = getProfile(getPlayerID()).getHealthComponent();

        boolean inCombat = false;

        Set<KeystoneIDs> playerKeystones = getPlayerEquipment(getPlayerID()).getSpecialInscriptions().getKeystones();
        int regeneratedHealth = playerHealth.regenHealth(inCombat,playerKeystones);

        boolean canRegenSoul = canRegenSoul(getPlayerID());
        if ((playerHealth.getMaxSoul() > 0) && (playerHealth.getSoul() < playerHealth.getMaxSoul()) && (canRegenSoul)){
            int regeneratedSoul = playerHealth.regenSoul(inCombat);
        }
        HealthComponent.updateHealthHearts(player,playerHealth);
        HealthComponent.updateSoulHearts(player,playerHealth);
        //Create HPS display
    }




    public static void startSoulRegenCooldownFor(UUID playerID){
        soulRegenCooldownMap.put(playerID, System.currentTimeMillis());
    }

    public static boolean canRegenSoul(UUID playerID){
        if (soulRegenCooldownMap.containsKey(playerID)){
            int timeSincelastHit = (int) ((System.currentTimeMillis() - soulRegenCooldownMap.get(playerID))/1000);
            //If not enough time's passed since the that player's last hit, they cant regen ward
            return timeSincelastHit >= secondsBeforeWardRecharge;
        }
        //Player hasn't been hit yet
        return true;
    }
}
