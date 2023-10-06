package com.amorabot.inscripted.components.Mobs;

import com.amorabot.inscripted.components.DefenceComponent;

public enum DefensePresets {

    //cold -> fire -> light -> cold ->...
    BASIC_PHYSICAL_RESISTANT(10,10,10, -30, 0, 1500, 150),
    BASIC_EVASIVE(10,10,10,-30, 2000, 0 ,0),
    FIRE_RESISTANT(70, -10, 10, -30, 0, 300, 50),
    LIGHTNING_RESISTANT(-10, 10, 70, -30 , 0, 150, 25),
    COLD_RESISTANT(10, 70, -10, -30, 0, 450, 70),
    ABYSS_RESISTANT(20, 20 , 20, 50, 0, 500, 50);


    final DefenceComponent defenceComponent;

    DefensePresets(int fireRes, int coldRes, int lightRes, int abyssRes, int dodge, int baseArmor, int incArmor){
        this.defenceComponent = new DefenceComponent(fireRes, coldRes, lightRes, abyssRes, dodge, baseArmor, incArmor);
    }

    public DefenceComponent getDefense(){
        return defenceComponent;
    }

}
