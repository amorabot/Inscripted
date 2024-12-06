package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
import com.amorabot.inscripted.components.Items.ItemBuilder;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.components.Items.modifiers.data.Meta;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
import com.amorabot.inscripted.components.buffs.Buffs;
import com.amorabot.inscripted.components.buffs.categories.damage.DamageBuff;
import com.amorabot.inscripted.components.buffs.categories.healing.HealingBuff;
import com.amorabot.inscripted.components.buffs.categories.stat.StatBuff;
import com.amorabot.inscripted.components.renderers.ItemInterfaceRenderer;
import com.amorabot.inscripted.inscriptions.InscriptionTable;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerBuffManager;
import com.amorabot.inscripted.skills.math.LinalgMath;
import com.amorabot.inscripted.skills.ParticlePlotter;
import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.skills.SteeringBehaviors;
import com.amorabot.inscripted.skills.attackInstances.projectile.Projectile;
import com.amorabot.inscripted.skills.archetypes.bow.BowBasicAttacks;
import com.amorabot.inscripted.skills.math.OrientedBoundingBox;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TemplateCommand implements CommandExecutor {

    /*
    entity.wither_skeleton.ambient
    wither.break_block
    minecraft:entity.arrow.hit_player
    entity.player.attack.knockback - dish dish

     */

    public static Skeleton testDummy = null;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player)){
            return false;
        }
        Player player = (Player) commandSender;
        World playerWorld = player.getWorld();

        if (strings.length == 1){
            String action = strings[0];
            switch (action){
                case "mod":
                    for (InscriptionID inscription : InscriptionID.values()){
                        if (inscription.getData().getAffixType().equals(Affix.IMPLICIT)){
                            player.sendMessage(inscription.getDisplayName() + "  IMPLICIT!!  " + inscription.getTotalTiers());
                            continue;
                        }
                        try {
                            if (InscriptionID.class.getField(inscription.name()).isAnnotationPresent(Meta.class)){
                                player.sendMessage("TESTANDO METAMOD!");
                            }
                        } catch (NoSuchFieldException e) {
                            player.sendMessage("NOT A METAMOD");
                            throw new RuntimeException(e);
                        }
                        player.sendMessage(inscription.getDisplayName() + "   " + inscription.getTotalTiers());
                    }
                    break;
                case "bar":
                    Component bar = JSONProfileManager.getProfile(player.getUniqueId()).getHealthComponent().getHealthBarComponent();
//                    player.sendMessage(bar);
                    Inscripted.getPlugin().getWorld().spawn(player.getLocation(), TextDisplay.class, textDisplay -> {
                        textDisplay.text(bar);
                        textDisplay.setBillboard(Display.Billboard.CENTER);
                        textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
                        textDisplay.setTextOpacity((byte) 255);

//                        textDisplay.setInterpolationDelay(1);
//                        textDisplay.setInterpolationDuration(14);
                        textDisplay.setPersistent(false);
                    });
                    break;
                case "toggle":
                    //Not persistent (ideal for temporary tags/ownership/toggles that are not essential in combat) -> if persistance is needed: scoreboard tags
                    if (!player.hasMetadata("Player")){
                        player.sendMessage("you are not a player!, turning you into one");
                        player.setMetadata("Player", new FixedMetadataValue(Inscripted.getPlugin(), "im a player!"));
                    } else {
                        player.sendMessage("you are a player! not anymore");
                        player.removeMetadata("Player", Inscripted.getPlugin());
                    }
                    return true;
                case "color":
                    String temp = "&"+ Archetypes.GLADIATOR.getColor() + " :D " + "&7testando";
                    player.sendMessage(temp);
                    player.sendMessage(ColorUtils.decolor(temp));
                    player.sendMessage(ColorUtils.translateColorCodes(temp));
                    return true;
                case "unalive":
//                    Profile.execute(player);
                    player.setKiller(player);
                    HealthComponent.execute(player);
//                    JSONProfileManager.getProfile(player.getUniqueId()).updatePlayerHearts(player);
                    return true;
                case "bleed":
                    DamageBuff bleed = new DamageBuff(Buffs.BLEED);
                    int baseDamage = (int) (Math.random()*100);
                    Utils.error("Current base DoT: " + baseDamage);
                    int[] dot = bleed.convertBaseHit(baseDamage);
                    bleed.createDamageTask(dot, player, true, player);

                    PlayerBuffManager.addBuffToPlayer(bleed, player);
//                    bleed.activate();
                    return true;
                case "stat":
                    StatBuff fortify = new StatBuff(Buffs.FORTIFY, player);
                    Utils.log("Applying fortify to " + player.getName());
                    PlayerBuffManager.addBuffToPlayer(fortify, player);
                    return true;
                case "tailwind":
                    StatBuff tailwind = new StatBuff(Buffs.TAILWIND, player);
                    player.sendMessage("Applying tailwind!");
                    PlayerBuffManager.addBuffToPlayer(tailwind, player);
                    return true;
                case "cripple":
                    StatBuff cripple = new StatBuff(Buffs.MAIM, player);
                    player.sendMessage("Applying cripple :(");
                    PlayerBuffManager.addBuffToPlayer(cripple, player);
                    return true;
                case "rejuv":
                    HealingBuff rejuv = new HealingBuff(Buffs.REJUVENATE);
                    Utils.msgPlayer(player, "Rejuvenating!");
                    int baseHealing = rejuv.getFinalHealingTick(JSONProfileManager.getProfile(player.getUniqueId()));
                    rejuv.createHealingTask(baseHealing, player, player);

                    PlayerBuffManager.addBuffToPlayer(rejuv, player);
                    return true;
                case "seek":
                    BowBasicAttacks.standardBowAttackBy(player, PlayerAbilities.BASIC_BOW_ATTACK, SteeringBehaviors.STRAIGHT_LINE, 4);
//                    BowBasicAttacks.standardBowAttackBy(player, SteeringBehaviors.ARRIVE, 10);
                    return true;
                case "circle":
                    Location loc = player.getLocation().clone().add(0,1.5,0);
                    Vector raytracePos = Projectile.getRaytracedMaxDistance(loc, loc.getDirection(), 10);
                    ParticlePlotter.spawnParticleAt(raytracePos, loc.getWorld(), Particle.GUST);
//                    Vector[] points = LinalgMath.plotPointsInsideNonAlignedCircle(raytracePos,loc.getDirection(), 6, 200);
                    Vector[] points = LinalgMath.plotPointsInsideNonAlignedCircle(raytracePos,loc.getDirection(), 6, 12);
//                    Vector[] points = LinalgMath.plotNonAlignedCircleBorder(raytracePos,loc.getDirection(), 6, 100);
                    for (Vector point : points){
                        ParticlePlotter.spawnParticleAt(point, loc.getWorld(), Particle.END_ROD);
                    }

                    Vector zAxis = loc.getDirection();
                    Vector xAxis = zAxis.clone().crossProduct(new Vector(0,1,0)).normalize();
                    Vector yAxis = xAxis.clone().crossProduct(zAxis);
                    OrientedBoundingBox spreadOBB = new OrientedBoundingBox(points, new Vector[]{xAxis,yAxis,zAxis});
//                    spreadOBB.expandDirectional(2, true, 3);
                    spreadOBB.expandFromCenter(3);
                    spreadOBB.render(playerWorld);
                    if (spreadOBB.intersects(player.getBoundingBox())){Utils.msgPlayer(player, "CollisioN!");}
                    return true;
                case "modGen":
                    InscriptionTable axeTable = new InscriptionTable("AXE");
                    axeTable.debug();

                    for (int i = 0; i< 5; i++){
                        final int ilvl = Utils.getRandomIntBetween(0,100);
                        Utils.log("Batch: " + i + " =========("+ilvl+")=========");
                        Set<InscriptionID> blockedPrefixes = new HashSet<>();
                        Inscription prefixA = axeTable.getRandomInscription(Affix.PREFIX, ilvl, blockedPrefixes);
                        Inscription prefixB = axeTable.getRandomInscription(Affix.PREFIX, ilvl, blockedPrefixes);

                        Set<InscriptionID> blockedSuffixes = new HashSet<>();
                        Inscription suffixA = axeTable.getRandomInscription(Affix.SUFFIX, ilvl, blockedSuffixes);
                        Inscription suffixB = axeTable.getRandomInscription(Affix.SUFFIX, ilvl, blockedSuffixes);

                        try {
                            Utils.log("prefix A: " + prefixA.getInscription() + " tier: " + prefixA.getTier());
                            Utils.log("prefix B: " + prefixB.getInscription() + " tier: " + prefixB.getTier());

                            Utils.log("suffix A: " + suffixA.getInscription() + " tier: " + suffixA.getTier());
                            Utils.log("suffix B: " + suffixB.getInscription() + " tier: " + suffixB.getTier());
                        } catch (NullPointerException ex){
                            Utils.log("Invalid insc. gen attempt");
                        }
                    }
                    return true;
                case "testColor":
                    ItemStack heldItem = player.getInventory().getItemInMainHand();
                    ItemInterfaceRenderer.setDisplayName("Awooga buga nuga",heldItem,ItemRarities.COMMON,false,4);
//                    ItemInterfaceRenderer.setDisplayName("Runeec Bunguschungus",heldItem,ItemRarities.AUGMENTED,false,4);
//                    ItemInterfaceRenderer.setDisplayName("Bingoos",heldItem,ItemRarities.RUNIC,false,7);
                    return true;
            }
        }

        return true;
    }
}
