package com.amorabot.inscripted.file;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.item.structure.Armor.ArmorTypes;
import com.amorabot.inscripted.item.structure.Armor.DefenceTypes;
import com.amorabot.inscripted.item.structure.EquipmentSlots;
import com.amorabot.inscripted.item.structure.Tiers;
import com.amorabot.inscripted.item.structure.Weapon.WeaponTypes;
//import com.amorabot.inscripted.file.item.RelicEditor;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.amorabot.inscripted.utils.Utils.log;

public class GenericFileManager {

//    private void initializeRelicItemData() {
//        try {
//            log("Initializing relic item data");
//            RelicEditor.setup(); //vai criar o arquivo se ele não existe
//            log("Relic data initialized.");
//        } catch (IOException exception){
//            Utils.error("Unable to setup relic data file");
//        }
//    }

    private static void populateArmorConfigSection(){

        FileConfiguration config = Inscripted.getPlugin().getConfig();

        String armorRoot = ArmorTypes.class.getSimpleName();
        String defString = DefenceTypes.class.getSimpleName();
        for (ArmorTypes armorType : ArmorTypes.values()){
            String currTypeStringPath = armorRoot+"."+armorType.toString();

            String defencePath = currTypeStringPath+"."+defString;
            List<String> defList = new ArrayList<>();
            defList.add(DefenceTypes.SOUL.toString());
            defList.add(DefenceTypes.ARMOR.toString());
            defList.add(DefenceTypes.DODGE.toString());
            config.set(defencePath, defList);

            for (Tiers tier : Tiers.values()){

                String currTierStringPath = currTypeStringPath+"."+tier.toString()+".";

                String namePath = currTierStringPath+"NAME";
                config.set(namePath, "TEMPLATE");

                List<String> mappedDefs = config.getStringList(defencePath);
                for (String def : mappedDefs){
                    String slotDefenceStringPath = currTierStringPath + def;
                    config.set(slotDefenceStringPath, 69);
                }

                for (EquipmentSlots armorSlot : EquipmentSlots.values()){
                    if (armorSlot.equals(EquipmentSlots.WEAPON)){continue;}

                    String slotHealthPath = currTierStringPath + armorSlot;
                    config.set(slotHealthPath, 99);
                }
            }
        }

        Inscripted.getPlugin().saveConfig();
    }

    private static void populateWeaponConfigSection(){
        FileConfiguration config = Inscripted.getPlugin().getConfig();

        String weaponRoot = WeaponTypes.class.getSimpleName();
        for (WeaponTypes weaponType : WeaponTypes.values()){
            String currWeaponTypePath = weaponRoot + "." + weaponType + ".";
            //Fields: Name, Base Damages
            for (Tiers tier : Tiers.values()){
                String currTierPath = currWeaponTypePath + tier + ".";

                String namePath = currTierPath + "NAME";
                String damagePath = currTierPath + "BASE_DAMAGE";

                config.set(namePath, "TEMPREITO");
                config.set(damagePath, new int[2]);
            }
        }

        Inscripted.getPlugin().saveConfig();
    }
}
