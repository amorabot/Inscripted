package com.amorabot.inscripted.components.Items.Weapon;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Tiers;
import com.amorabot.inscripted.components.Items.Files.ResourcesJSONReader;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.inscriptions.InscriptionTable;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum WeaponTypes implements ItemSubtype {

    AXE(RangeCategory.MELEE,WeaponAttackSpeeds.SLOW),
    SWORD(RangeCategory.MELEE,WeaponAttackSpeeds.NORMAL),
    BOW(RangeCategory.RANGED,WeaponAttackSpeeds.SLOW),
    DAGGER(RangeCategory.MELEE,WeaponAttackSpeeds.QUICK),
    WAND(RangeCategory.RANGED,WeaponAttackSpeeds.SLOW),
    MACE(RangeCategory.MELEE,WeaponAttackSpeeds.SLOW);

    public static final int weaponDamageVariance = 10;

    @Getter
    private final RangeCategory range;
    private final WeaponAttackSpeeds atkSpeed;

    private final Map<Tiers, int[]> damages = new HashMap<>();
    private final Map<Tiers, String> names = new HashMap<>();

    private final InscriptionTable itemInscriptionTable;


    WeaponTypes(RangeCategory range, WeaponAttackSpeeds atkSpeed){
        this.range = range;
        this.atkSpeed = atkSpeed;
        for (Tiers tier : Tiers.values()){
            this.damages.put(tier, loadBaseDamages(tier));
            this.names.put(tier, loadTierName(tier));
        }
        this.itemInscriptionTable = new InscriptionTable(this.toString());
    }

    public InscriptionTable getTableData(){
        return this.itemInscriptionTable;
    }


    public int[] mapBaseDamage(Tiers tier){
        return this.damages.getOrDefault(tier, new int[2]).clone();
    }
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
