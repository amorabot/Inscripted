package com.amorabot.inscripted.components;

import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Player.Profile;

import java.util.UUID;

public class DefenceComponent implements EntityComponent {
    private final int baseElementalCap;
    private int fireResistance;
    private int fireCapMod;
    private int coldResistance;
    private int coldCapMod;
    private int lightningResistance;
    private int lightningCapMod;
    private int abyssalResistance;
    private final int abyssalCap = 60;

    private int dodge;

    private float finalArmor;
    private int baseArmor;
    private int increasedArmor;
    //Set<ItemModifier> specialModList; // (effects calculations in a specific way)

    //Player constructor
    public DefenceComponent(){
        this.fireResistance = 15;
        this.lightningResistance = 15;
        this.coldResistance = 15;

        this.baseElementalCap = 75; //Player base elemental cap
        this.fireCapMod = 0;
        this.lightningCapMod = 0;
        this.coldCapMod = 0;

        this.dodge = 0;
        this.baseArmor = 0;
    }
    //Mob constructor
    public DefenceComponent(int fireRes, int coldRes, int lightRes, int abyssRes, int dodge, int baseArmor, int incArmor){

        this.baseElementalCap = 80; //Monster base elemental cap
        this.fireCapMod = 0;
        this.lightningCapMod = 0;
        this.coldCapMod = 0;

        this.fireResistance = fireRes;
        this.lightningResistance = coldRes;
        this.coldResistance = lightRes;
        this.abyssalResistance = abyssRes;

        this.dodge = dodge;

        this.baseArmor = baseArmor;
        this.increasedArmor = incArmor;
        setFinalArmor(getBaseArmor(), getIncreasedArmor());
    }
    @Override
    public void reset(){
        fireCapMod = 0;
        lightningCapMod = 0;
        coldCapMod = 0;

        fireResistance = 0;
        lightningResistance = 0;
        coldResistance = 0;
        abyssalResistance = 0;

        dodge = 0;
        baseArmor = 0;
        increasedArmor = 0;
        setFinalArmor(getBaseArmor(), getIncreasedArmor());
    }

    //------------ELE RES METHODS-------------
    //The resistance value can be higher than the cap for that element, it will be capped upon get()
    public void addBaseFireRes(int fireRes){
        this.fireResistance += fireRes;
    }
    public void addBaseColdResistance(int coldRes){
        this.coldResistance += coldRes;
    }
    public void addBaseLightningResistance(int lightRes){
        this.lightningResistance += lightRes;
    }
    public void addBaseAbyssalResistance(int abyssRes){
        this.abyssalResistance += abyssRes;
    }

    public void addBaseFireCap(int fireCapMod){
        this.fireCapMod += fireCapMod;
    }
    public void addBaseColdCap(int coldCapMod){
        this.coldCapMod += coldCapMod;
    }
    public void addBaseLightCap(int lightCapMod){
        this.lightningCapMod += lightCapMod;
    }

    /*
    The actual elemental resistance can be uncapped and things like X% damage for Y uncapped res can occur
    When using this value for practical purposes, it should be capped below 100% and based the entities cap and cap modifiers
     */
    public int getFireResistance() {
        return Math.min(fireResistance, baseElementalCap + fireCapMod);
    }
    public int getColdResistance() {
        return Math.min(coldResistance, baseElementalCap + coldCapMod);
    }
    public int getLightningResistance() {
        return Math.min(lightningResistance, baseElementalCap + lightningCapMod);
    }
    public int getAbyssalResistance() {
        return Math.min(abyssalResistance, abyssalCap);
    }

    public int getBaseElementalCap() {
        return baseElementalCap;
    }
    public byte getAbyssalCap() {
        return abyssalCap;
    }
    public int getFireCapMod() {
        return fireCapMod;
    }
    public int getColdCapMod() {
        return coldCapMod;
    }
    public int getLightningCapMod() {
        return lightningCapMod;
    }

    //------------DODGE METHODS-------------
    public void addBaseDodge(int dodge){
        this.dodge += dodge;
    }

    public int getDodge() {
        return dodge;
    }
    //------------ARMOR METHODS-------------
    public void addBaseArmor(int armor){
        this.baseArmor += armor;
    }

    public void addBaseIncreasedArmor(int incArmor){
        this.increasedArmor += incArmor;
    }

    private void setFinalArmor(int flatArmor, int incArmor){
        this.finalArmor = (float) flatArmor * (1 + incArmor/100f);
    }

    public float getFinalArmor() {
        return finalArmor;
    }
    public int getBaseArmor() {
        return baseArmor;
    }
    public int getIncreasedArmor() {
        return increasedArmor;
    }

    @Override
    public void update(UUID profileID) {
        //When more complex math needs to be done for defence stat calcs,
        //It should be done here

        setFinalArmor(baseArmor, increasedArmor);
    }
}