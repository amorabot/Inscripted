package com.amorabot.inscripted.skill;

import com.amorabot.inscripted.skill.attackInstances.SkillcastContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class SkillcastData {

    protected final SkillcastContext castingContext;
    protected final CastSource source;

    protected final List<UUID> affectedEntities = new ArrayList<>();
    protected List<UUID> blacklistedEntities = new ArrayList<>();

    public SkillcastData(SkillcastContext context, CastSource source){
        this.castingContext = context;
        this.source = source;
    }
}
