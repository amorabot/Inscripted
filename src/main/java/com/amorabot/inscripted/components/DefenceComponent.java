package com.amorabot.inscripted.components;

import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Player.Profile;

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
        setFireResistance(15);
        setColdResistance(15);
        setLightningResistance(15);

        this.baseElementalCap = 75; //Player base elemental cap
        setFireCapMod(0);
        setColdCapMod(0);
        setLightningCapMod(0);

        this.dodge = 0;
        this.baseArmor = 0;
    }
    //Mob constructor
    public DefenceComponent(int fireRes, int coldRes, int lightRes, int abyssRes, int dodge, int baseArmor, int incArmor){

        this.baseElementalCap = 80; //Monster base elemental cap
        setFireCapMod(0);
        setColdCapMod(0);
        setLightningCapMod(0);

        setFireResistance(fireRes);
        setColdResistance(coldRes);
        setLightningResistance(lightRes);
        setAbyssalResistance(abyssRes);

        this.dodge = dodge;

        this.baseArmor = baseArmor;
        this.increasedArmor = incArmor;
        setFinalArmor(getBaseArmor(), getIncreasedArmor());
    }

    //------------ELE RES METHODS-------------
    //The resistance value can be higher than the cap for that element, it will be capped upon get()
    public void setFireResistance(int newFireResistance) {
        this.fireResistance = newFireResistance;
    }

    public void setColdResistance(int newColdResistance) {
        this.coldResistance = newColdResistance;
    }
    public void setLightningResistance(int newLightningResistance) {
        this.lightningResistance = newLightningResistance;
    }
    public void setAbyssalResistance(int newAbyssalResistance) {
        this.abyssalResistance = newAbyssalResistance;
    }

    public void setFireCapMod(int fireCapMod) {
        this.fireCapMod = fireCapMod;
    }
    public void setColdCapMod(int coldCapMod) {
        this.coldCapMod = coldCapMod;
    }
    public void setLightningCapMod(int lightningCapMod) {
        this.lightningCapMod = lightningCapMod;
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
    public void setDodge(int newDodge){
        this.dodge = newDodge;
    }
    public int getDodge() {
        return dodge;
    }
    //------------ARMOR METHODS-------------
    public void setBaseArmor(int newBaseArmor){
        this.baseArmor = newBaseArmor;
    }
    public void setIncreasedArmor(int incArmor){
        this.increasedArmor = incArmor;
    }
    private void setFinalArmor(int flatArmor, int incArmor){
        setBaseArmor(flatArmor);
        setIncreasedArmor(incArmor);
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
    public void update(Profile profileData) {
    }
    public void update(int fireResistance, int coldResistance, int lightningResistance, int abyssalResistance, int baseArmor, int increasedArmor, int dodge){
        //Setup elemental cap modifiers arguments
        setFireCapMod(0);
        setColdCapMod(0);
        setLightningCapMod(0);

        setFireResistance(fireResistance);
        setColdResistance(coldResistance);
        setLightningResistance(lightningResistance);
        setAbyssalResistance(abyssalResistance);

        setFinalArmor(baseArmor, increasedArmor);
        setDodge(dodge);
    }
}