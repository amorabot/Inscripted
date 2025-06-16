package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.component.DamageComponent;
import com.amorabot.inscripted.skill.CastSource;
import com.amorabot.inscripted.skill.PlayerAbilities;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.type.Attack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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
            new Attack.Basic(playerID,skill, CastSource.PLAYER, WeaponAttackSpeeds.FAST).start(0,0);

//            switch (selectorArgument){
//                case "slash":
////                    boolean isMirrored = Math.random() > 0.5;
////                    boolean isInverted = Math.random() > 0.5;
////                    SlashConfig slashConfig = new SlashConfig(
////                            20,100,2,-0.2, 0.1,
////                            0.3,1.2, new int[]{173, 143, 130}, null, 0.7F, 0.2
////                            );
////
////                    Slash slash = new Slash(player, PlayerAbilities.BASIC_SWORD_SLASH,slashConfig,
////                            isMirrored,isInverted,false, SlashSegment::standardSword, 30);
//
////                    slash.execute();
//                    return true;
//                case "slam":
//
//                    return true;
//
//
//                case "smite":
////                    Consumer<Slam> impactRenderer = slamData -> {
////                        Vector slamCenter = slamData.getSlamCenter();
////                        World world = slamData.getOwner().getWorld();
////                        double slamRadius = slamData.getSlamData().impactRadius();
////
////                        ParticlePlotter.thunderAt(slamCenter.toLocation(world),4,14);
////                        ParticlePlotter.plotColoredCircleAt(slamCenter, world, 160,160,160, 1.5F, (float) slamRadius, 16);
////                        ParticlePlotter.plotDirectionalCircleAt(slamCenter,world,Particle.ELECTRIC_SPARK, (float) (slamRadius-0.1f), 16, true, 1.2f);
////                        ParticlePlotter.plotDirectionalCircleAt(slamCenter,world,Particle.ELECTRIC_SPARK, (float) (slamRadius/2), 16, true, 1.2f);
////                    };
////
////                    SlashConfig smiteSlashData = new SlashConfig(18,50,2.2,0,0.2,0.2,0.4,
////                            new int[]{230,220,40}, new double[]{1,1},0.7F, 0.2);
////                    SlamConfigDTO smiteConfig = new SlamConfigDTO(smiteSlashData, Math.random() > 0.5, 20,
////                            1.2, 1.4, 10, 3);
////
////                    Slam smite = new Slam(player, PlayerAbilities.BASIC_MACE_SLAM, smiteConfig, SlamRenderers::standardMaceSlash, impactRenderer);
////
////                    smite.execute();
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
