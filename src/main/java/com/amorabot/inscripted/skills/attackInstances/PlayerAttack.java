package com.amorabot.inscripted.skills.attackInstances;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class PlayerAttack {

    private final AttackContext context;

    private final List<UUID> affectedEntities = new ArrayList<>();
    private List<UUID> blacklistedEntities = new ArrayList<>();

    protected PlayerAttack(AttackContext context) {
        this.context = context;
    }
    protected PlayerAttack(AttackContext context, List<UUID> blacklistedEntities) {
        this.context = context;
        this.blacklistedEntities = blacklistedEntities;
    }

    public abstract void execute();
}
