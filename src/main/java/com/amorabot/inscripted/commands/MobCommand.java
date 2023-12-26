package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.APIs.MedicalCareAPI;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Attack;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Mobs.DefensePresets;
import com.amorabot.inscripted.components.Mobs.MobStats;
import com.amorabot.inscripted.components.Mobs.MobStatsContainer;
import com.amorabot.inscripted.utils.ColorUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MobCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player)){
            return false;
        }
        Player player = (Player) commandSender;
        NamespacedKey mobKey = new NamespacedKey(Inscripted.getPlugin(), "INSCRIPTED_MOB");
        Location locationToSpawn = player.getLocation();

        if (strings.length == 1){ //spawn a specific mob, lets map it

            switch (strings[0]){
                case "Dummy":
                    Skeleton skeleton = locationToSpawn.getWorld().spawn(player.getLocation().clone(), Skeleton.class);
                    skeleton.setAI(false);
                    EntityEquipment equipment = skeleton.getEquipment();
//                    skeleton.getRemoveWhenFarAway()
//                    skeleton.launchProjectile()
//                    skeleton.getI

                    //Make templates for mods (mobs.yml)
                    ItemStack helmet = new ItemStack(Material.TARGET);
                    ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                    ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
                    ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
                    LeatherArmorMeta armorMeta = (LeatherArmorMeta) chestplate.getItemMeta();
                    assert armorMeta != null;
                    armorMeta.setColor(Color.fromRGB(200, 50, 50));
                    chestplate.setItemMeta(armorMeta);
                    leggings.setItemMeta(armorMeta);
                    boots.setItemMeta(armorMeta);

                    assert equipment != null;
                    ItemStack[] armorSet = new ItemStack[]{boots, leggings, chestplate, helmet};
                    equipment.setArmorContents(armorSet);
                    equipment.setItemInMainHand(null);

                    skeleton.setMaximumNoDamageTicks(5);
                    skeleton.setHealth(1);
                    MobStats mobStats = new MobStats(
                            155,
                            new Attack(
                                    new int[]{100,120}, new int[]{20,60}, new int[2], new int[2], new int[2],
                                    10F, 0, 0, 0, 0),
                            new HealthComponent(1000, 150, 0, 0, 10),
                            DefensePresets.PHYSICAL_RESISTANT);

                    skeleton.getPersistentDataContainer().set(mobKey, new MobStatsContainer(), mobStats);

                    String displayName = ColorUtils.translateColorCodes("&c&lTest Dummy" + " &7[ lv." + mobStats.getMobLevel() + "]");

                    skeleton.setCustomName(displayName);
                    skeleton.setCustomNameVisible(true);
                    return true;
                case "TestMob":
                    ZombieVillager zombieVillager = locationToSpawn.getWorld().spawn(player.getLocation().clone(), ZombieVillager.class);
                    MedicalCareAPI.showMobGoals(player, zombieVillager);
                    String goal1 = "minecraft:move_through_village";
                    String goal2 = "minecraft:zombie_attack_turtle_egg";
                    MedicalCareAPI.removeMobGoals(zombieVillager, List.of(goal1, goal2));
                    MedicalCareAPI.showMobGoals(player, zombieVillager);

                    zombieVillager.setMaximumNoDamageTicks(10);
                    zombieVillager.setHealth(1);
                    MobStats zombieStats = new MobStats(
                            30,
                            new Attack(
                                    new int[]{100,120}, new int[]{10,20}, new int[2], new int[2], new int[]{70,70},
                                    10F, 0, 0, 0, 0),
                            new HealthComponent(1000, 150, 0, 0, 10),
                            DefensePresets.BASIC_EVASIVE);

                    zombieVillager.getPersistentDataContainer().set(mobKey, new MobStatsContainer(), zombieStats);
                    String zombieName = ColorUtils.translateColorCodes( "&e" + DamageTypes.LIGHTNING.getCharacter() + " "
                            + "&c" + DefenceTypes.ARMOR.getSpecialChar() + "&e" + DefenceTypes.ARMOR.getSpecialChar() + "&b" + DefenceTypes.ARMOR.getSpecialChar() +
                            " &6&lMeno" + " &7[Lv." + zombieStats.getMobLevel() + "]");

                    zombieVillager.setCustomName(zombieName);
                    zombieVillager.setCustomNameVisible(true);
                    break;
                case "Nengue":
                    Enderman nengue = locationToSpawn.getWorld().spawn(player.getLocation().clone(), Enderman.class);
                    MedicalCareAPI.showMobGoals(player,nengue);

                    nengue.setMaximumNoDamageTicks(5);
                    nengue.setHealth(1);

                    MobStats nengueStats = new MobStats(
                            999,
                            new Attack(
                                    new int[]{100,120}, new int[2], new int[2], new int[2], new int[]{200,250},
                                    10F, 0, 0, 0, 0),
                            new HealthComponent(5000, 20, 0, 0, 10),
                            DefensePresets.ABYSS_RESISTANT);

                    nengue.getPersistentDataContainer().set(mobKey, new MobStatsContainer(), nengueStats);

                    String nenguename = ColorUtils.translateColorCodes("&d&lNengue" + " &7[Lv." + nengueStats.getMobLevel() + "]");

                    nengue.setCustomName(nenguename);
                    nengue.setCustomNameVisible(true);
                    break;
            }

        }


        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        List<String> mobList = new ArrayList<>();
        if (strings.length == 1){
            //Ideally comes from a Enum
            mobList.add("Dummy");
            mobList.add("TestMob");
            mobList.add("Nengue");

            return mobList;
        } else if (strings.length == 2) { //%Life modifiers, for example
            //Ideally comes from a Enum
            mobList.add("None");
            mobList.add("Double");

            return mobList;
        }
        return null;
    }
}
