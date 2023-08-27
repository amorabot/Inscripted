package com.amorabot.rpgelements.components.PlayerComponents;

import com.amorabot.rpgelements.components.Items.Interfaces.PlayerComponent;

public class Miscellaneous implements PlayerComponent {

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
