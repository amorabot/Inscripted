//package com.amorabot.inscripted.GUIs;
//
//import com.amorabot.inscripted.GUIs.modules.GUI;
//import com.amorabot.inscripted.GUIs.modules.GUIButton;
//import com.amorabot.inscripted.Inscripted;
//import com.amorabot.inscripted.item.relic.Relics;
//import org.bukkit.Material;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.Inventory;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.ItemMeta;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class RelicsGUI extends GUI {
//    protected RelicsGUI(int rows, boolean editable, boolean renderNulls) {
//        super(rows, editable, renderNulls);
//    }
//    public RelicsGUI(){
//        super(6, false, true);
//        this.inventory = renderInventory(createButtons());
//    }
//
//    @Override
//    public Inventory renderInventory(GUIButton[] UIButtons) {
//        Inventory cleanInventory = Inscripted.getPlugin().getServer().createInventory(this, 9*getRows());
//
//        if (renderNulls()){
//            ItemStack nullItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
//            ItemMeta itemMeta = nullItem.getItemMeta();
//            itemMeta.setDisplayName(" ");
//            nullItem.setItemMeta(itemMeta);
//
//            for (int i = 0; i < cleanInventory.getSize(); i++){
//                cleanInventory.setItem(i, nullItem);
//            }
//        }
//
//        for (GUIButton currentButton : UIButtons) {
//            setGUIButton(currentButton.getSlot(), currentButton);
//            cleanInventory.setItem(currentButton.getSlot(), currentButton.getIcon());
//        }
//
//        return cleanInventory;
//    }
//
//    @Override
//    public GUIButton[] createButtons() {
//        List<GUIButton> relicButtons = new ArrayList<>();
//        for (Relics relic : Relics.values()){
//            ItemStack itemForm = relic.getItemForm();
//
//        }
//
//        return relicButtons.toArray(new GUIButton[0]);
//    }
//}
