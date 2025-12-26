package com.amorabot.inscripted.skill.casting;

import lombok.Getter;

@Getter
public enum CastType {
    NEUTRAL("NONE"),
    BASIC_ATTACK("Left MB"), MOVEMENT("Right MB"), UTILITY("F+RMB"), SPECIAL_ATTACK("F+LMB");

    /*
    Examples:
    Item cast: NEUTRAL, ITEM
    Player "Fist": NEUTRAL, PLAYER
    Player Skill: MOVEMENT, PLAYER
    */
    final String command;
    CastType(String cmd){
        this.command = cmd;
    }
}
