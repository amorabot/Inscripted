package com.amorabot.inscripted.player.profile;

import com.amorabot.inscripted.player.profile.component.DamageComponent;
import com.amorabot.inscripted.player.profile.component.HealthComponent;

public class Profile {

    private final HealthComponent healthComponent;
    private final DamageComponent damageComponent;
    //Defence

    //Attributes
    //Misc


    public Profile(){
        this.healthComponent = new HealthComponent();
        this.damageComponent = new DamageComponent();
    }
}
