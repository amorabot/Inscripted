package com.amorabot.inscripted.skills.casting;

import com.amorabot.inscripted.skills.PlayerAbilities;
import lombok.Getter;

public class CasterState {

    private boolean disabledCasting;

    private boolean alternateCasting;
    @Getter
    private PlayerAbilities lastAbility;

    public CasterState(){
        this.disabledCasting = false;

        this.alternateCasting = false;
        this.lastAbility = null;
    }

    public void toggleDisabledCasting(){
        this.disabledCasting = !disabledCasting;
    }

    public void toggleAlternateCasting(){
        this.alternateCasting = !alternateCasting;
    }
    public void resetCastingState(){
        this.alternateCasting = false;
    }

    public void setLastestAbility(PlayerAbilities ability){
        this.lastAbility = ability;
    }
    public boolean hasCasted(){
        return lastAbility != null;
    }

}
