package com.amorabot.inscripted.gui.button;

import com.amorabot.inscripted.gui.GUI;
import org.bukkit.entity.Player;

public interface InterfaceButton {

    void leftClick(Player playerWhoClicked, GUI openGUI);
    void rightClick(Player playerWhoClicked, GUI openGUI);

    void shiftLeftClick(Player playerWhoClicked, GUI openGUI);
    void shiftRightClick(Player playerWhoClicked, GUI openGUI);
}
