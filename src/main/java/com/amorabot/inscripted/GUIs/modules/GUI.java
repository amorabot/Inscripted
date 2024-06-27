package com.amorabot.inscripted.GUIs.modules;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public abstract class GUI implements InventoryHolder {

    private final boolean editable;
    private final boolean renderNulls;
    private final int rows;
    private final GUIButton[][] buttons;
    protected Inventory inventory;

    protected GUI(int rows, boolean editable, boolean renderNulls){
        this.editable = editable;
        this.renderNulls = renderNulls;
        this.rows = rows;
        this.buttons = new GUIButton[9][rows];

        // https://docs.papermc.io/paper/dev/custom-inventory-holder -> Paper Docs for inventory holder

//        this.inventory = renderInventory(createButtons());
//        this.inventory = Bukkit.createInventory(owner, 9*rows, title);  //OLD METHOD

    }

    public abstract Inventory renderInventory(GUIButton[] UIButtons);
    public abstract GUIButton[] createButtons();

    public GUIButton getGUISlot(int slot){
        if (!isValidSlot(slot)){return null;}

        int mappedColumn = slot % 9;
        int mappedRow = slot / 9;
        return this.buttons[mappedColumn][mappedRow];
    }
    protected void setGUIButton(int slot, GUIButton button){
        if (!isValidSlot(slot)){return;}

        int mappedColumn = slot % 9;
        int mappedRow = slot / 9;
        getButtons()[mappedColumn][mappedRow] = button;
    }
    private boolean isValidSlot(int slot){
        if ( (slot < 0) || (slot>=rows*9)){ // 0 -> 9*r-1
            Utils.error("Invalid slot access attempt at: " + this.getClass().getSimpleName() + " Class");
            return false;
        }
        return true;
    }

    public void click(Player player, int slot, ClickType type){
        GUIButton clickedButton = getGUISlot(slot);
        if (clickedButton == null){
            Utils.error("Invalid button @" + this.getClass().getSimpleName());
            return;
        }
        switch (type){
            case LEFT -> {
                clickedButton.leftClick(player);
            }
            case RIGHT -> {
                clickedButton.rightClick(player);
            }
            case SHIFT_LEFT -> {
                clickedButton.shiftLeftClick(player);
            }
            case SHIFT_RIGHT -> {
                clickedButton.shiftRightClick(player);
            }
            default ->{
                return;
            }
        }
    }


    public boolean isEditable() {
        return editable;
    }
    public boolean renderNulls() {
        return renderNulls;
    }
    public int getRows() {
        return rows;
    }
    public GUIButton[][] getButtons() {
        return buttons;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public static int mapGridSlot(int row, int column){
        return (9*(row-1)) + column;
    }
}
