package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.skill.casting.CastSource;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.routine.slam.Slam;
import com.amorabot.inscripted.skill.routine.slam.SlamConfig;
import com.amorabot.inscripted.skill.routine.slam.SlamRenderers;
import com.amorabot.inscripted.skill.routine.slash.SlashConfig;
import com.amorabot.inscripted.skill.routine.slash.SlashPresets;
import com.amorabot.inscripted.skill.routine.slash.SlashSegment;
import com.amorabot.inscripted.skill.type.Attack;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CastCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)){
            return true;
        }
        Player player = (Player) commandSender;
        UUID playerID = player.getUniqueId();
        if (strings == null){return false;}
        try{
            Skills skill = Skills.valueOf(strings[0]);
            skill.cast(playerID,CastSource.PLAYER,WeaponAttackSpeeds.FAST);
//            new Attack.Basic(playerID,skill, CastSource.PLAYER, WeaponAttackSpeeds.FAST).start(0,0);
//            switch (strings[0]){
//                case "smite":


//                    SlashConfig smiteSlashData = new SlashConfig(18,50,2.2,0,0.2,0.2,0.4,
//                            new int[]{230,220,40}, new double[]{1,1},0.7F, 0.2);
//                    SlamConfigDTO smiteConfig = new SlamConfigDTO(smiteSlashData, Math.random() > 0.5, 20,
//                            1.2, 1.4, 10, 3);
////
//                    Slam smite = new Slam(player, PlayerAbilities.BASIC_MACE_SLAM, smiteConfig, SlamRenderers::standardMaceSlash, impactRenderer);
//
//                    smite.execute();
//                    return true;
//            }
        } catch (IllegalArgumentException e) {
            player.sendMessage("Invalid Call");
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return Arrays.stream(Skills.values()).map(Enum::name).collect(Collectors.toList());
    }
}
