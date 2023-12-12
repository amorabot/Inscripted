package com.amorabot.inscripted.components;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.utils.Utils;

public class DamageComponent implements EntityComponent {

    private Attack hitData;

    private int increasedAccuracy;
    private int increasedCritChance;
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
        this.hitData = new Attack(null);
    }
    @Override
    public void reset(){
        Utils.error("Refrain from using no-arg DamageComponent reset() method, use reset(Weapon weaponData)");
    }

    public void reset(Weapon weaponData){
        this.hitData = new Attack(weaponData);

        this.increasedCritChance = 0;
        this.increasedAccuracy = 0;
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

    public Attack getHitData() {
        return hitData;
    }


    @Override
    public void update(Profile profileData) {
        //Different protocol when considering areaDamage, proj DMG, ...
        hitData.applyPercentDamage(DamageTypes.PHYSICAL, increasedPhysicalDamage);
        hitData.applyPercentDamage(DamageTypes.FIRE, increasedFireDamage + increasedElementalDamage);
        hitData.applyPercentDamage(DamageTypes.LIGHTNING, increasedLightningDamage + increasedElementalDamage);
        hitData.applyPercentDamage(DamageTypes.COLD, increasedColdDamage + increasedElementalDamage);
        hitData.applyPercentDamage(DamageTypes.ABYSSAL, increasedAbyssalDamage);

        hitData.setFinalAccuracy(increasedAccuracy);
        hitData.setFinalCritChance(increasedCritChance);
    }

    public int getIncreasedAccuracy() {
        return increasedAccuracy;
    }
    public void addBaseIncreasedAccuracy(int increasedAccuracy) {
        this.increasedAccuracy += increasedAccuracy;
    }
    public void addBaseIncreasedCritChance(int increasedCritChance){
        this.increasedCritChance += increasedCritChance;
    }
    public int getIncreasedCritChance() {
        return increasedCritChance;
    }

    public int getLifeOnHit() {
        return lifeOnHit;
    }

    public void addBaseLifeOnHit(int lifeOnHit) {
        this.lifeOnHit += lifeOnHit;
    }

    public int getLifeSteal() {
        return lifeSteal;
    }

    public void addBaseLifeSteal(int lifeSteal) {
        this.lifeSteal += lifeSteal;
    }
    //TO BE IMPLEMENTED
    public int getProjectileDamage() {
        return projectileDamage;
    }

    public void addBaseProjectileDamage(int projectileDamage) {
        this.projectileDamage += projectileDamage;
    }

    public int getAreaDamage() {
        return areaDamage;
    }

    public void addBaseAreaDamage(int areaDamage) {
        this.areaDamage += areaDamage;
    }
    //-------------------------

    public int getIncreasedPhysicalDamage() {
        return increasedPhysicalDamage;
    }

    public void addBaseIncreasedPhysicalDamage(int increasedPhysicalDamage) {
        this.increasedPhysicalDamage += increasedPhysicalDamage;
    }

    public int getIncreasedElementalDamage() {
        return increasedElementalDamage;
    }

    public void addBaseIncreasedElementalDamage(int increasedElementalDamage) {
        this.increasedElementalDamage += increasedElementalDamage;
    }

    public int getIncreasedFireDamage() {
        return increasedFireDamage;
    }

    public void addBaseIncreasedFireDamage(int increasedFireDamage) {
        this.increasedFireDamage += increasedFireDamage;
    }

    public int getIncreasedLightningDamage() {
        return increasedLightningDamage;
    }

    public void addBaseIncreasedLightningDamage(int increasedLightningDamage) {
        this.increasedLightningDamage += increasedLightningDamage;
    }

    public int getIncreasedColdDamage() {
        return increasedColdDamage;
    }

    public void addBaseIncreasedColdDamage(int increasedColdDamage) {
        this.increasedColdDamage += increasedColdDamage;
    }

    public int getIncreasedAbyssalDamage() {
        return increasedAbyssalDamage;
    }

    public void addBaseIncreasedAbyssalDamage(int increasedAbyssalDamage) {
        this.increasedAbyssalDamage += increasedAbyssalDamage;
    }
}
