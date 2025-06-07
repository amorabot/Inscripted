package com.amorabot.inscripted.player.profile.component;

import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.player.profile.BaseStats;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class Miscellaneous implements ProfileComponent {

    private int walkSpeed;
    private int stamina;
    private double staminaRegen;

    public Miscellaneous(){
        this.walkSpeed = BaseStats.WALK_SPEED.getValue();
        this.stamina = BaseStats.STAMINA.getValue();
        this.staminaRegen = BaseStats.STAMINA_REGEN.getValue();
    }


    @Override
    public void updateComponent(UUID playerID, Map<Stats, double[]> finalStats) {
        //Base stats should always be present
        setWalkSpeed((int) finalStats.get(Stats.WALK_SPEED)[0]);
        setStamina((int) finalStats.get(Stats.STAMINA)[0]);
        setStaminaRegen(finalStats.get(Stats.STAMINA_REGEN)[0]);

        //Update actual player speed
        Player player = Bukkit.getPlayer(playerID);
        final float baseSpeed = 0.2F;
        if (player!=null && player.isOnline()){
            float newSpeed = baseSpeed * (0.01F * getWalkSpeed());
            player.setWalkSpeed(newSpeed);
        }
    }

    @Override
    public List<Component> asTextComponent() {
        return List.of();
    }
    /*
    The final walkSpeed stat reflects the % multiplier that is applied to the base player's movement speed
    Ex:  100 (Base) MS = 0.2  player speed
         169 (100 + 54) * 1.1 => 169% base MS,   1,69 multiplier overall to the base 0.2 MS => 0.3388

    Input ->  min -1 | max 1
    Default speed value for players: 0.2 (EMPIRIC FUCKING VALUE)  (https://minecraft.wiki/w/Attribute)
     */
}
