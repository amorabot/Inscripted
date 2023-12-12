package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.DamageComponent;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;


public class Profile {
    private HealthComponent health;
    private DefenceComponent defences;
    private DamageComponent damage;
    private Attributes attributes;
    private Miscellaneous miscellaneous;
    private Stats stats;
    public Profile(Attributes attributes, Stats stats){
        this.attributes = attributes;
        this.stats = stats;
    }
    public Profile(HealthComponent hp, DefenceComponent def, DamageComponent dmg, Attributes att, Stats stats){
        this.health = hp;
        this.defences = def;
        this.damage = dmg;
        this.attributes = att;
        this.miscellaneous = new Miscellaneous();
        this.stats = stats;
    }
    public Attributes getAttributes(){
        return this.attributes;
    }
    public void setAttributes(Attributes attributes){
        this.attributes = attributes;
    }

    public Miscellaneous getMiscellaneous() {
        return miscellaneous;
    }
    public void setMiscellaneous(Miscellaneous miscellaneous) {
        this.miscellaneous = miscellaneous;
    }

    public Stats getStats() {
        return this.stats;
    }
    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public HealthComponent getHealthComponent(){
        return this.health;
    }
    public DefenceComponent getDefenceComponent(){
        return this.defences;
    }
    public DamageComponent getDamageComponent(){
        return this.damage;
    }
    private void updateProfile(){
        StatCompiler compiler = new StatCompiler(this);
        compiler.updateProfile();
    }

    public void updateMainHand(Weapon weapon){
        getStats().setWeaponSlot(weapon);
        updateProfile();
    }
    public void updateArmorSlot(){
        updateProfile();
    }

    public boolean hasWeaponEquipped(){
        return this.stats.getWeaponSlot() != null;
    }
}
