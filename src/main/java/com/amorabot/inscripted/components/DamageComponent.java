package com.amorabot.inscripted.components;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Player.Profile;

public class DamageComponent implements EntityComponent {

    private HitComponent hitData;

    private int lifeOnHit;
    private int lifeSteal;
    private int projectileDamage;
    private int areaDamage;
    private int increasedPhysicalDamage;
    private int increasedElementalDamage;
    private int increasedFireDamage;
    private int increasedLightningDamage;
    private int increasedColdDamage;
    private int increasedAbyssalDamage;

    //Added damage is added directly to hitData, doesnt need to be stored

    //List of special on-hit keystones

    public DamageComponent(){
        this.hitData = new HitComponent(null);
    }
    public void reset(Weapon weaponData){
        this.hitData = new HitComponent(weaponData);

        this.lifeOnHit = 0;
        this.lifeSteal = 0;
        this.areaDamage = 0;
        this.increasedPhysicalDamage = 0;
        this.increasedElementalDamage = 0;
        this.increasedFireDamage = 0;
        this.increasedLightningDamage = 0;
        this.increasedColdDamage = 0;
        this.increasedAbyssalDamage = 0;
    }

    public HitComponent getHitData() {
        return hitData;
    }


    @Override
    public void update(Profile profileData) {
    }

    public void applyDamageMods(){
        //Different protocol when considering areaDamage, proj DMG, ...
        hitData.applyPercentDamage(DamageTypes.PHYSICAL, increasedPhysicalDamage);
        hitData.applyPercentDamage(DamageTypes.FIRE, increasedFireDamage + increasedElementalDamage);
        hitData.applyPercentDamage(DamageTypes.LIGHTNING, increasedLightningDamage + increasedElementalDamage);
        hitData.applyPercentDamage(DamageTypes.COLD, increasedColdDamage + increasedElementalDamage);
        hitData.applyPercentDamage(DamageTypes.ABYSSAL, increasedAbyssalDamage);


    }

    public int getLifeOnHit() {
        return lifeOnHit;
    }

    public void setLifeOnHit(int lifeOnHit) {
        this.lifeOnHit = lifeOnHit;
    }

    public int getLifeSteal() {
        return lifeSteal;
    }

    public void setLifeSteal(int lifeSteal) {
        this.lifeSteal = lifeSteal;
    }

    public int getProjectileDamage() {
        return projectileDamage;
    }

    public void setProjectileDamage(int projectileDamage) {
        this.projectileDamage = projectileDamage;
    }

    public int getAreaDamage() {
        return areaDamage;
    }

    public void setAreaDamage(int areaDamage) {
        this.areaDamage = areaDamage;
    }

    public int getIncreasedPhysicalDamage() {
        return increasedPhysicalDamage;
    }

    public void setIncreasedPhysicalDamage(int increasedPhysicalDamage) {
        this.increasedPhysicalDamage = increasedPhysicalDamage;
    }

    public int getIncreasedElementalDamage() {
        return increasedElementalDamage;
    }

    public void setIncreasedElementalDamage(int increasedElementalDamage) {
        this.increasedElementalDamage = increasedElementalDamage;
    }

    public int getIncreasedFireDamage() {
        return increasedFireDamage;
    }

    public void setIncreasedFireDamage(int increasedFireDamage) {
        this.increasedFireDamage = increasedFireDamage;
    }

    public int getIncreasedLightningDamage() {
        return increasedLightningDamage;
    }

    public void setIncreasedLightningDamage(int increasedLightningDamage) {
        this.increasedLightningDamage = increasedLightningDamage;
    }

    public int getIncreasedColdDamage() {
        return increasedColdDamage;
    }

    public void setIncreasedColdDamage(int increasedColdDamage) {
        this.increasedColdDamage = increasedColdDamage;
    }

    public int getIncreasedAbyssalDamage() {
        return increasedAbyssalDamage;
    }

    public void setIncreasedAbyssalDamage(int increasedAbyssalDamage) {
        this.increasedAbyssalDamage = increasedAbyssalDamage;
    }
}
