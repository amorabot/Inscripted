package com.amorabot.inscripted.item;

import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;

import java.util.concurrent.atomic.AtomicInteger;

public class ItemTests {
    public static void main(String[] args) {
//        for (InscriptionIDs inscription : InscriptionIDs.values()){
//            System.out.println(inscription.getDefinitionData().getDisplayName());
//        }
        AtomicInteger counter = new AtomicInteger();
        func(counter);
        System.out.println(counter);

    }
    private static void func(AtomicInteger counter){
        for (int i = 0; i < 3; i++) {
            counter.getAndIncrement();
            System.out.println(counter);
            if (counter.compareAndSet(1,99)){
                System.out.println(":D");
            }
        }
    }
}
