package pluginstudies.pluginstudies.components.PlayerComponents;

import static pluginstudies.pluginstudies.utils.StatCalculator.updateMaxHealth;
import static pluginstudies.pluginstudies.utils.StatCalculator.updateMaxWard;

public class Stats {

    private int baseHealth;
    private int healthPercent;
    private int maxHealth;
    private int baseWard;
    private int wardPercent;
    private int maxWard;
    private int armor;
    private int armorPercent;
    private int evasion;
    private int evasionPercent;

    // [HPS, WPS, ELE DMG, FIRE DMG, COLD DMG, LIGHTNING DMG, BLEED, POISON, CONVERSIONS]
    private int DPS;
    private int[] physicalDmg = new int[2];
    private int[] fireDmg = new int[2];
    private int[] coldDmg = new int[2];
    private int[] lightningDmg = new int[2];

    public Stats(int health, int ward, int dps){ //Basic constructor
        this.baseHealth = health;
        this.baseWard = ward;
        this.DPS = dps;
    }
    public Stats(int baseHealth, int healthPercent, int baseWard, int wardPercent, int armor, int armorPercent, int evasion, int evasionPercent){
        this.baseHealth = baseHealth;
        this.healthPercent = healthPercent;
        this.maxHealth = updateMaxHealth(baseHealth, healthPercent);
        this.baseWard = baseWard;
        this.wardPercent = wardPercent;
        this.maxWard = updateMaxWard(baseWard, wardPercent);
        this.armor = armor;
        this.armorPercent = armorPercent;
        this.evasion = evasion;
        this.evasionPercent = evasionPercent;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getBaseHealth() {
        return baseHealth;
    }

    public void setBaseHealth(int baseHealth) {
        this.baseHealth = baseHealth;
    }

    public int getMaxWard() {
        return maxWard;
    }

    public void setMaxWard(int maxWard) {
        this.maxWard = maxWard;
    }

    public int getBaseWard() {
        return baseWard;
    }

    public void setBaseWard(int baseWard) {
        this.baseWard = baseWard;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getEvasion() {
        return evasion;
    }

    public void setEvasion(int evasion) {
        this.evasion = evasion;
    }

    public int getDPS() {
        return DPS;
    }

    public void setDPS(int DPS) {
        this.DPS = DPS;
    }

    public int getHealthPercent() {
        return healthPercent;
    }

    public void setHealthPercent(int healthPercent) {
        this.healthPercent = healthPercent;
    }

    public int getWardPercent() {
        return wardPercent;
    }

    public void setWardPercent(int wardPercent) {
        this.wardPercent = wardPercent;
    }

    public int getArmorPercent() {
        return armorPercent;
    }

    public void setArmorPercent(int armorPercent) {
        this.armorPercent = armorPercent;
    }

    public int getEvasionPercent() {
        return evasionPercent;
    }

    public void setEvasionPercent(int evasionPercent) {
        this.evasionPercent = evasionPercent;
    }
}
