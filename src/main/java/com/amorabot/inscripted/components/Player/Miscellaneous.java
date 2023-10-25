package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;

public class Miscellaneous implements EntityComponent {

    //Unique modifier list

    private int extraStamina;
    private int percentStamina;
    private float extraStaminaRegen;
    private int percentStaminaRegen;
    private int extraProjectiles;
    private int projectileSpeed;
    private int extraArea;
    private int walkSpeed;
    //inc. Healing
    //thorns
    //damage per stat

    @Override
    public void update(Profile profileData) {
    }

    public void setExtraStamina(int extraStamina) {
        this.extraStamina = extraStamina;
    }
    public void setPercentStamina(int percentStamina) {
        this.percentStamina = percentStamina;
    }

    public int getStamina(){
        int baseStamina = 100;
        return (int) ((baseStamina+extraStamina) * (1+ (percentStamina/100F) ) );
    }

    public void setExtraStaminaRegen(float extraStaminaRegen) {
        this.extraStaminaRegen = extraStaminaRegen;
    }
    public void setPercentStaminaRegen(int percentStaminaRegen) {
        this.percentStaminaRegen = percentStaminaRegen;
    }

    public float getStaminaRegen(){
        int baseStaminaRegen = 5;
        return ((baseStaminaRegen+extraStaminaRegen) * (1+ (percentStaminaRegen/100F) ) );
    }

    public void setWalkSpeed(int walkSpeed) {
        this.walkSpeed = walkSpeed;
    }
    public int getWalkSpeed(){
        return walkSpeed;
    }
}
