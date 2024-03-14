package com.amorabot.inscripted.APIs;

import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.utils.CraftingUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SoundAPI {

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
            p.playSound(p.getLocation(), org.bukkit.Sound.ITEM_SHIELD_BREAK, baseVol, basePitch);
            return;
        }
        entity.playSound(net.kyori.adventure.sound.Sound.sound(
                Key.key("item.shield.break"),
                Sound.Source.AMBIENT,
                baseVol, basePitch));
    }
    public static void playArmorUnequipFor(Entity entity){
        float baseVol = 1.2f;
        float basePitch = 0.5f;

        if (entity instanceof Player){
            Player p = (Player) entity;
            p.playSound(p.getLocation(), org.bukkit.Sound.ITEM_ARMOR_EQUIP_NETHERITE, baseVol, basePitch);
            return;
        }
        entity.playSound(Sound.sound(
                Key.key("item.armor.equip_netherite"),
                Sound.Source.AMBIENT,
                baseVol, basePitch));
    }
    public static void playArmorEquipFor(Entity entity){
        float baseVol = 0.5f;
        float basePitch = 1.3f;

        if (entity instanceof Player){
            Player p = (Player) entity;
            p.playSound(p.getLocation(), org.bukkit.Sound.ITEM_ARMOR_EQUIP_IRON, baseVol, basePitch);
            return;
        }
        playGenericSoundAtLocation(entity, entity.getLocation(), "item.armor.equip_iron", baseVol, basePitch);
    }

    public static void playGenericSoundAtLocation(Audience audience, Location loc, String soundName, float baseVolume, float basePitch){
        Sound sound = Sound.sound(
                Key.key(soundName),
                Sound.Source.AMBIENT,
                baseVolume, basePitch);

        audience.playSound(sound, loc.x(), loc.y(), loc.z());
    }

    public static void playEnchantingSoundFor(Audience audience, Location loc, boolean success){
        if (success){
            playGenericSoundAtLocation(audience, loc, "block.enchantment_table.use", 0.5f, 0.7f);
        } else {
            playGenericSoundAtLocation(audience, loc, "block.enchantment_table.use", 0.6f, 1.3f);
        }
    }
    public static void playInvalidActionFor(Audience audience, Location loc){
        playGenericSoundAtLocation(audience, loc, "block.note_block.basedrum", 0.5f, 1.0f);
    }
    public static void playAttackSoundFor(Audience audience, Location loc, WeaponTypes weaponType){
        switch (weaponType){
            case AXE -> playGenericSoundAtLocation(audience, loc, "entity.player.attack.strong", 0.5f, 0.7f);
            case SWORD -> playGenericSoundAtLocation(audience, loc, "entity.player.attack.sweep", 0.8f, 1f);
            case BOW -> playGenericSoundAtLocation(audience, loc, "item.crossbow.shoot", 0.8f, 1f);
            case DAGGER -> playGenericSoundAtLocation(audience, loc, "entity.player.attack.sweep", 0.6f, 1.9f);
            case WAND -> playGenericSoundAtLocation(audience, loc, "entity.drowned.shoot", 0.8f, 1f);
            case SCEPTRE -> {
                playGenericSoundAtLocation(audience, loc, "block.gravel.break", 0.7f, 0.6f);
//                playGenericSoundAtLocation(audience, loc, "block.basalt.break", 0.8f, 0.5f);
//                playGenericSoundAtLocation(audience, loc, "entity.zombie.break_wooden_door", 0.1f, 0.2f);
            }
        }
    }
    public static void playDeathSoundFor(Audience audience, Location loc){
        playGenericSoundAtLocation(audience, loc, "entity.player.death", 1f, 0.6f);
        playGenericSoundAtLocation(audience, loc, "entity.wither.hurt", 0.4f, 0.6f);
    }
}
