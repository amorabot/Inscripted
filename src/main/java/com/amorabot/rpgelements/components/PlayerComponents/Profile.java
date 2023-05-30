package com.amorabot.rpgelements.components.PlayerComponents;

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
    public Profile(HealthComponent hp, DefenceComponent def, DamageComponent dmg, Attributes attributes, Stats stats){
        this.health = hp;
        this.defences = def;
        this.damage = dmg;
        this.attributes = attributes;
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
    public HealthComponent getHealth(){
        return this.health;
    }
    public DefenceComponent getDefences(){
        return this.defences;
    }
    public DamageComponent getDamage(){
        return this.damage;
    }
}
