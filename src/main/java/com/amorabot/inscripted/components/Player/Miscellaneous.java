package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Miscellaneous implements EntityComponent {

    //Unique modifier list

    private int stamina;
    private int percentStamina;
    private float staminaRegen;
    private int percentStaminaRegen;
    //Not implemented
    private int extraProjectiles;
    private int projectileSpeed;
    private int extraArea;
    private float walkSpeed;
    private int percentWalkSpeed;
    //inc. Healing
    //thorns
    //damage per stat

    public Miscellaneous(){
        reset();
    }

    public void reset(){
        walkSpeed = BaseStats.WALK_SPEED.getValue();
        percentWalkSpeed = 0;

        stamina = BaseStats.STAMINA.getValue();
        percentStamina = 0;

        staminaRegen = BaseStats.STAMINA_REGEN.getValue();
        percentStaminaRegen =0;

        extraProjectiles = 0;
        projectileSpeed = 0;
        extraArea = 0;
    }
    @Override
    public void update(UUID profileID) {
//        Profile profileData = JSONProfileManager.getProfile(profileID);

        setFinalStamina(getStamina(), getPercentStamina());
        setFinalStaminaRegen(getStaminaRegen(), getPercentStaminaRegen());
        setFinalWalkSpeed(getWalkSpeed(), getPercentWalkSpeed());

        Player player = Bukkit.getPlayer(profileID);
        assert player != null;
        /*
        The final walkSpeed stat reflects the % multiplier that is applied to the base player's movement speed
        Ex:  100 (Base) MS = 0.2  player speed
             169 (100 + 54) * 1.1 => 169% base MS,   1,69 multiplier overall to the base 0.2 MS => 0.3388
         */
        float mappedWS = ( walkSpeed ) * 0.002F;
        // input ->  min -1 |  max 1
        player.setWalkSpeed(mappedWS);
        //DEX can give 1% inc MS per 10
        //Default speed value for players: 0.2 (EMPIRIC FUCKING VALUE)  (https://minecraft.wiki/w/Attribute)
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
