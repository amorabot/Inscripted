package com.amorabot.rpgelements.components.Player;

import com.amorabot.rpgelements.components.Items.Interfaces.EntityComponent;

public class Miscellaneous implements EntityComponent {

    //Unique modifier list

    private int walkSpeed;
    //area
    //projectiles
    //proj speed
    //inc. Healing
    //thorns
    //damage per stat

    @Override
    public void update(Profile profileData) {
        Attributes playerAttributes = profileData.getAttributes();
        int walkSpeedSum = 0;

        walkSpeedSum += playerAttributes.getDexterity()/20;

        this.walkSpeed = walkSpeedSum;
    }
}
