package com.amorabot.inscripted.player;

import com.amorabot.inscripted.player.profile.ProfileEvents;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private final List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer){
        int maxObservers = 3;
        if (observers.contains(observer) || observers.size() == maxObservers){
            return;
        }
        observers.add(observer);
    }
    public void removeObserver(Observer observer){
        observers.remove(observer);
    }

    public void notifyListeners(ProfileEvents event){ //#notify() is std, avoid.
        for (Observer obs : observers){
            obs.onNotify(event);
        }
    }
}
