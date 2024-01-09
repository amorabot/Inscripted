package com.amorabot.inscripted.APIs;

import com.amorabot.inscripted.utils.CraftingUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SoundAPI {

    /*
    item.axe.scrape -> sound for whetstones
     */

    public static void playDodgeFor(Audience audience, Location dodgeLocation){
        playGenericSoundAtLocation(audience,
                dodgeLocation,
                "entity.bat.takeoff",
                0.4f, 0.3f + CraftingUtils.getRandomNumber(-3,3) * 0.1f);
    }

    public static void playBreakSoundFor(Entity entity){
        float baseVol = 0.5f;
        float basePitch = 1.7f;

        if (entity instanceof Player){
            Player p = (Player) entity;
            p.playSound(p.getLocation(), Sound.ITEM_SHIELD_BREAK, baseVol, basePitch);
            return;
        }
        entity.playSound(net.kyori.adventure.sound.Sound.sound(
                Key.key("item.shield.break"),
                net.kyori.adventure.sound.Sound.Source.AMBIENT,
                baseVol, basePitch));
    }
    public static void playArmorUnequipFor(Entity entity){
        float baseVol = 1.2f;
        float basePitch = 0.5f;

        if (entity instanceof Player){
            Player p = (Player) entity;
            p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE, baseVol, basePitch);
            return;
        }
        entity.playSound(net.kyori.adventure.sound.Sound.sound(
                Key.key("item.armor.equip_netherite"),
                net.kyori.adventure.sound.Sound.Source.AMBIENT,
                baseVol, basePitch));
    }
    public static void playArmorEquipFor(Entity entity){
        float baseVol = 0.5f;
        float basePitch = 1.3f;

        if (entity instanceof Player){
            Player p = (Player) entity;
            p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, baseVol, basePitch);
            return;
        }
        entity.playSound(net.kyori.adventure.sound.Sound.sound(
                Key.key("item.armor.equip_iron"),
                net.kyori.adventure.sound.Sound.Source.AMBIENT,
                baseVol, basePitch));
    }

    private static void playGenericSoundAtLocation(Audience audience, Location loc, String soundName, float baseVolume, float basePitch){
        net.kyori.adventure.sound.Sound sound = net.kyori.adventure.sound.Sound.sound(
                Key.key(soundName),
                net.kyori.adventure.sound.Sound.Source.AMBIENT,
                baseVolume, basePitch);

        audience.playSound(sound, loc.x(), loc.y(), loc.z());
    }
}
