package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
import com.amorabot.inscripted.components.Items.ItemBuilder;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.components.Items.modifiers.data.Meta;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
import com.amorabot.inscripted.utils.ColorUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
                case "item":
                    Weapon weapon = new Weapon(20, WeaponTypes.SWORD, ItemRarities.RARE,true, false);
                    String impA = weapon.getImplicit().getImplicitDisplayName(Objects.requireNonNull(Archetypes.mapArchetypeFor(weapon.getSubtype())),2);
                    Weapon item = (Weapon) ItemBuilder.randomItem(ItemTypes.WEAPON, WeaponTypes.MACE, 70, ItemRarities.MAGIC, true, false);
                    String impB = item.getImplicit().getImplicitDisplayName(Objects.requireNonNull(Archetypes.mapArchetypeFor(item.getSubtype())),2);
                    player.sendMessage(ColorUtils.translateColorCodes("1: " + impA + " 2: " + impB));
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
                    Profile.execute(player);
                    return true;
            }
        }

        return true;
    }
}
