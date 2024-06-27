package com.amorabot.inscripted.GUIs;

import com.amorabot.inscripted.GUIs.modules.GUI;
import com.amorabot.inscripted.GUIs.modules.GUIButton;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.currency.Currencies;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static com.amorabot.inscripted.utils.ColorUtils.translateColorCodes;
import static com.amorabot.inscripted.utils.Utils.color;
import static com.amorabot.inscripted.utils.Utils.convertToPrettyString;

public class OrbGUI extends GUI {



    protected OrbGUI(int rows, boolean editable, boolean renderNulls) {
        super(rows, editable, renderNulls);
        this.inventory = renderInventory(createButtons());
    }
    public OrbGUI(){
        super(6, false, true);
        this.inventory = renderInventory(createButtons());
    }

    @Override
    public Inventory renderInventory(GUIButton[] UIButtons) {
        Inventory cleanInventory = Inscripted.getPlugin().getServer().createInventory(this, 9*getRows());

        if (renderNulls()){
            ItemStack nullItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta itemMeta = nullItem.getItemMeta();
            itemMeta.setDisplayName(" ");
            nullItem.setItemMeta(itemMeta);

            for (int i = 0; i < cleanInventory.getSize(); i++){
                cleanInventory.setItem(i, nullItem);
            }
        }

        for (GUIButton currentButton : UIButtons) {
            setGUIButton(currentButton.getSlot(), currentButton);
            cleanInventory.setItem(currentButton.getSlot(), currentButton.getIcon());
        }

        return cleanInventory;
    }

    @Override
    public GUIButton[] createButtons() {
        GUIButton commonsButton = new GUIButton(mapGridSlot(2,1), Material.WHITE_WOOL, 1, false,
                translateColorCodes("&f&lCommon Orbs"),
                new ArrayList<String>(){
                    {
                        add(color("&8  Below are some orbs associated"));
                        add(color("&8  with &f&lCOMMON&8 item crafting!"));
                    }
                }) {
            @Override
            public void leftClick(Player playerWhoClicked) {}
            @Override
            public void rightClick(Player playerWhoClicked) {}
            @Override
            public void shiftLeftClick(Player playerWhoClicked) {}
            @Override
            public void shiftRightClick(Player playerWhoClicked) {}
        };

        GUIButton magicsButton = new GUIButton(mapGridSlot(2,2), Material.BLUE_WOOL, 1, false,
                translateColorCodes("&9&lMagic Orbs"),
                new ArrayList<String>(){
                    {
                        add(color("&8  Below are some orbs associated"));
                        add(color("&8  with &9&lMAGIC&8 item crafting!"));
                    }
                }) {
            @Override
            public void leftClick(Player playerWhoClicked) {}
            @Override
            public void rightClick(Player playerWhoClicked) {}
            @Override
            public void shiftLeftClick(Player playerWhoClicked) {}
            @Override
            public void shiftRightClick(Player playerWhoClicked) {}
        };

        GUIButton raresButton = new GUIButton(mapGridSlot(2,3), Material.YELLOW_WOOL, 1, false,
                translateColorCodes("&e&lRare Orbs"),
                new ArrayList<String>(){
                    {
                        add(color("&8  Below are some orbs associated"));
                        add(color("&8  with &e&lRARE&8 item crafting!"));
                    }
                }) {
            @Override
            public void leftClick(Player playerWhoClicked) {}
            @Override
            public void rightClick(Player playerWhoClicked) {}
            @Override
            public void shiftLeftClick(Player playerWhoClicked) {}
            @Override
            public void shiftRightClick(Player playerWhoClicked) {}
        };

        GUIButton qualitiesButton = new GUIButton(mapGridSlot(2,5), Material.CYAN_WOOL, 1, false,
                translateColorCodes("&3&lQuality Orbs"),
                new ArrayList<String>(){
                    {
                        add(color("&8  Below are some orbs associated"));
                        add(color("&8  improving a item's quality!"));
                    }
                }) {
            @Override
            public void leftClick(Player playerWhoClicked) {}
            @Override
            public void rightClick(Player playerWhoClicked) {}
            @Override
            public void shiftLeftClick(Player playerWhoClicked) {}
            @Override
            public void shiftRightClick(Player playerWhoClicked) {}
        };

        GUIButton specialsButton = new GUIButton(mapGridSlot(2,7), Material.ORANGE_WOOL, 1, false,
                translateColorCodes("&6&lSpecial Orbs"),
                new ArrayList<String>(){
                    {
                        add(color("&8  Below are some orbs with special"));
                        add(color("&8  crafting capabilities!"));
                    }
                }) {
            @Override
            public void leftClick(Player playerWhoClicked) {}
            @Override
            public void rightClick(Player playerWhoClicked) {}
            @Override
            public void shiftLeftClick(Player playerWhoClicked) {}
            @Override
            public void shiftRightClick(Player playerWhoClicked) {}
        };

        GUIButton augmentButton = getCurrencyButton(mapGridSlot(3,1), Currencies.AUGMENT);

        GUIButton magicChiselButton = getCurrencyButton(mapGridSlot(3,2), Currencies.MAGIC_CHISEL);
        GUIButton alterationButton = getCurrencyButton(mapGridSlot(4,2), Currencies.ALTERATION);
        GUIButton regalButton = getCurrencyButton(mapGridSlot(5,2), Currencies.REGAL);

        GUIButton chaosButton = getCurrencyButton(mapGridSlot(3,3), Currencies.CHAOS);
        GUIButton pristineChiselButton = getCurrencyButton(mapGridSlot(4,3), Currencies.PRISTINE_CHISEL);

        GUIButton sharpButton = getCurrencyButton(mapGridSlot(3,5), Currencies.SHARPENING_WHETSTONE);
        GUIButton armorersButton = getCurrencyButton(mapGridSlot(4,5), Currencies.ARMOR_SCRAP);

        GUIButton nullifyingButton = getCurrencyButton(mapGridSlot(3,7), Currencies.NULLIFYING);
        GUIButton sealButton = getCurrencyButton(mapGridSlot(4,7), Currencies.IMBUIMENT_SEAL);
        GUIButton tendrilsButton = getCurrencyButton(mapGridSlot(5,7), Currencies.PROFANE_TENDRILS);

        return new GUIButton[]{commonsButton, magicsButton, raresButton, qualitiesButton, specialsButton,
        augmentButton,
        magicChiselButton,alterationButton,regalButton,
        chaosButton,pristineChiselButton,
        sharpButton,armorersButton,
        nullifyingButton,sealButton,tendrilsButton};
    }

    private static GUIButton getCurrencyButton(int slot, Currencies currency){
        return new GUIButton(slot, currency.getItem(), 1, false,
                translateColorCodes("&a&l"+currency.getDisplayName()),
                new ArrayList<String>(){
                    {
                        addAll(currency.getDescription());
                        add(" ");
                        add(color("&8  >| "+convertToPrettyString("LEFT-CLICK to get 1")));
                        add(color("&8  >| "+convertToPrettyString("RIGHT-CLICK to get 10")));
                        add(color("&8  >| "+convertToPrettyString("SHIFT + LEFT-CLICK to get 64" + "  ")));
                    }
                }) {
            @Override
            public void leftClick(Player playerWhoClicked) {playerWhoClicked.getInventory().addItem(currency.get(1));}
            @Override
            public void rightClick(Player playerWhoClicked) {playerWhoClicked.getInventory().addItem(currency.get(10));}
            @Override
            public void shiftLeftClick(Player playerWhoClicked) {playerWhoClicked.getInventory().addItem(currency.get(64));}
            @Override
            public void shiftRightClick(Player playerWhoClicked) {playerWhoClicked.sendMessage("who told you to shift right click????");}
        };
    }
}
