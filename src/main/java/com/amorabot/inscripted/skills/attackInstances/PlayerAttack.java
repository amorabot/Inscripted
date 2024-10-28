package com.amorabot.inscripted.skills.attackInstances;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class PlayerAttack {

    private final AttackContext context;

    private final List<UUID> affectedEntities = new ArrayList<>();
    private List<UUID> blacklistedEntities = new ArrayList<>();

    protected PlayerAttack(AttackContext context, boolean blacklistCaster) {
        this.context = context;
        if (blacklistCaster){
            blacklistedEntities.add(context.getAttackerID());
        }
    }
    protected PlayerAttack(AttackContext context, List<UUID> blacklistedEntities) {
        this.context = context;
        this.blacklistedEntities = blacklistedEntities;
    }

    public abstract void execute();

    public Entity getOwner(){
        return Bukkit.getEntity(getContext().getAttackerID());
    }
}
