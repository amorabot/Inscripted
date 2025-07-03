package com.amorabot.inscripted.item.inscription.definition;

import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;

@Getter
public enum EffectIDs {
    THRILL_OF_THE_HUNT(TriggerTypes.ON_HIT, TriggerTimes.LATE, 10),
    OVERDRIVE(TriggerTypes.WHEN_HIT, TriggerTimes.LATE, 0),
    ADRENALINE_RUSH(TriggerTypes.ON_DEATH, TriggerTimes.EARLY, 120),
    GRACEFUL_LANDING(TriggerTypes.ON_MOVEMENT, TriggerTimes.LATE, 60),
    SADISM(TriggerTypes.ON_BLEED, TriggerTimes.LATE, 0),
    COUP_DE_GRACE(TriggerTypes.ON_HIT, TriggerTimes.EARLY, 0),
    OPPORTUNIST(TriggerTypes.ON_CRIT, TriggerTimes.EARLY, 0);


    private final TriggerTypes trigger;
    private final TriggerTimes triggerTime;
    private final int cooldowInSeconds;

    EffectIDs(TriggerTypes trigger,TriggerTimes triggerTime,int cdInSeconds){
        this.trigger = trigger;
        this.triggerTime = triggerTime;
        this.cooldowInSeconds = cdInSeconds;
//        Utils.log(this.name());
    }

    public String getInfo(){
        String triggerDN = getTrigger().getDisplayName();
        if (getCooldowInSeconds() == 0){
            return triggerDN;
        }
        return (triggerDN + " - ⏳" + getCooldowInSeconds() + "s");
    }
}
