package pluginstudies.pluginstudies.Crafting.Weapons.Modifiers;

import pluginstudies.pluginstudies.utils.ItemLevelRange;

import java.util.HashMap;
import java.util.Map;

public class ModTableEntry {

    private WeaponModifiers mod;

    private Map<Integer, int[]> mappedRanges = new HashMap<>();

    public ModTableEntry(WeaponModifiers mod, ItemLevelRange... ranges){
        this.mod = mod;
        for (ItemLevelRange range : ranges){
            mappedRanges.put(range.getIlvl(), range.getRange());
        }
    }

    public WeaponModifiers getMod(){
        return mod;
    }
    public Map<Integer, int[]> getMappedRanges(){
        return mappedRanges;
    }
}
