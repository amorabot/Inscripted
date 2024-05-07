package com.amorabot.inscripted.components.Items.Weapon;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Tiers;
import com.amorabot.inscripted.components.Items.Files.ResourcesJSONReader;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum WeaponTypes implements ItemSubtype {

    AXE(RangeCategory.MELEE,WeaponAttackSpeeds.SLOW),
    SWORD(RangeCategory.MELEE,WeaponAttackSpeeds.NORMAL),
    BOW(RangeCategory.RANGED,WeaponAttackSpeeds.NORMAL),
    DAGGER(RangeCategory.MELEE,WeaponAttackSpeeds.QUICK),
    WAND(RangeCategory.RANGED,WeaponAttackSpeeds.SLOW),
    SCEPTRE(RangeCategory.MELEE,WeaponAttackSpeeds.SLOW);

    public static final int weaponDamageVariance = 10;

    private final RangeCategory range;
    private final WeaponAttackSpeeds atkSpeed;

    private final Map<Tiers, int[]> damages = new HashMap<>();
    private final Map<Tiers, String> names = new HashMap<>();
    private Map<String, Map<String, Map<Integer, Integer>>> affixes;


    WeaponTypes(RangeCategory range, WeaponAttackSpeeds atkSpeed){
        this.range = range;
        this.atkSpeed = atkSpeed;
        for (Tiers tier : Tiers.values()){
            this.damages.put(tier, loadBaseDamages(tier));
            this.names.put(tier, loadTierName(tier));
        }
        loadAffixes();
    }


    public int[] mapBaseDamage(Tiers tier){
        return this.damages.getOrDefault(tier, new int[2]).clone();
    }
//    public abstract Material mapWeaponBase(Tiers tier);
    public int mapWeaponTierModel(Tiers tier){
        int modelID = (this.ordinal()+1)+(WeaponTypes.values().length*tier.ordinal());
        return modelID;
    }
    public WeaponAttackSpeeds getBaseAttackSpeed(){
        return this.atkSpeed;
    }
    public Material mapWeaponBase(){
        return getRange().getItem();
    }
    public RangeCategory getRange() {
        return range;
    }

    private void loadAffixes(){
        this.affixes = ResourcesJSONReader.getModifierTableFor(ItemTypes.WEAPON, this);

        Utils.log("Modifiers loaded successfully!(" + this + ")");
    }
    public Map<String, Map<String, Map<Integer, Integer>>> getAffixes(){
        return this.affixes;
    }

    @Override
    public String loadTierName(Tiers tier) {
        String namePath = WeaponTypes.class.getSimpleName() + "." + this + "." + tier + "." + "NAME";
        return Inscripted.getPlugin().getConfig().getString(namePath);
    }
    @Override
    public String getTierName(Tiers tier) {
        return this.names.getOrDefault(tier, "INVALID WEAPON");
    }
    private int[] loadBaseDamages(Tiers tier){
        FileConfiguration config = Inscripted.getPlugin().getConfig();

        WeaponTypes subtype = this;
        String subtypePath = WeaponTypes.class.getSimpleName() + "." + subtype + ".";
        String tierBaseDamagePath = subtypePath + tier + "." + "BASE_DAMAGE";

        int[] dmgArray = new int[2];
        List<Integer> dmgList = config.getIntegerList(tierBaseDamagePath);
        for (int i = 0; i<dmgList.size(); i++){
            dmgArray[i] = dmgList.get(i);
        }
        return dmgArray;
    }
}
