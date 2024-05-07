package com.amorabot.inscripted.GUIs.modules;

import org.bukkit.entity.Player;

public interface Button {

    void leftClick(Player playerWhoClicked);
    void rightClick(Player playerWhoClicked);

    void shiftLeftClick(Player playerWhoClicked);
    void shiftRightClick(Player playerWhoClicked);
}
