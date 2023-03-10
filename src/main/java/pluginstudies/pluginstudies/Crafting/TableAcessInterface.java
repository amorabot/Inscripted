package pluginstudies.pluginstudies.Crafting;

import java.util.List;
import java.util.Map;

public interface TableAcessInterface {
    default Map<Integer, List<int[]>> getAffix(String affixName){
        return null;
    }
    default String[] getAffixNameArray(){
        return null;
    }
}
