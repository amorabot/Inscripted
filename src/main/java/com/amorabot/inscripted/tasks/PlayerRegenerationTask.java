package com.amorabot.inscripted.tasks;

import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Items.modifiers.unique.Keystones;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerRegenManager;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.UUID;

import static com.amorabot.inscripted.components.HealthComponent.updateHealthHearts;
import static com.amorabot.inscripted.components.HealthComponent.updateWardHearts;

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

            boolean inCombat = player.hasMetadata(CombatLogger.getCombatTag());
            Set<Keystones> playerKeystones = playerProfile.getKeystones();
            if (!playerKeystones.contains(Keystones.BLOOD_PACT)){
                int HPS = HPComponent.regenHealth(inCombat, playerID);
                if (HPS > 0){
                    if (inCombat){regenString.append("&e&l+");}
                    else {regenString.append("&a&l+");}
                    regenString.append(HPS);

                    updateHealthHearts(player, HPComponent);
                }
            } else {
                updateHealthHearts(player, HPComponent);
            }

        }

        if (HPComponent.getCurrentWard() != HPComponent.getMaxWard() && (PlayerRegenManager.canRegenWard(playerID))){
            int wardRegen = HPComponent.regenWard(isPvPTagged);

            updateWardHearts(player, HPComponent);

            regenString.append(" ").append(DefenceTypes.WARD.getTextColorTag()).append("&l+").append((int) (wardRegen));
        }

        if (!regenString.isEmpty()){
//            CombatHologramsDepleter.getInstance().instantiateRegenHologram(player.getLocation(), regenString.toString());
            CombatHologramsDepleter.getInstance().instantiateRegenHologram(player.getLocation(),
                    regenString.toString());
        }
    }
}
