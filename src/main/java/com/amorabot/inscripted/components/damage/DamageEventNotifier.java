package com.amorabot.inscripted.components.damage;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DamageEventNotifier {
    private static final List<DamageEventObserver> globalObservers = new ArrayList<>();
    private static final ConcurrentHashMap<UUID, List<DamageEventObserver>> playerObservers = new ConcurrentHashMap<>();

    public static void addGlobalObserver(DamageEventObserver observer) {
        synchronized (globalObservers) {
            globalObservers.add(observer);
        }
    }

    public static void removeGlobalObserver(DamageEventObserver observer) {
        synchronized (globalObservers) {
            globalObservers.remove(observer);
        }
    }

    public static void addPlayerObserver(UUID playerId, DamageEventObserver observer) {
        playerObservers.computeIfAbsent(playerId, k -> new ArrayList<>()).add(observer);
    }

    public static void removePlayerObserver(UUID playerId, DamageEventObserver observer) {
        List<DamageEventObserver> observers = playerObservers.get(playerId);
        if (observers != null) {
            observers.remove(observer);
            if (observers.isEmpty()) {
                playerObservers.remove(playerId);
            }
        }
    }

    public static void notifyDamageTaken(DamageEvent event) {
        // Notify global observers
        synchronized (globalObservers) {
            for (DamageEventObserver observer : globalObservers) {
                try {
                    observer.onDamageTaken(event);
                } catch (Exception e) {
                    // Log error but continue with other observers
                    System.err.println("Error in damage observer: " + e.getMessage());
                }
            }
        }

        // Notify player-specific observers
        List<DamageEventObserver> observers = playerObservers.get(event.getTargetId());
        if (observers != null) {
            for (DamageEventObserver observer : observers) {
                try {
                    observer.onDamageTaken(event);
                } catch (Exception e) {
                    System.err.println("Error in player damage observer: " + e.getMessage());
                }
            }
        }
    }

    public static void notifyDamageDealt(DamageEvent event) {
        // Notify global observers
        synchronized (globalObservers) {
            for (DamageEventObserver observer : globalObservers) {
                try {
                    observer.onDamageDealt(event);
                } catch (Exception e) {
                    System.err.println("Error in damage observer: " + e.getMessage());
                }
            }
        }

        // Notify attacker-specific observers
        if (event.getAttackerId() != null) {
            List<DamageEventObserver> observers = playerObservers.get(event.getAttackerId());
            if (observers != null) {
                for (DamageEventObserver observer : observers) {
                    try {
                        observer.onDamageDealt(event);
                    } catch (Exception e) {
                        System.err.println("Error in player damage observer: " + e.getMessage());
                    }
                }
            }
        }
    }

    public static void notifyMobDamaged(DamageEvent event) {
        synchronized (globalObservers) {
            for (DamageEventObserver observer : globalObservers) {
                try {
                    observer.onMobDamaged(event);
                } catch (Exception e) {
                    System.err.println("Error in mob damage observer: " + e.getMessage());
                }
            }
        }

        // Notify attacker-specific observers if attacker is a player
        if (event.getAttackerId() != null) {
            List<DamageEventObserver> observers = playerObservers.get(event.getAttackerId());
            if (observers != null) {
                for (DamageEventObserver observer : observers) {
                    try {
                        observer.onMobDamaged(event);
                    } catch (Exception e) {
                        System.err.println("Error in player damage observer: " + e.getMessage());
                    }
                }
            }
        }
    }

    public static void notifyMobKilled(DamageEvent event) {
        synchronized (globalObservers) {
            for (DamageEventObserver observer : globalObservers) {
                try {
                    observer.onMobKilled(event);
                } catch (Exception e) {
                    System.err.println("Error in mob kill observer: " + e.getMessage());
                }
            }
        }

        // Notify attacker-specific observers if attacker is a player
        if (event.getAttackerId() != null) {
            List<DamageEventObserver> observers = playerObservers.get(event.getAttackerId());
            if (observers != null) {
                for (DamageEventObserver observer : observers) {
                    try {
                        observer.onMobKilled(event);
                    } catch (Exception e) {
                        System.err.println("Error in player damage observer: " + e.getMessage());
                    }
                }
            }
        }
    }

    public static void cleanup() {
        // Remove observers for players that are no longer online
        playerObservers.entrySet().removeIf(entry -> {
            UUID playerId = entry.getKey();
            Player player = org.bukkit.Bukkit.getPlayer(playerId);
            return player == null || !player.isOnline();
        });
    }
}