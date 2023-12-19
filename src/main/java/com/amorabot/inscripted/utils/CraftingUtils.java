package com.amorabot.inscripted.utils;

public class CraftingUtils {
    // METHODS FOR THE CREATION OF CRAFTING ENUM TYPES ---------------------------------------------
    //TODO: move to utils
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * ((max - min)+1)) + min);
    }
}
