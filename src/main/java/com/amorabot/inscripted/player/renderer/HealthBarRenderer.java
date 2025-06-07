package com.amorabot.inscripted.player.renderer;

import com.amorabot.inscripted.item.render.CustomUnicodeTable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class HealthBarRenderer {
    //    public static TextDisplay getHPDisplayFor(LivingEntity entity){
//        UUID entityID = entity.getUniqueId();
//        if (!hpDisplays.containsKey(entityID)){
//            return createHPDisplayFor(entity);
//        }
//        return hpDisplays.get(entityID);
//    }

//    public static TextDisplay createHPDisplayFor(LivingEntity entity){
//        TextDisplay display = Inscripted.getPlugin().getWorld().spawn(entity.getLocation().clone().add(0,2.5,0), TextDisplay.class, textDisplay -> {
//            textDisplay.setBillboard(Display.Billboard.CENTER);
//            textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
//            textDisplay.setTextOpacity((byte) (255*0.70));
//            textDisplay.setLineWidth(900);
//            textDisplay.setBackgroundColor(Color.fromARGB(10,30,10,10));
//            textDisplay.setPersistent(false);
//            textDisplay.text(Component.text("Not initialized!"));
//        });
//        entity.addPassenger(display);
//        if (entity instanceof Player){
//            ((Player)entity).hideEntity(Inscripted.getPlugin(),display);
//            hpDisplays.put(entity.getUniqueId(),display);
//        }
//        return display;
//    }
//    public static void destroyHPDisplayFor(LivingEntity entity){
//        TextDisplay hpDisplay = getHPDisplayFor(entity);
//        entity.removePassenger(hpDisplay);
//        hpDisplay.remove();
//        hpDisplays.remove(entity.getUniqueId());
//        Utils.log("Sucessfully removed hp display!");
//    }

//    public static void reloadHPDisplays(){
//        //Assumes the only entity riding the player is their hp display
//        for (Player currentPlayer : Bukkit.getOnlinePlayers()){
//            try{
//                Entity display = currentPlayer.getPassengers().get(0);
//                currentPlayer.removePassenger(display);
//                display.remove();
//                createHPDisplayFor(currentPlayer);
//            } catch (IndexOutOfBoundsException ex){
//                Utils.error("Invalid passenger removal attempt");
//            }
//        }


//public Component getHealthBarComponent(){
//    String hpBarNegSpace = "" + CustomUnicodeTable.M16 + CustomUnicodeTable.M64;
//    String wardBarNegSpace = ""+CustomUnicodeTable.P3 + CustomUnicodeTable.M16 + CustomUnicodeTable.M64;
//    if (getMappedWard()>0){
//        return Component.text(getBarFrame()+hpBarNegSpace+getHPBar()+wardBarNegSpace+getWardBar()).decoration(TextDecoration.ITALIC, false);
//    }
//    return Component.text(getBarFrame()+hpBarNegSpace+getHPBar()).decoration(TextDecoration.ITALIC, false);
//}
//
//    public String getBarFrame(){
//        return "" + CustomUnicodeTable.HP_ICON + CustomUnicodeTable.M8 + CustomUnicodeTable.HP_BAR;
//    }
//
//    public String getHPBar(){
//        StringBuilder hpBar = new StringBuilder(CustomUnicodeTable.HP_HEAD.toString());
//        double mappedHealth = getMappedHealth()/2;
//
//        int currentSegments = 1;
//        //Empty bar rendering
//        if (mappedHealth<=1){
//            hpBar.append((""+CustomUnicodeTable.M1+CustomUnicodeTable.EMPTY_HP).repeat(8));
//            hpBar.append(CustomUnicodeTable.M1).append(CustomUnicodeTable.EMPTY_HP_TAIL);
//            return hpBar.toString();
//        }
//        //Full bar rendering
//        if (mappedHealth>9){
//            hpBar.append((""+CustomUnicodeTable.M1+CustomUnicodeTable.HP_FULL).repeat(8));
//            hpBar.append(CustomUnicodeTable.M1).append(CustomUnicodeTable.HP_TAIL);
//            return hpBar.toString();
//        }
//
//        //Intermediate bar rendering
//        int fullSegments = (int) Math.abs(mappedHealth-1);
//        double decimalHP = (mappedHealth-fullSegments+1);
//        boolean hasHalfSegment = (decimalHP>=0.5);
//        hpBar.append((""+CustomUnicodeTable.M1+CustomUnicodeTable.HP_FULL).repeat(fullSegments));
//        currentSegments+=fullSegments;
//        if (hasHalfSegment){
//            hpBar.append(CustomUnicodeTable.M1).append(CustomUnicodeTable.HP_HALF);
//        } else {
//            hpBar.append(CustomUnicodeTable.M1).append(CustomUnicodeTable.EMPTY_HP);
//        }
//        currentSegments++;
//        int emptySegments = 10 - currentSegments;
//        if (emptySegments == 1){
//            hpBar.append(CustomUnicodeTable.M1).append(CustomUnicodeTable.EMPTY_HP_TAIL);
//            return hpBar.toString();
//        }
//
//        hpBar.append((""+CustomUnicodeTable.M1+CustomUnicodeTable.EMPTY_HP).repeat(emptySegments-1));
//        hpBar.append(CustomUnicodeTable.M1).append(CustomUnicodeTable.EMPTY_HP_TAIL);
//        return hpBar.toString();
//    }
//
//    public String getWardBar(){
//        StringBuilder wardBar = new StringBuilder();
//        double mappedWard = getMappedWard()/2;
//        if (mappedWard==0){return "";}
//        if (mappedWard<=1){return CustomUnicodeTable.HP_HEAD+(CustomUnicodeTable.P8.toString()).repeat(9);}
//        if (mappedWard>9){
//            wardBar.append(CustomUnicodeTable.WARD_HEAD);
//            wardBar.append((""+CustomUnicodeTable.M1+CustomUnicodeTable.WARD_FULL).repeat(8));
//            wardBar.append(CustomUnicodeTable.M1).append(CustomUnicodeTable.WARD_TAIL);
//            return wardBar.toString();
//        }
//        //Intermediate bar rendering
//        wardBar.append(CustomUnicodeTable.WARD_HEAD);
//        int fullSegments = (int) Math.abs(mappedWard-1);
//        int emptySegments = 10-fullSegments+1;
//        boolean hasHalfSegment = (mappedWard-fullSegments+1)>=0.5;
//        wardBar.append((""+CustomUnicodeTable.M1+CustomUnicodeTable.WARD_FULL).repeat(fullSegments));
//        if (hasHalfSegment){
//            wardBar.append(CustomUnicodeTable.M1).append(CustomUnicodeTable.WARD_HALF);
//        }
//        wardBar.append((CustomUnicodeTable.P8.toString()).repeat(emptySegments-2));
////        wardBar.append((CustomUnicodeTable.P16));
//        return wardBar.toString();
//    }
}
