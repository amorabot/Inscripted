package pluginstudies.pluginstudies.Crafting;

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
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * ((max - min)+1)) + min);
    }
    public static int[] rangeOf(int min, int max){
        return new int[]{min, max};
    }
    public static int[] rangeOf(int min1, int max1, int min2, int max2){
        return new int[]{min1, max1, min2, max2};
    }
}
