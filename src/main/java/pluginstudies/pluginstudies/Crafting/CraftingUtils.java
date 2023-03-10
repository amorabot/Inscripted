package pluginstudies.pluginstudies.Crafting;

import pluginstudies.pluginstudies.utils.Pair;

import java.util.*;

public class CraftingUtils {
    // METHODS FOR THE CREATION OF CRAFTING ENUM TYPES ---------------------------------------------
    public static List<int[]> generateRange(int min, int max){
        List<int[]> range = new ArrayList<>();
        range.add(new int[]{min, max});

        return range;
    }
    public static List<int[]> generateRange(int min1, int max1, int min2, int max2){
        List<int[]> range = new ArrayList<>();
        range.add(new int[]{min1, max1});
        range.add(new int[]{min2, max2});

        return range;
    }
    public static Map<Integer, List<int[]>> mapRanges(Pair<Integer, List<int[]>>... ilvlRangePairs){
        Map<Integer, List<int[]>> mappedPairs = new HashMap<>();
        for (Pair<Integer, List<int[]>> pair : ilvlRangePairs){
            mappedPairs.put(pair.getName(), pair.getRange());
        }
        return mappedPairs;
    }
}
