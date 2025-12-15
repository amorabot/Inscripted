package com.amorabot.inscripted.player.profile.component;

import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.item.inscription.language.ValueType;
import com.amorabot.inscripted.item.structure.Weapon.DamageTypes;
import com.amorabot.inscripted.player.profile.parsing.StatPool;
import com.amorabot.inscripted.skill.annotations.AttackSkill;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.Tags;
import com.amorabot.inscripted.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttackData implements ProfileComponent {
    private static final boolean DEBUG_MODE = false;

    private float DPS;

    private final int[][] hitDamage = new int[DamageTypes.values().length][2];
    private int accuracy;
    private int critChance;
    private int critDamage;
    private int shred;
    private int maelstrom;
    private int firePen;
    private int lightningPen;
    private int coldPen;
    private int bleedChance;
    private int bleedDamage;


    public int[] getPhysicalDmg(){
        return hitDamage[0];
    }
    public void setPhysicalDmg(int[] physDmg){
        this.hitDamage[0] = physDmg;
    }

    public int[] getFireDmg(){
        return hitDamage[1];
    }
    public void setFireDmg(int[] fireDmg){
        this.hitDamage[1] = fireDmg;
    }

    public int[] getLightningDmg(){
        return hitDamage[2];
    }
    public void setLightningDmg(int[] lightDmg){
        this.hitDamage[2] = lightDmg;
    }

    public int[] getColdDmg(){
        return hitDamage[3];
    }
    public void setColdDmg(int[] coldDmg){
        this.hitDamage[3] = coldDmg;
    }

    public int[] getAbyssalDmg(){
        return hitDamage[4];
    }
    public void setAbyssalDmg(int[] abyssDmg){
        this.hitDamage[4] = abyssDmg;
    }
    public void resetDamages(){
        for (int i = 0; i < hitDamage.length; i++) {hitDamage[i] = new int[2];}
    }


    public AttackData(UUID attackerID, Skills skillUsed, StatPool globalPlayerStats){
        if (!skillUsed.isAttackSkill()){
            Utils.error("Skill '" + skillUsed.name() + "' does not have attack data set.");
            return;
        }
        StatPool globalSnapshot = globalPlayerStats.snapshot(); //TODO: Create a filtered version of this snapshot, containing only meaningful stats
        skillUsed.applyBonusStats(globalSnapshot);

        if (DEBUG_MODE){
            Utils.log("base bleed: " + bleedChance);
            globalSnapshot.debug("Stats snapshot for " + skillUsed);
        }

        AttackSkill attackSkillData = skillUsed.getAttackSkillData();
        Tags[] skillTags = skillUsed.getSkillTags();
        assert attackSkillData != null;
        int[] addedDmgs = attackSkillData.addedBaseDmg();
        int[] effectiveness = attackSkillData.dmgEffectiveness();
        double[] normalizedConversions = normalizeConversions(attackSkillData.dmgConversion());

        //Start skill pipeline
        DamageTypes[] dmgTypes = DamageTypes.values();
        for (int i = 0; i < DamageTypes.values().length; i++) {
            DamageTypes type = dmgTypes[i];
            Stats dmgStat = type.getDmgStat();
            //Added skill base damages
            globalSnapshot.insertValue(dmgStat, ValueType.FLAT,new int[]{addedDmgs[(2*i)],addedDmgs[(2*i) + 1]});
            //Apply damage effectiveness for that type
            globalSnapshot.insertValue(dmgStat,ValueType.MULTIPLIER,new int[]{effectiveness[i]});
            //Increases are handled inside the #getFinalValues() on globalSnapshot at a later stage
            //...
        }

        //Convert stored physical dmg to other types
        int[] remainingPhys = convert(normalizedConversions,globalSnapshot);
        globalSnapshot.setBaseStatValue(Stats.PHYSICAL_DAMAGE,ValueType.FLAT,remainingPhys);

        if (skillTags!=null){ // Apply conditional increases to 'Final' damages, if any
            int totalDmgIncrease = 0;
            for (Tags tag : skillTags){
                totalDmgIncrease+=tag.getDamageBonus(attackerID);
            }
            for (DamageTypes dmg : DamageTypes.values()){
                globalSnapshot.insertValue(dmg.getDmgStat(),ValueType.INCREASED,new int[]{totalDmgIncrease});
            }
        }

        //globalSnapshot has been updated, lets get the final values for the AttackData component being constructed
        updateComponent(attackerID,globalSnapshot.calculateFinalValues());
    }
    private int[] convert(double[] normalizedConversions, StatPool globalStats){
        int[] basePhysical = globalStats.getBaseStatValue(Stats.PHYSICAL_DAMAGE,ValueType.FLAT);
        int[] totalConverted = new int[2];

        double fireConversion = normalizedConversions[0];
        if (fireConversion>0){
            int[] fire = Arrays.stream(basePhysical).map(phys -> (int) (phys * fireConversion)).toArray();
            globalStats.insertValue(Stats.FIRE_DAMAGE,ValueType.FLAT,fire);
            totalConverted = Utils.vectorSum(totalConverted,fire);
        }
        double lightningConversion = normalizedConversions[1];
        if (lightningConversion>0){
            int[] lightning = Arrays.stream(basePhysical).map(phys -> (int) (phys * lightningConversion)).toArray();
            globalStats.insertValue(Stats.LIGHTNING_DAMAGE,ValueType.FLAT,lightning);
            totalConverted = Utils.vectorSum(totalConverted,lightning);
        }
        double coldConversion = normalizedConversions[2];
        if (coldConversion>0){
            int[] cold = Arrays.stream(basePhysical).map(phys -> (int) (phys * coldConversion)).toArray();
            globalStats.insertValue(Stats.COLD_DAMAGE,ValueType.FLAT,cold);
            totalConverted = Utils.vectorSum(totalConverted,cold);
        }
        double abyssalConversion = normalizedConversions[3];
        if (abyssalConversion>0){
            int[] abyssal = Arrays.stream(basePhysical).map(phys -> (int) (phys * abyssalConversion)).toArray();
            globalStats.insertValue(Stats.ABYSSAL_DAMAGE,ValueType.FLAT,abyssal);
            totalConverted = Utils.vectorSum(totalConverted,abyssal);
        }
        //Remaining phys -> Subtracting converted from total
        return Utils.vectorSum(basePhysical, Arrays.stream(totalConverted).map(t -> -t).toArray());
    }
    private double[] normalizeConversions(int[] conversions){
        double[] normalizedValues = new double[conversions.length];
        final int conversionSum = Arrays.stream(conversions).sum();
        if (conversionSum>100){
            for (int i = 0; i < conversions.length; i++) {
                normalizedValues[i] = ((double) conversions[i] / conversionSum);
            }
            return normalizedValues;
        }
        for (int i = 0; i < conversions.length; i++) {
            normalizedValues[i] = (conversions[i] / 100D);
        }
        return normalizedValues;
    }
    public int[] getDamage(DamageTypes dmgType){
        switch (dmgType){
            case PHYSICAL -> {
                return getPhysicalDmg();
            }
            case FIRE -> {
                return getFireDmg();
            }
            case LIGHTNING -> {
                return getLightningDmg();
            }
            case COLD -> {
                return getColdDmg();
            }
            case ABYSSAL -> {
                return getAbyssalDmg();
            }
        }
        return new int[2];
    }

    @Override
    public void updateComponent(UUID playerID, Map<Stats, double[]> finalStats) {
        setPhysicalDmg(getAndCast(Stats.PHYSICAL_DAMAGE,finalStats));
        setFireDmg(getAndCast(Stats.FIRE_DAMAGE,finalStats));
        setLightningDmg(getAndCast(Stats.LIGHTNING_DAMAGE,finalStats));
        setColdDmg(getAndCast(Stats.COLD_DAMAGE,finalStats));
        setAbyssalDmg(getAndCast(Stats.ABYSSAL_DAMAGE,finalStats));

        setAccuracy(getSingleValueFrom(Stats.ACCURACY,finalStats));

        setCritChance(getSingleValueFrom(Stats.CRITICAL_CHANCE,finalStats));
        setCritDamage(getSingleValueFrom(Stats.CRITICAL_DAMAGE,finalStats));

        setShred(getSingleValueFrom(Stats.SHRED,finalStats));
        setMaelstrom(getSingleValueFrom(Stats.MAELSTROM,finalStats));

        setFirePen(getSingleValueFrom(Stats.FIRE_PENETRATION,finalStats));
        setLightningPen(getSingleValueFrom(Stats.LIGHTNING_PENETRATION,finalStats));
        setColdPen(getSingleValueFrom(Stats.COLD_PENETRATION,finalStats));

        setBleedChance(getSingleValueFrom(Stats.BLEED,finalStats));
        setBleedDamage(getSingleValueFrom(Stats.BLEED_DAMAGE,finalStats));


        //DPS is total dmg for now
        setDPS();
    }

    @Override
    public List<Component> asTextComponent() {
        return List.of();
    }

    private int[] getAndCast(Stats dmgStat, Map<Stats, double[]> finalStats){
        double[] dmgValues = finalStats.get(dmgStat);
        if (dmgValues == null){return new int[2];}
        return new int[]{(int) dmgValues[0], (int) dmgValues[1]};
    }

    private void setDPS(){
        int totalDmg = 0;
        for (int[] dmg : hitDamage){
            totalDmg += Arrays.stream(dmg).sum();
        }
        DPS = totalDmg;
    }
}
