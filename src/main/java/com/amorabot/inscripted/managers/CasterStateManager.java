package com.amorabot.inscripted.managers;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.events.ItemUsage;
import com.amorabot.inscripted.skill.casting.CasterState;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CasterStateManager {

    private static final boolean DEBUG_MODE = true;
    private static final Map<UUID, CasterState> playerCasterStateMap = new HashMap<>();

    public static void alternateSpellcastingTriggerFor(Player caster, ItemUsage interaction){
        CasterState state = getCastingStateFor(caster);
        if (state.isDisabledCasting()){
            if (DEBUG_MODE) Utils.error("Disable casting!");
            return;
        }
        boolean alreadyAlternateCasting = state.isAlternateCasting();
        switch (interaction){
            case NONE -> {
                if (alreadyAlternateCasting){
                    if (DEBUG_MODE) caster.sendMessage(Component.text("Goblin mode activated").color(NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD));
                    state.reset();
                    return;
                }
            }
            case WEAPON_LEFT_CLICK_AIR, WEAPON_LEFT_CLICK_BLOCK -> {
                if (alreadyAlternateCasting){
                    if (DEBUG_MODE) caster.sendMessage(Component.text("Special attack Cast").color(NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD));
                    state.reset();
                    return;
                }
            }
            case WEAPON_RIGHT_CLICK_AIR, WEAPON_RIGHT_CLICK_BLOCK -> {
                if (alreadyAlternateCasting){
                    if (DEBUG_MODE) caster.sendMessage(Component.text("Utility Cast").color(NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD));
                    state.reset();
                    return;
                }
            }
        }
        // If not alternate-casting, activate that stat
        playToggleSound(caster,0.7f);
        if (DEBUG_MODE) caster.sendMessage(Component.text("Spellcasting mode").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
        state.activateAlternateCasting(caster);
    }
    public static CasterState getCastingStateFor(Player caster){
        UUID playerID = caster.getUniqueId();
        return playerCasterStateMap.computeIfAbsent(playerID, uuid -> new CasterState());
    }
    public static void playToggleSound(Player caster, float pitch){
        SoundAPI.playGenericSoundAtLocation(Audience.audience(caster),caster.getLocation(),"entity.experience_orb.pickup", 0.5f,pitch);
    }
}
