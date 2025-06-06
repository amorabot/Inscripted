package com.amorabot.inscripted.player.profile.component;

import com.amorabot.inscripted.item.inscription.definition.Stats;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class Attributes implements ProfileComponent {

    private int strength;
    private int dexterity;
    private int intelligence;

    public Attributes(int str,int intel, int dex) {
        this.strength = str;
        this.dexterity = dex;
        this.intelligence = intel;
    }

    @Override
    public void updateComponent(UUID playerID, Map<Stats, double[]> finalStats) {
        setStrength(getSingleValueFrom(Stats.STRENGTH,finalStats));
        setDexterity(getSingleValueFrom(Stats.DEXTERITY,finalStats));
        setIntelligence(getSingleValueFrom(Stats.INTELLIGENCE,finalStats));
    }

    @Override
    public List<Component> asTextComponent() {
        return List.of();
    }
}
