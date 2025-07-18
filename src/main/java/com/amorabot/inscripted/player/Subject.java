package com.amorabot.inscripted.player;

import com.amorabot.inscripted.player.profile.PlayerEvents;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private final List<ProfileObserver> observers = new ArrayList<>();

    public void addObserver(ProfileObserver observer){
        int maxObservers = 3;
        if (observers.contains(observer) || observers.size() == maxObservers){
            return;
        }
        observers.add(observer);
    }
    public void removeObserver(ProfileObserver observer){
        observers.remove(observer);
    }

    public void notifyListeners(PlayerEvents event){ //#notify() is std, avoid.
        for (ProfileObserver obs : observers){
            obs.onNotify(event);
        }
    }
}
