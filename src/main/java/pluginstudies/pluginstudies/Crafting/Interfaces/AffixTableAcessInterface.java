package pluginstudies.pluginstudies.Crafting.Interfaces;

import pluginstudies.pluginstudies.Crafting.Weapons.Modifiers.WeaponModifiers;

import java.util.List;
import java.util.Map;

public interface AffixTableAcessInterface {
    @Deprecated
    default Map<Integer, List<int[]>> getAffix(String affixName){//TODO: LIMPAR USAGES DE getAffix e getAffixNameArrat
        return null;
    }
    @Deprecated
    default String[] getAffixNameArray(){
        return null;
    }

    default Map<Integer, int[]> getModTable(WeaponModifiers mod){
        return null;
    }
    default List<WeaponModifiers> getAllModNames(){
        return null;
    }
}
