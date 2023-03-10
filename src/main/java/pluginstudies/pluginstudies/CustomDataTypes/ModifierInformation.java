package pluginstudies.pluginstudies.CustomDataTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifierInformation implements Serializable {

    private static final long serialversionUID = 1L;
    private List<String> modifierNames;
    private List<Integer> modifierTiers;
    private Map<String, int[]> mappedModifiers;

    public ModifierInformation(List<String> modifierList, List<Integer> modifierTiers, Map<String, int[]> mappedMods){
        this.modifierNames = modifierList;
        this.modifierTiers = modifierTiers;
        this.mappedModifiers = mappedMods;
    }

    public List<String> getModifierNames() {
        return modifierNames;
    }

    public void setModifierNames(List<String> modifierNames) {
        this.modifierNames = modifierNames;
    }

    public Map<String, int[]> getMappedModifiers() {
        return mappedModifiers;
    }

    public void setMappedModifiers(Map<String, int[]> mappedModifiers) {
        this.mappedModifiers = mappedModifiers;
    }

    public List<Integer> getModifierTiers() {
        return modifierTiers;
    }

    public void setModifierTiers(List<Integer> modifierTiers) {
        this.modifierTiers = modifierTiers;
    }
}
