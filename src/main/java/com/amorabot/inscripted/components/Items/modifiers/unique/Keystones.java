package com.amorabot.inscripted.components.Items.modifiers.unique;


import com.amorabot.inscripted.components.Attack;
import com.amorabot.inscripted.components.DamageComponent;
import com.amorabot.inscripted.components.Player.Profile;
import lombok.Getter;

@Getter
public enum Keystones { //Add "early" boolean? -> Checking for early/late effect on profile

    FORBIDDEN_PACT(TriggerTimes.LATE,"The abyssal gaze","has changed you."," ",
            "Become immune to","☽ Abyssal DMG",
            "Your maximum ","❤ Health is 1") {
        @Override
        public void apply(Profile playerProfile) {
            playerProfile.getHealthComponent().setMaxHealth(1);
        }
    }, //Chaos inoc
    LETHAL_STRIKES(TriggerTimes.LATE,"You aim for the","weak spots."," ",
            "All Shred is con-","verted to Bleed","chance.") {
        @Override
        public void apply(Profile playerProfile) {
            DamageComponent damageComponent = playerProfile.getDamageComponent();
            Attack playerAttackData = damageComponent.getHitData();
            int shred = playerAttackData.getShred();
            playerAttackData.setShred(0);
            playerAttackData.setBleedChance(playerAttackData.getBleedChance()+shred);
        }
    }; //All shred is converted to Bleed Chance

    private final TriggerTimes compilationTime;
    private final String[] description;

    Keystones(TriggerTimes compilationTime, String... description){
        this.compilationTime = compilationTime;
        this.description = description;
    }

    public abstract void apply(Profile playerProfile);
}
