package com.amorabot.inscripted.skills.attackInstances;

import com.amorabot.inscripted.components.Items.relic.enums.Effects;
import com.amorabot.inscripted.components.Items.relic.enums.Keystones;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.skills.PlayerAbilities;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AttackContext {

    private final UUID attackerID;
    private final PlayerAbilities sourceAbility;
    private final Set<Keystones> attackKeystones;
    private final Set<Effects> attackEffects;

    public AttackContext(Player player, PlayerAbilities ability){
        this.attackerID = player.getUniqueId();
        this.sourceAbility = ability;
        Profile playerProfile = JSONProfileManager.getProfile(attackerID);
        this.attackKeystones = new HashSet<>();
        attackKeystones.addAll(playerProfile.getKeystones());

        this.attackEffects = new HashSet<>();
        attackEffects.addAll(playerProfile.getEffects());
    }

}
