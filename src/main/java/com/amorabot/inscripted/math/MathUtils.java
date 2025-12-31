package com.amorabot.inscripted.math;

public class MathUtils {
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * ((max - min)+1)) + min);
    }
    public static boolean fiftyFifty(){
        double random = Math.random();
        return random >= 0.5;
    }
}
