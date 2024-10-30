package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.skills.AbilityRoutines;
import com.amorabot.inscripted.skills.ParticlePlotter;
import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.skills.attackInstances.slam.Slam;
import com.amorabot.inscripted.skills.attackInstances.slam.SlamConfigDTO;
import com.amorabot.inscripted.skills.attackInstances.slam.SlamRenderers;
import com.amorabot.inscripted.skills.attackInstances.slash.Slash;
import com.amorabot.inscripted.skills.attackInstances.slash.SlashConfigDTO;
import com.amorabot.inscripted.skills.attackInstances.slash.SlashSegment;
import com.amorabot.inscripted.skills.math.LinalgMath;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class CastCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)){
            return true;
        }
        Player player = (Player) commandSender;
        if (strings == null){return false;}
        try{
            String selectorArgument = strings[0];

            switch (selectorArgument){
                case "slash":
                    boolean isMirrored = Math.random() > 0.5;
                    boolean isInverted = Math.random() > 0.5;
                    SlashConfigDTO slashConfig = new SlashConfigDTO(
                            20,100,2,-0.2, 0.1,
                            0.3,1.2, new int[]{173, 143, 130}, null, 0.7F, 0.2
                            );

                    Slash slash = new Slash(player, PlayerAbilities.BASIC_SWORD_SLASH,slashConfig,
                            isMirrored,isInverted,false, SlashSegment::standardSword, 30);

                    slash.execute();
                    return true;
                case "slam":

                    return true;


                case "smite":
//                    Function<Slam, Consumer<Vector[]>> impactRenderer = slamData -> segment ->{
                    Consumer<Slam> impactRenderer = slamData -> {
                        Vector slamCenter = slamData.getSlamCenter();
                        World world = slamData.getOwner().getWorld();
                        double slamRadius = slamData.getSlamData().impactRadius();

                        ParticlePlotter.thunderAt(slamCenter.toLocation(world),4,14);
                        ParticlePlotter.plotColoredCircleAt(slamCenter, world, 160,160,160, 1.5F, (float) slamRadius, 16);
                        ParticlePlotter.plotDirectionalCircleAt(slamCenter,world,Particle.ELECTRIC_SPARK, (float) (slamRadius-0.1f), 16, true, 1.2f);
                        ParticlePlotter.plotDirectionalCircleAt(slamCenter,world,Particle.ELECTRIC_SPARK, (float) (slamRadius/2), 16, true, 1.2f);
                    };

                    SlashConfigDTO smiteSlashData = new SlashConfigDTO(18,50,2.2,0,0.2,0.2,0.4,
                            new int[]{230,220,40}, new double[]{1,1},0.7F, 0.2);
                    SlamConfigDTO smiteConfig = new SlamConfigDTO(smiteSlashData, Math.random() > 0.5, 20,
                            1.2, 1.4, 10, 3);

                    Slam smite = new Slam(player, PlayerAbilities.BASIC_MACE_SLAM, smiteConfig, SlamRenderers::standardMaceSlash, impactRenderer);

                    smite.execute();
                    return true;
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            player.sendMessage("Invalid Call");
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        //Add tab completion to spell casts
        return List.of();
    }
}
