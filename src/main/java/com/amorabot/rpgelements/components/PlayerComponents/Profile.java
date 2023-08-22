package com.amorabot.rpgelements.components.PlayerComponents;

import com.amorabot.rpgelements.components.Items.Weapon.Weapon;
import com.amorabot.rpgelements.utils.Utils;

public class Profile {
    private HealthComponent health;
    private DefenceComponent defences;
    private DamageComponent damage;
    private Attributes attributes;
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
        this.stats = stats;
    }
    public Attributes getAttributes(){
        return this.attributes;
    }
    public void setAttributes(Attributes attributes){
        this.attributes = attributes;
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
        getAttributes().update(this.stats);
        getDamageComponent().update(this);
        getHealthComponent().update(this);
//        getDefenceComponent().update(this.stats);
    }
    public void updateMainHand(Weapon weapon){
        getStats().setWeaponSlot(weapon);
        Utils.log("Arma setada com sucesso");
        updateProfile();
    }
    public void updateArmorSlot(){}
}
