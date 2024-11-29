package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Items.modifiers.unique.Keystones;
import com.amorabot.inscripted.components.Player.stats.StatCompiler;
import com.amorabot.inscripted.components.Player.stats.StatPool;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static com.amorabot.inscripted.utils.Utils.calculateFinalFlatValue;

@Setter
@Getter
public class StatsComponent implements EntityComponent {
    /*
    https://www.poewiki.net/wiki/Stat
    https://www.poewiki.net/wiki/Damage_conversion#Conversion_order
    */

    @Setter
    private StatPool playerStats = new StatPool();
    private StatPool externalStats = new StatPool();

    private Set<Keystones> activeStatKeystones = new HashSet<>();

    public StatsComponent(){

    }

    @Override
    public void update(UUID profileID) {
        Player player = Bukkit.getPlayer(profileID);
        assert player != null;

        StatPool updatedStats = getMergedStatsSnapshot();
        /*
        The final walkSpeed stat reflects the % multiplier that is applied to the base player's movement speed
        Ex:  100 (Base) MS = 0.2  player speed
             169 (100 + 54) * 1.1 => 169% base MS,   1,69 multiplier overall to the base 0.2 MS => 0.3388

        Input ->  min -1 | max 1
        Default speed value for players: 0.2 (EMPIRIC FUCKING VALUE)  (https://minecraft.wiki/w/Attribute)
         */
        float finalWalkSpeed = updatedStats.getFinalValueFor(PlayerStats.WALK_SPEED, true);
        setPlayerAttributes(player, finalWalkSpeed);

    }
    private void setPlayerAttributes(Player player, float... attributes){
        setPlayerSpeed(player, attributes[0]);
        //Size
        //Range
        //...
    }
    private void setPlayerSpeed(Player player, float speedValue){
        Utils.log("Final WS: " + speedValue);
        float mappedWS = Math.max(-1, ( speedValue ) * 0.002F);
        player.setWalkSpeed(mappedWS);
    }

    //Combined, playerStats and externalStats represent the statPool that components should sample to
    public StatPool getMergedStatsSnapshot(){
        StatPool clonedPlayerStats = new StatPool(new HashMap<>(getPlayerStats().getStats()));
        clonedPlayerStats.merge(getExternalStats());
        return clonedPlayerStats;
    }

    //Changes to stat keystones will trigger a recompilation
    public void addActiveStatKeystone(UUID playerID, Keystones keystone){
        activeStatKeystones.add(keystone);
        StatCompiler.updateProfile(playerID);
    }
    public void removeActiveStatKeystone(UUID playerID, Keystones keystone, boolean supressCompiling){ //TODO: make boolean optional [boolean...]
        activeStatKeystones.remove(keystone);
        if (!supressCompiling){
            StatCompiler.updateProfile(playerID);
        }
    }
    public boolean isStatKeystonePresent(Keystones keystone){
        return activeStatKeystones.contains(keystone);
    }

    public void debug(){
        playerStats.debug("Regular Stats");
        externalStats.debug("External Stats");
    }
}
