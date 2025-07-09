package com.amorabot.inscripted.gui.instances;

import com.amorabot.inscripted.gui.button.Button;
import com.amorabot.inscripted.gui.CustomInterfaceVisitor;
import com.amorabot.inscripted.gui.GUI;
import com.amorabot.inscripted.gui.button.CloseButton;
import com.amorabot.inscripted.item.relic.Relics;
import com.amorabot.inscripted.item.render.InscriptedPalette;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RelicSelection extends GUI {

    private static final int relicsPerPage;
    private static final int relicsPerRow;
    private List<Relics> selectedRelics = new ArrayList<>();
    static{
        relicsPerRow = 7;
        relicsPerPage = relicsPerRow*3;
    }

    public RelicSelection(Player owner) {
        super(owner, 5, false, true);

        Button[] defaultButtons = new Button[]{
                new CloseButton((9*getRows())-1),
                getWeaponSelector(),
                getArmorSelector()
        };
        setButtons(defaultButtons);
        this.inventory = renderNewInventory();
    }

    @Override
    public void accept(CustomInterfaceVisitor visitor, InventoryClickEvent event) {
        visitor.visitRelicSelection(this,event);
    }

    private Button getWeaponSelector(){
        Button weaponsButton = new Button((2*9)-1+3, Material.IRON_SWORD,1,false,
                Component.text("Relic Weapons").color(InscriptedPalette.RELIC.getColor()).decorate(TextDecoration.BOLD),
                List.of(Component.text("Click to see all Relic Weapons").color(InscriptedPalette.DARK_GRAY.getColor())));
        weaponsButton.setLeftClickFunction(
                (player, gui) -> filterButtons(Relics::isWeapon)
        );
        return weaponsButton;
    }
    private Button getArmorSelector(){
        Button armorsButton = new Button((2*9)-1+7, Material.IRON_CHESTPLATE,1,false,
                Component.text("Relic Armors").color(InscriptedPalette.RELIC.getColor()).decorate(TextDecoration.BOLD),
                List.of(Component.text("Click to see all Relic Weapons").color(InscriptedPalette.DARK_GRAY.getColor())));
        armorsButton.setLeftClickFunction(
                (player, gui) -> filterButtons(Relics::isArmor)
        );
        return armorsButton;
    }
    private void filterButtons(Function<Relics, Boolean> filter){
        Button[] buttonsToRender = new Button[relicsPerPage];
        selectedRelics = Relics.filterRelics(filter);
        for (int i = 0; i < relicsPerPage; i++) { // Render only 1 page for now
            final int currentRowOffset = (i / relicsPerRow);
            if (i>= selectedRelics.size()){
                Button blankButton = new Button(i+10+(currentRowOffset*2), ItemStack.of(Material.AIR));
                buttonsToRender[i] = (blankButton);
                continue;
            }
            Relics selectedRelic = selectedRelics.get(i);
            ItemStack relicIcon = selectedRelic.getItemForm();
            relicIcon.editMeta(
                    itemMeta -> {
                        List<Component> lore = itemMeta.lore();
                        if (lore == null){lore = new ArrayList<>();}
                        lore.add(Component.text(""));
                        lore.add(Component.text("LEFT CLICK TO GENERATE").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
                        itemMeta.lore(lore);
                    }
            );
            Button relicButton = new Button(i+10+(currentRowOffset*2),relicIcon); // Lazy but works, has useless internal data
            relicButton.setLeftClickFunction(
                    (player, gui) -> player.getInventory().addItem(selectedRelic.getItemForm())
            );
            buttonsToRender[i] = relicButton;
        }
        clearDisplaySlots();
        setButtons(buttonsToRender);
        update();
    }

    private void clearDisplaySlots(){
        Button[][] buttons = getButtons();
        for (int r= 1; r < getRows()-1; r++) {
            for (int c = 1; c <= relicsPerRow; c++) {
                buttons[c][r] = null;
            }
        }
    }
}
