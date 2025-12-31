package com.amorabot.inscripted.skill.routine.slash;

import lombok.Getter;

@Getter
public enum SlashPresets {
    STANDARD_AXE(new SlashConfig(SlashSegment::standardAxe,
            24,120,2.2,-0.3, 0.3,
            0.4,1.4, new int[]{176, 126, 111}, null,0.85F, 0.45
    )),
    STANDARD_SWORD(new SlashConfig(SlashSegment::standardSword,
            20,100,2,-0.2, 0.1,
            0.3,1.2, new int[]{173, 143, 130}, null,0.7F, 0.2
    )),
    STANDARD_DAGGER(new SlashConfig(SlashSegment::standardSword,
            12,90,1.6,0, 0,
            1.1,1.1, new int[]{140,140,140}, null, 0.5F, 0.1
    )),
    STANDARD_SLAM_SWING(new SlashConfig(SlashSegment::standardMaceSwing,
            12,70,1.7,0,0.25,0.2,0.5,
            new int[]{220,160,190}, new double[]{1.1,1},0.7F, 0.25)),
    LACERATE(new SlashConfig(SlashSegment::bloody,
            16,96,2.2,0.1, -0.4,
            0.4,1.2, new int[]{128, 20, 24}, null, 0.8F, 0.1
    ));

    private final SlashConfig slashConfigData;

    SlashPresets(SlashConfig config){
        this.slashConfigData = config;
    }
}
