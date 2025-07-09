package com.amorabot.inscripted.gui;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.gui.button.Button;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

// https://docs.papermc.io/paper/dev/custom-inventory-holder -> Paper Docs for inventory holder
@Getter
public abstract class GUI implements InventoryHolder {
    //TODO: Make inner enum for button definition for each interface

    protected static final ItemStack nullIcon;
    static {
        ItemStack nullItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        nullItem.editMeta(
                itemMeta -> itemMeta.displayName(Component.text(" "))
        );
        nullIcon = nullItem;
    }

    private final Player owner;
    protected final boolean editable;
    protected final boolean renderNulls;
    protected final int rows;
    protected final Button[][] buttons;
    protected Inventory inventory;
    protected GUI(Player owner, int rows, boolean editable, boolean renderNulls){
        this.owner = owner;
        this.editable = editable;
        this.renderNulls = renderNulls;
        this.rows = rows;
        this.buttons = new Button[9][rows];
        // Buttons and inventory instance are decided later
    }
    protected GUI(Player owner, int rows, boolean editable, boolean renderNulls, Button[] buttons){
        this.owner = owner;
        this.editable = editable;
        this.renderNulls = renderNulls;
        this.rows = rows;
        this.buttons = new Button[9][rows];
        setButtons(buttons);
        this.inventory = renderNewInventory();
    }

    public void setButtons(Button[] UIButtons){
        for (Button currentButton : UIButtons) {
            setGUIButton(currentButton.getSlot(), currentButton);
        }
    }
    public abstract void accept(CustomInterfaceVisitor visitor, InventoryClickEvent event);

    public void open(){
        owner.openInventory(inventory);
    }
    public Button getGUISlot(int slot){
        if (invalidSlot(slot)){return null;}

        int mappedColumn = slot % 9;
        int mappedRow = slot / 9;
        return this.buttons[mappedColumn][mappedRow];
    }
    protected void setGUIButton(int slot, Button button){
        if (invalidSlot(slot)){return;}

        int mappedColumn = slot % 9;
        int mappedRow = slot / 9;
        getButtons()[mappedColumn][mappedRow] = button;
    }
    private boolean invalidSlot(int slot){
        if ( (slot < 0) || (slot>=rows*9)){ // 0 -> 9*r-1
            Utils.error("Invalid slot: " + slot + "( " + this.getClass().getSimpleName() + " ) Class");
            return true;
        }
        return false;
    }
    public void click(Player player, int slot, ClickType type){
        Button clickedButton = getGUISlot(slot);
        if (clickedButton == null){
            Utils.error("Invalid button @" + this.getClass().getSimpleName());
            return;
        }
        switch (type){
            case LEFT -> clickedButton.leftClick(player,this);
            case RIGHT -> clickedButton.rightClick(player,this);
            case SHIFT_LEFT -> clickedButton.shiftLeftClick(player,this);
            case SHIFT_RIGHT -> clickedButton.shiftRightClick(player,this);
            default ->{
                return;
            }
        }
    }
    public boolean renderNulls() {
        return renderNulls;
    }
    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
    public Inventory renderNewInventory() {
        Inventory cleanInventory = Inscripted.getPlugin().getServer().createInventory(this, 9*getRows());
        // Bukkit.createInventory(player,size,title) -> Alternative (?)

        renderItems(cleanInventory);

        return cleanInventory;
    }
    public void update(){
        inventory.clear();
        renderItems(inventory);
    }
    private void renderItems(Inventory inventory){
        for (int i = 0; i < inventory.getSize(); i++){
            if (renderNulls && (getGUISlot(i) == null)){
                inventory.setItem(i,nullIcon);
                continue;
            }
            inventory.setItem(i,getGUISlot(i).getIcon());
        }
    }
    public static int mapGridSlot(int row, int column){
        return (9*(row-1)) + column;
    }



    //TODO:
    public static class PaginatedGUI extends GUI {

        public PaginatedGUI(Player owner, int rows, boolean editable, boolean renderNulls) {
            super(owner, rows, editable, renderNulls);
        }

        @Override
        public void accept(CustomInterfaceVisitor visitor, InventoryClickEvent event) {

        }
    }
}
