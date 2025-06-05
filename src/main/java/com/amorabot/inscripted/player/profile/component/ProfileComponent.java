package com.amorabot.inscripted.player.profile.component;

import com.amorabot.inscripted.player.profile.parsing.StatPool;
import net.kyori.adventure.text.Component;

import java.util.List;

public interface ProfileComponent {
    void updateComponent(StatPool stats);
    List<Component> asTextComponent();
}
