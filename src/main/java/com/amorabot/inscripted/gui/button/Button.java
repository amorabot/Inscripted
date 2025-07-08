package com.amorabot.inscripted.gui.button;

import com.amorabot.inscripted.gui.GUI;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;

@Getter
@Setter
public class Button implements InterfaceButton {
    private final ItemStack icon;
    private final int slot;
    private BiConsumer<Player, GUI> leftClickFunction;
    private BiConsumer<Player, GUI> rightClickFunction;
    private BiConsumer<Player, GUI> shiftLeftClickFunction;
    private BiConsumer<Player, GUI> shiftRightClickFunction;
    public Button(int desiredSlot, Material buttonIcon, int quantityDisplay, boolean enchanted, Component name, List<Component> description){
        this.slot = desiredSlot;
        ItemStack iconItem = new ItemStack(buttonIcon);
        iconItem.setAmount(quantityDisplay);
        iconItem.editMeta(
                itemMeta -> {
                    itemMeta.setUnbreakable(true);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    if (enchanted){
                        itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, false);
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                    itemMeta.displayName(name);
                    if (description != null && !description.isEmpty()){
                        itemMeta.lore(description);
                    }
                }
        );
        this.icon = iconItem;
    }
    public Button(int desiredSlot, ItemStack buttonIcon){
        this.icon = buttonIcon;
        this.slot = desiredSlot;
    }

    public void setButtonFunctions(
            BiConsumer<Player, GUI> leftClickFunction,
            BiConsumer<Player, GUI> rightClickFunction,
            BiConsumer<Player, GUI> shiftLeftClickFunction,
            BiConsumer<Player, GUI> shiftRightClickFunction
    ){
        setLeftClickFunction(leftClickFunction);
        setRightClickFunction(rightClickFunction);
        setShiftLeftClickFunction(shiftLeftClickFunction);
        setShiftRightClickFunction(shiftRightClickFunction);
    }

    @Override
    public void leftClick(Player playerWhoClicked, GUI openGUI) {
        if (leftClickFunction==null) {
            Utils.error("Not implemented!");
            return;
        }
        leftClickFunction.accept(playerWhoClicked, openGUI);
    }

    @Override
    public void rightClick(Player playerWhoClicked, GUI openGUI) {
        if (rightClickFunction==null) {
            Utils.error("Not implemented!");
            return;
        }
        rightClickFunction.accept(playerWhoClicked, openGUI);
    }

    @Override
    public void shiftLeftClick(Player playerWhoClicked, GUI openGUI) {
        if (shiftLeftClickFunction==null) {
            Utils.error("Not implemented!");
            return;
        }
        shiftLeftClickFunction.accept(playerWhoClicked, openGUI);
    }

    @Override
    public void shiftRightClick(Player playerWhoClicked, GUI openGUI) {
        if (shiftRightClickFunction==null) {
            Utils.error("Not implemented!");
            return;
        }
        shiftRightClickFunction.accept(playerWhoClicked, openGUI);
    }
}
