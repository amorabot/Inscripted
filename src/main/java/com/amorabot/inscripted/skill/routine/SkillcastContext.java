package com.amorabot.inscripted.skill.routine;

import com.amorabot.inscripted.item.inscription.definition.EffectIDs;
import com.amorabot.inscripted.item.inscription.definition.KeystoneIDs;
import com.amorabot.inscripted.skill.Skills;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SkillcastContext {

    private final UUID attackerID;
    private final Skills skillUsed;
    private final Set<KeystoneIDs> attackKeystones;
    private final Set<EffectIDs> attackEffects;

    public SkillcastContext(Player player, Skills skill){
        this.attackerID = player.getUniqueId();
        this.skillUsed = skill;
        this.attackKeystones = new HashSet<>();
        this.attackEffects = new HashSet<>();
    }

}
