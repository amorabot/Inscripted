package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;

public class Miscellaneous implements EntityComponent {

    //Unique modifier list

    private int stamina; //100 base
    private int percentStamina;
    private float staminaRegen; //5 base
    private int percentStaminaRegen;
    //Not implemented
    private int extraProjectiles;
    private int projectileSpeed;
    private int extraArea;
    private float walkSpeed; //100 base
    private int percentWalkSpeed;
    //inc. Healing
    //thorns
    //damage per stat

    public Miscellaneous(){
        reset();
    }

    public void reset(){
        walkSpeed = 100;
        percentWalkSpeed = 0;

        stamina = 100;
        percentStamina = 0;

        staminaRegen = 5;
        percentStaminaRegen =0;

        extraProjectiles = 0;
        projectileSpeed = 0;
        extraArea = 0;
    }
    @Override
    public void update(Profile profileData) {
        setFinalStamina(getStamina(), getPercentStamina());
        setFinalStaminaRegen(getStaminaRegen(), getPercentStaminaRegen());
        setFinalWalkSpeed(getWalkSpeed(), getPercentWalkSpeed());
    }

    public int getStamina(){
        return stamina;
    }
    public void addBaseStamina(int stamina){
        this.stamina += stamina;
    }


    public int getPercentStamina() {
        return percentStamina;
    }
    public void addBasePercentStamina(int perentStamina){
        this.percentStamina += perentStamina;
    }
    public void setFinalStamina(int baseStamina, int increasedStamina){
        this.stamina = (int) ((baseStamina) * (1+ (increasedStamina/100F) ) );
    }


    public float getStaminaRegen(){
        return staminaRegen;
    }
    public void addBaseStaminaRegen(int staminaRegen){
        this.staminaRegen += staminaRegen;
    }


    public int getPercentStaminaRegen() {
        return percentStaminaRegen;
    }
    public void addBasePercentStaminaRegen(int percentStaminaRegen){
        this.percentStaminaRegen += percentStaminaRegen;
    }
    public void setFinalStaminaRegen(float staminaRegen, int percentStaminaRegen){
        this.staminaRegen = (staminaRegen * (1 + (percentStaminaRegen/100F) ) );
    }

    public float getWalkSpeed(){
        return walkSpeed;
    }
    public void addBaseWalkSpeed(int walkSpeed){
        this.walkSpeed += walkSpeed;
    }
    public int getPercentWalkSpeed() {
        return percentWalkSpeed;
    }
    public void addBasePercentWalkSpeed(int WSPercent){
        this.percentWalkSpeed += WSPercent;
    }
    public void setFinalWalkSpeed(float baseWalkSpeed, int percentWalkSpeed){
        this.walkSpeed = (baseWalkSpeed * (1 + (percentWalkSpeed/100F) ) );
    }
}
