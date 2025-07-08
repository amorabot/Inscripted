package com.amorabot.inscripted.gui.instances;

import com.amorabot.inscripted.gui.button.Button;
import com.amorabot.inscripted.gui.CustomInterfaceVisitor;
import com.amorabot.inscripted.gui.GUI;
import com.amorabot.inscripted.gui.button.CloseButton;
import com.amorabot.inscripted.item.render.InscriptedPalette;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class RelicSelection extends GUI {


    public RelicSelection(Player owner) {
        super(owner, 3, false, true);
        Button weaponsButton = new Button(11, Material.IRON_SWORD,1,false,
                Component.text("Relic Weapons").color(InscriptedPalette.RELIC.getColor()),
                List.of(Component.text("Click to see all Relic Weapons").color(InscriptedPalette.DARK_GRAY.getColor())));
        Button[] defaultButtons = new Button[]{
                new CloseButton(26),
                weaponsButton
        };
        setButtons(defaultButtons);
        this.inventory = renderNewInventory();
    }

    @Override
    public void accept(CustomInterfaceVisitor visitor, InventoryClickEvent event) {
        visitor.visitRelicSelection(this,event);
    }
}
