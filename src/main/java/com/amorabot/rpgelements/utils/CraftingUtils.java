package com.amorabot.rpgelements.utils;

import java.util.*;

public class CraftingUtils {
    // METHODS FOR THE CREATION OF CRAFTING ENUM TYPES ---------------------------------------------
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
