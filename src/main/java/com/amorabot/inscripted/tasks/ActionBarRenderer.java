package com.amorabot.inscripted.tasks;

import com.amorabot.inscripted.item.render.InscriptedPalette;
import com.amorabot.inscripted.item.structure.Armor.DefenceTypes;
import com.amorabot.inscripted.item.structure.Weapon.DamageTypes;
import com.amorabot.inscripted.managers.CasterStateManager;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.Profile;
import com.amorabot.inscripted.player.profile.component.HealthComponent;
import com.amorabot.inscripted.skill.casting.CastType;
import com.amorabot.inscripted.skill.casting.CasterState;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class ActionBarRenderer extends BukkitRunnable {

    private static final ActionBarRenderer INSTANCE = new ActionBarRenderer();
    private ActionBarRenderer(){
    }
    @Override
    public void run() {
        for (Player currentPlayer : Bukkit.getOnlinePlayers()){
            UUID id = currentPlayer.getUniqueId();
            Profile playerProfile = PlayerDataContainer.getProfile(id);
            HealthComponent healthComponent = playerProfile.getHealthComponent();
            Component cdComponent = getCooldownComponent(currentPlayer);

            final int currentHP = healthComponent.getHealth();
            final int totalHP = healthComponent.getMaxHealth();
            Component hpSection = Component.text(DefenceTypes.HEALTH.getSpecialChar()+" "+ currentHP + "/" + totalHP).color(InscriptedPalette.HEALTH.getColor());
            Component soulSection = null;

            if (healthComponent.getSoul()>0){
                soulSection = Component.text("  |  ").color(TextColor.color(120,120,120));
                final int currentSoul = healthComponent.getSoul();
                final int totalSoul = healthComponent.getMaxSoul();
                soulSection = soulSection.append(Component.text(DefenceTypes.SOUL.getSpecialChar()+" "+ currentSoul + "/" + totalSoul).color(InscriptedPalette.SOUL.getColor()));
            }
            if (soulSection==null){
                Component renderedHealth = hpSection.decoration(TextDecoration.ITALIC,false);
                currentPlayer.sendActionBar(renderedHealth.append(cdComponent));
                continue;
            }

            Component renderedHealth = hpSection.append(soulSection).decoration(TextDecoration.ITALIC,false);

            currentPlayer.sendActionBar(renderedHealth.append(cdComponent));


//            Long remainingMovementCD = GlobalCooldownManager.fetchAbilityRemainingCooldown(currentPlayer.getUniqueId(), AbilityTypes.MOVEMENT);
//            if (remainingMovementCD > 0){
//                if (remainingMovementCD<4000){
//                    cooldownSection += "&8"+ remainingMovementCD/1000 +"⏳M ";
//                } else {
//                    cooldownSection += "&8⏳M ";
//                }
//            } else {
//                cooldownSection += "&a⏳M ";
//            }
////            cooldownSection += " &7\uD83E\uDDEA12";
//            TextComponent cooldownCoomponent = LegacyComponentSerializer.legacyAmpersand().deserialize(cooldownSection);
    }


    }

    public static ActionBarRenderer getInstance() {
        return INSTANCE;
    }

    private Component getCooldownComponent(Player player){
        PlayerDataContainer playerData = PlayerDataContainer.getDataContainerFor(player.getUniqueId());
        CasterState casterState = CasterStateManager.getCastingStateFor(player);
        Component div = Component.text(" | ").decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false).color(InscriptedPalette.NEUTRAL_GRAY.getColor());
        Component space = Component.text(" ");
        InscriptedPalette castingColor;
        if (casterState.isAlternateCasting()){
            castingColor = InscriptedPalette.SORCERER;
        } else {
            castingColor = InscriptedPalette.DARK_GRAY;
        }
        String movColor = getSkillIconColor(CastType.MOVEMENT,playerData);
        String utilityColor = getSkillIconColor(CastType.UTILITY,playerData);
        String specialColor = getSkillIconColor(CastType.SPECIAL_ATTACK,playerData);
        Component movIcon = Component.text("\uE032").decoration(TextDecoration.ITALIC,false).color(TextColor.fromHexString(movColor));
        Component utilityIcon = Component.text("\uE031").decoration(TextDecoration.ITALIC,false).color(TextColor.fromHexString(utilityColor));
        Component specialIcon = Component.text("\uE030").decoration(TextDecoration.ITALIC,false).color(TextColor.fromHexString(specialColor));

        Component altCasting = Component.text("").append(div).append(Component.text(DamageTypes.FIRE.getCharacter()).color(castingColor.getColor())).append(div);
        return altCasting.append(movIcon).append(space).append(utilityIcon).append(space).append(specialIcon).append(div);
    }
    private String getSkillIconColor(CastType type, PlayerDataContainer playerData){
        if (playerData.fetchAbilityRemainingCooldown(type) == 0){
            return NamedTextColor.GREEN.asHexString();
        }
        return InscriptedPalette.NEUTRAL_GRAY.getColorString();
    }
}
