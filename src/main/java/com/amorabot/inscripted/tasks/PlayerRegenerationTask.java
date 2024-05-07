package com.amorabot.inscripted.tasks;

import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerRegenManager;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;

public class PlayerRegenerationTask extends BukkitRunnable {

    private final UUID playerID;

    public PlayerRegenerationTask(UUID playerID){
        this.playerID = playerID;
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(playerID);

        if (player == null) {
            Utils.error("Invalid player regen call, cancelling task for  (@Regen task)");
            this.cancel();
            return;
        }

        Profile playerProfile = JSONProfileManager.getProfile(playerID);
        HealthComponent HPComponent = playerProfile.getHealthComponent();

        //Reset food levels (TEMPORARY)!!!!!!!!!!!!!
        player.setFoodLevel(10);
        player.setSaturation(0);

        //If the player is pvp tagged, dont regen life
        boolean isPvPTagged = player.hasMetadata(CombatLogger.getPvpTag());

        StringBuilder regenString = new StringBuilder();


        if (HPComponent.getCurrentHealth() != HPComponent.getMaxHealth() && !isPvPTagged){
            int HPS = HPComponent.getHealthRegen();


            //TODO: Make a specific logger for player debug messages
            if (player.hasMetadata(CombatLogger.getCombatTag())){
                HPS = HPS/2;
                regenString.append("&e&l+");
            } else {
                regenString.append("&a&l+");
            }
            regenString.append(HPS);

            HPComponent.regenHealth(HPS);

            //TODO: Encapsulate Health and Ward mapping
            double mappedHealth = HPComponent.getMappedHealth(20);
            if ((mappedHealth - player.getHealth()) >= 0.5D){
                player.setHealth(mappedHealth);
            }
        }

        if (HPComponent.getCurrentWard() != HPComponent.getMaxWard() && (PlayerRegenManager.canRegenWard(playerID))){
            float wardRegen = HPComponent.getWardRegenTick();

            if (isPvPTagged){
                wardRegen = wardRegen/2;
            }
            HPComponent.regenWard(wardRegen);
            double mappedWard = HPComponent.getMappedWard(20);
            if ((mappedWard - player.getAbsorptionAmount()) >= 0.5D){
                player.setAbsorptionAmount(mappedWard);
            }

            regenString.append(" ").append(DefenceTypes.WARD.getTextColor()).append("&l+").append((int) (wardRegen));
        }

        if (!regenString.isEmpty()){
            CombatHologramsDepleter.getInstance().instantiateRegenHologram(player.getLocation(), regenString.toString());
        }
    }
}
