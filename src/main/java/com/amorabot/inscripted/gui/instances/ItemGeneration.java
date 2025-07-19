package com.amorabot.inscripted.gui.instances;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.gui.CustomInterfaceVisitor;
import com.amorabot.inscripted.gui.GUI;
import com.amorabot.inscripted.gui.button.Button;
import com.amorabot.inscripted.gui.button.CloseButton;
import com.amorabot.inscripted.item.render.InscriptedPalette;
import com.amorabot.inscripted.item.structure.Armor.Armor;
import com.amorabot.inscripted.item.structure.Armor.ArmorTypes;
import com.amorabot.inscripted.item.structure.EquipmentSlots;
import com.amorabot.inscripted.item.structure.ItemRarities;
import com.amorabot.inscripted.item.structure.Tiers;
import com.amorabot.inscripted.item.structure.Weapon.Weapon;
import com.amorabot.inscripted.item.structure.Weapon.WeaponTypes;
import com.amorabot.inscripted.player.Archetypes;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemGeneration extends GUI {
    private Archetypes selectedArchetype = null;
    private ItemRarities selectedRarity = null;
    private Tiers selectedTier = null;
    private EquipmentSlots equipmentSlot = null;


    public ItemGeneration(Player owner) {
        super(owner, 6, false, true);
        setButtons(getArchetypeButtons());
        setButtons(getTierButtons());
        setButtons(getRarityButtons());
        setButtons(getSlotButtons());
        setButtons(getSpecialButtons());

        this.inventory = renderNewInventory();
    }

    @Override
    public void accept(CustomInterfaceVisitor visitor, InventoryClickEvent event) {
        visitor.visitItemGeneration(this,event);
    }

    public Button[] getSpecialButtons(){
        Button[] buttons = new Button[3];
        buttons[0] = new CloseButton(mapGridSlot(1,3));
        Button forgeIcon = new Button(mapGridSlot(1,4),
                Material.ANVIL,1,false,getButtonName("Equipment forge",NamedTextColor.GRAY.asHexString()),List.of(
                Component.text(""),
                styleDescriptionLine("  Tweak all equipment parameters"),
                styleDescriptionLine("  and generate a brand new piece"),
                styleDescriptionLine("  of equipment by clicking the"),
                Component.text("  >> GREEN BUTTON >> ").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD),
                styleDescriptionLine("  When all parameters are defined!  "),
                Component.text("")
        ));
        buttons[1] = forgeIcon;
        Button confirmButton = new Button(mapGridSlot(1,5),
                Material.LIME_WOOL,1,false,
                getButtonName("Click to generate",NamedTextColor.GREEN.asHexString()),new ArrayList<>());
        //block.anvil.land
        confirmButton.setLeftClickFunction(
                (player, gui) -> {
                    if (selectedArchetype==null){
                        player.sendMessage(getButtonName("Archetype not set.",NamedTextColor.DARK_RED.asHexString()));
                    }
                    if (selectedRarity==null){
                        player.sendMessage(getButtonName("Rarity not set.",NamedTextColor.DARK_RED.asHexString()));
                    }
                    if (selectedTier==null){
                        player.sendMessage(getButtonName("Tier not set.",NamedTextColor.DARK_RED.asHexString()));
                    }
                    if (equipmentSlot==null){
                        player.sendMessage(getButtonName("Slot not set.",NamedTextColor.DARK_RED.asHexString()));
                    }
                    if (selectedArchetype==null || selectedRarity==null || selectedTier==null || equipmentSlot==null){
                        SoundAPI.playAnvil(player,player.getLocation());
                        return;
                    }
                    //Weapon
                    if (equipmentSlot.equals(EquipmentSlots.WEAPON)){
                        Weapon generatedWeapon = new Weapon(selectedTier.getMaxLevel(), selectedArchetype.getWeaponType(),selectedRarity,true,false);
                        ItemStack weaponItem = generatedWeapon.getItemForm();
                        player.getInventory().addItem(weaponItem);
                        return;
                    }
                    //Armor
                    Armor generatedArmor = new Armor(selectedTier.getMaxLevel(), selectedArchetype.getArmorType(),
                            selectedRarity,true, false, equipmentSlot);
                    ItemStack armorItemStack = generatedArmor.getItemForm();
                    player.getInventory().addItem(armorItemStack);
                }
        );
        buttons[2] = confirmButton;
        return buttons;
    }

    public Button[] getSlotButtons(){
        Button[] buttons = new Button[6];

        Component descriptionName = getButtonName("Equipment slot",NamedTextColor.GRAY.asHexString());
        Component helm = getButtonName(EquipmentSlots.HELMET.name(),NamedTextColor.GRAY.asHexString());
        Component chest = getButtonName(EquipmentSlots.CHESTPLATE.name(),NamedTextColor.GRAY.asHexString());
        Component leg = getButtonName(EquipmentSlots.LEGGINGS.name(),NamedTextColor.GRAY.asHexString());
        Component boot = getButtonName(EquipmentSlots.BOOTS.name(),NamedTextColor.GRAY.asHexString());
        Component weapon = getButtonName(EquipmentSlots.WEAPON.name(),NamedTextColor.GRAY.asHexString());
        int descSlot = mapGridSlot(5,1);
        List<Integer> equipSlotButtonSlots = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {equipSlotButtonSlots.add(descSlot + i);}
        Button description = new Button(descSlot,Material.NAME_TAG,1,false,descriptionName, List.of(
                styleDescriptionLine("  Select the equipment slot that you want to generate  "),
                Component.text(""),
                Component.text("Click to clear the slot selector").color(NamedTextColor.RED)
        ));
        description.setLeftClickFunction(
                (player, gui) -> {
                    equipmentSlot = null;
                    resetEnchantmentGlintFrom(equipSlotButtonSlots);
                    gui.update();
                    SoundAPI.playGenericSoundAtLocation(Audience.audience(player),player.getLocation(),"entity.experience_orb.pickup", 0.6f,0.3f);
                }
        );
        buttons[0] = description;

        Button helmButton = new Button(descSlot+1,Material.IRON_HELMET,1,false,helm,getGenericButtonDesc("Equipment slot"));
        helmButton.setLeftClickFunction(
                (player, gui) -> {
                    setEquipmentSlot(EquipmentSlots.HELMET);
                    applyGlintToButton(descSlot,equipSlotButtonSlots,1,player, gui);
                }
        );
        buttons[1] = helmButton;

        Button chestButton = new Button(descSlot+2,Material.IRON_CHESTPLATE,1,false,chest,getGenericButtonDesc("Equipment slot"));
        chestButton.setLeftClickFunction(
                (player, gui) -> {
                    setEquipmentSlot(EquipmentSlots.CHESTPLATE);
                    applyGlintToButton(descSlot,equipSlotButtonSlots,2,player, gui);
                }
        );
        buttons[2] = chestButton;

        Button legButton = new Button(descSlot+3,Material.IRON_LEGGINGS,1,false,leg,getGenericButtonDesc("Equipment slot"));
        legButton.setLeftClickFunction(
                (player, gui) -> {
                    setEquipmentSlot(EquipmentSlots.LEGGINGS);
                    applyGlintToButton(descSlot,equipSlotButtonSlots,3,player, gui);
                }
        );
        buttons[3] = legButton;

        Button bootsButton = new Button(descSlot+4,Material.IRON_BOOTS,1,false,boot,getGenericButtonDesc("Equipment slot"));
        bootsButton.setLeftClickFunction(
                (player, gui) -> {
                    setEquipmentSlot(EquipmentSlots.BOOTS);
                    applyGlintToButton(descSlot,equipSlotButtonSlots,4,player, gui);
                }
        );
        buttons[4] = bootsButton;

        Button weaponButton = new Button(descSlot+5,Material.IRON_SWORD,1,false,weapon,getGenericButtonDesc("Equipment slot"));
        weaponButton.setLeftClickFunction(
                (player, gui) -> {
                    setEquipmentSlot(EquipmentSlots.WEAPON);
                    applyGlintToButton(descSlot,equipSlotButtonSlots,5,player, gui);
                }
        );
        buttons[5] = weaponButton;

        return buttons;
    }

    public Button[] getRarityButtons(){
        Button[] buttons = new Button[5];

        Component descriptionName = getButtonName("Rarities",NamedTextColor.GRAY.asHexString());
        Component commonName = getButtonName("Common", InscriptedPalette.WHITE.getColorString());
        Component augmentedName = getButtonName("Augmented", InscriptedPalette.AUGMENTED.getColorString());
        Component runicName = getButtonName("Runic", InscriptedPalette.RUNIC.getColorString());
        Component relicName = getButtonName("Relic", InscriptedPalette.RELIC.getColorString());

        int descSlot = mapGridSlot(4,1);
        List<Integer> rarityButtonSlots = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {rarityButtonSlots.add(descSlot + i);}

        Button description = new Button(descSlot,Material.NAME_TAG,1,false,descriptionName,rarityDesc());
        description.setLeftClickFunction(
                (player, gui) -> {
                    selectedRarity = null;
                    resetEnchantmentGlintFrom(rarityButtonSlots);
                    gui.update();
                    SoundAPI.playGenericSoundAtLocation(Audience.audience(player),player.getLocation(),"entity.experience_orb.pickup", 0.6f,0.3f);
                }
        );
        buttons[0] = description;

        Button common = new Button(descSlot+1,Material.LIGHT_GRAY_DYE,1,false,commonName,getGenericButtonDesc("Rarity"));
        common.setLeftClickFunction(
                (player, gui) -> {
                    setSelectedRarity(ItemRarities.COMMON);
                    applyGlintToButton(descSlot,rarityButtonSlots,ItemRarities.COMMON.ordinal()+1,player, gui);
                }
        );
        buttons[1] = common;

        Button augmented = new Button(descSlot+2,Material.LIGHT_BLUE_DYE,1,false,augmentedName,getGenericButtonDesc("Rarity"));
        augmented.setLeftClickFunction(
                (player, gui) -> {
                    setSelectedRarity(ItemRarities.AUGMENTED);
                    applyGlintToButton(descSlot,rarityButtonSlots,ItemRarities.AUGMENTED.ordinal()+1,player, gui);
                }
        );
        buttons[2] = augmented;

        Button runic = new Button(descSlot+3,Material.YELLOW_DYE,1,false,runicName,getGenericButtonDesc("Rarity"));
        runic.setLeftClickFunction(
                (player, gui) -> {
                    setSelectedRarity(ItemRarities.RUNIC);
                    applyGlintToButton(descSlot,rarityButtonSlots,ItemRarities.RUNIC.ordinal()+1,player, gui);
                }
        );
        buttons[3] = runic;

        Button relic = new Button(descSlot+4,Material.RED_DYE,1,false,relicName,List.of(
                styleDescriptionLine("Special rarity"),
                Component.text(""),
                Component.text("Click to access the Relics Menu").color(NamedTextColor.GREEN)
        ));
        relic.setLeftClickFunction(
                (player, gui) -> {
                    player.closeInventory();
                    new RelicSelection(player).open();
                }
        );
        buttons[4] = relic;

        return buttons;
    }
    private List<Component> rarityDesc(){
        List<Component> lore = new ArrayList<>();
        Component emptyLine = Component.text("");

        lore.add(emptyLine);
        lore.add(getButtonName("  Craftable rarities:", InscriptedPalette.NEUTRAL_GRAY.getColorString()).decoration(TextDecoration.ITALIC,false));
        Component commonName = getButtonName("  Common", InscriptedPalette.WHITE.getColorString())
                .append(Component.text(
                        Utils.convertToPrettyString(" >| By default won't have any Inscriptions  "))
                .color(InscriptedPalette.DARK_GRAY.getColor()).decorate(TextDecoration.ITALIC).decoration(TextDecoration.BOLD,false));
        Component augmentedName = getButtonName("  Augmented", InscriptedPalette.AUGMENTED.getColorString())
                .append(Component.text(
                        Utils.convertToPrettyString(" >| Can hold up to 2 Inscriptions  "))
                .color(InscriptedPalette.DARK_GRAY.getColor()).decorate(TextDecoration.ITALIC).decoration(TextDecoration.BOLD,false));
        Component runicName = getButtonName("  Runic", InscriptedPalette.RUNIC.getColorString())
                .append(Component.text(
                        Utils.convertToPrettyString(" >| Can have from 3 up to 6 Inscriptions  "))
                .color(InscriptedPalette.DARK_GRAY.getColor()).decorate(TextDecoration.ITALIC).decoration(TextDecoration.BOLD,false));
        lore.add(commonName);
        lore.add(augmentedName);
        lore.add(runicName);
        lore.add(emptyLine);
        lore.add(getButtonName("  Relics:", InscriptedPalette.RELIC.getColorString()));
        lore.add(styleDescriptionLine(Utils.convertToPrettyString("   >| predefined but powerful items.")));
        lore.add(emptyLine);
        lore.add(styleDescriptionLine("  A item's rarity actively reflects"));
        lore.add(styleDescriptionLine("  the number of Inscriptions you can"));
        lore.add(styleDescriptionLine("  find on it. Craftable items can have"));
        lore.add(styleDescriptionLine("  their rarity changed and also allow"));
        lore.add(styleDescriptionLine("  for Inscription customization through  "));
        lore.add(styleDescriptionLine("  the various existing Orbs!"));
        lore.add(emptyLine);
        lore.add(styleDescriptionLine("  Relic items, for instance, are pre-"));
        lore.add(styleDescriptionLine("  defined and are immutable! They can"));
        lore.add(styleDescriptionLine("  be a safe choice and possibly have"));
        lore.add(styleDescriptionLine("  game-altering properties!"));
        lore.add(emptyLine);
        lore.add(Component.text("Click to reset selection").color(NamedTextColor.RED));

        return lore;
    }

    public Button[] getTierButtons(){
        Button[] buttons = new Button[6];

        Component descriptionName = getButtonName("Item tiers", NamedTextColor.GRAY.asHexString());

        Component t1Name = getButtonName("Tier 1: Leather", "#966E3A");
        Component t2Name = getButtonName("Tier 2: Chainmail", NamedTextColor.DARK_GRAY.asHexString());
        Component t3Name = getButtonName("Tier 3: Iron", NamedTextColor.GRAY.asHexString());
        Component t4Name = getButtonName("Tier 4: Crystal", NamedTextColor.AQUA.asHexString());
        Component t5Name = getButtonName("Tier 5: Runic Gold", NamedTextColor.GOLD.asHexString());

        int descSlot = mapGridSlot(3,1);
        List<Integer> tierButtonSlots = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {tierButtonSlots.add(descSlot + i);}
        Button description = new Button(descSlot,Material.NAME_TAG,1,false,descriptionName,getTiersDescription());
        description.setLeftClickFunction(
                (player, gui) -> {
                    selectedTier = null;
                    resetEnchantmentGlintFrom(tierButtonSlots);
                    gui.update();
                    SoundAPI.playGenericSoundAtLocation(Audience.audience(player),player.getLocation(),"entity.experience_orb.pickup", 0.6f,0.3f);
                }
        );
        buttons[0] = description;

        Button t1 = new Button(descSlot+1,Material.LEATHER_HELMET,1,false,t1Name, getGenericButtonDesc("Item tier"));
        t1.setLeftClickFunction(
                (player, gui) -> {
                    setSelectedTier(Tiers.T1);
                    applyGlintToButton(descSlot,tierButtonSlots,Tiers.T1.ordinal()+1,player, gui);
                }
        );
        buttons[1] = t1;

        Button t2 = new Button(descSlot+2,Material.CHAINMAIL_HELMET,2,false,t2Name, getGenericButtonDesc("Item tier"));
        t2.setLeftClickFunction(
                (player, gui) -> {
                    setSelectedTier(Tiers.T2);
                    applyGlintToButton(descSlot,tierButtonSlots,Tiers.T2.ordinal()+1,player, gui);
                }
        );
        buttons[2] = t2;

        Button t3 = new Button(descSlot+3,Material.IRON_HELMET,3,false,t3Name, getGenericButtonDesc("Item tier"));
        t3.setLeftClickFunction(
                (player, gui) -> {
                    setSelectedTier(Tiers.T3);
                    applyGlintToButton(descSlot,tierButtonSlots,Tiers.T3.ordinal()+1,player, gui);
                }
        );
        buttons[3] = t3;

        Button t4 = new Button(descSlot+4,Material.DIAMOND_HELMET,4,false,t4Name, getGenericButtonDesc("Item tier"));
        t4.setLeftClickFunction(
                (player, gui) -> {
                    setSelectedTier(Tiers.T4);
                    applyGlintToButton(descSlot,tierButtonSlots,Tiers.T4.ordinal()+1,player, gui);
                }
        );
        buttons[4] = t4;

        Button t5 = new Button(descSlot+5,Material.GOLDEN_HELMET,5,false,t5Name, getGenericButtonDesc("Item tier"));
        t5.setLeftClickFunction(
                (player, gui) -> {
                    setSelectedTier(Tiers.T5);
                    applyGlintToButton(descSlot,tierButtonSlots,Tiers.T5.ordinal()+1,player, gui);
                }
        );
        buttons[5] = t5;

        return buttons;
    }
    private void applyGlintToButton(int descriptionSlot, List<Integer> slotsToReset, int rowOffset, Player player, GUI gui){
        resetEnchantmentGlintFrom(slotsToReset);
        getGUISlot(descriptionSlot+rowOffset).applyGlint();
        gui.update();
        SoundAPI.playGenericSoundAtLocation(Audience.audience(player),player.getLocation(),"entity.experience_orb.pickup", 0.5f,0.8f);
    }
    private List<Component> getTiersDescription(){
        List<Component> lore = new ArrayList<>();
        Component emptyLine = Component.text("");
        lore.add(emptyLine);
        lore.add(styleDescriptionLine("  Tiers are meant to reflect the"));
        lore.add(styleDescriptionLine("  item's potential and are dictated"));
        lore.add(styleDescriptionLine("  by the item level."));
        lore.add(emptyLine);
        lore.add(styleDescriptionLine("  For example, certain Inscriptions"));
        lore.add(styleDescriptionLine("  are only available above certain"));
        lore.add(styleDescriptionLine("  item levels. Also, all available"));
        lore.add(styleDescriptionLine("  Inscriptions on a item tend to get  "));
        lore.add(styleDescriptionLine("  stronger with item level."));
        lore.add(emptyLine);
        for (int i = 0; i < Tiers.values().length; i++) {
            Tiers tier = Tiers.values()[i];
            Component tierName = Component.text(Utils.convertToPrettyString(tier.name()+": "))
                    .decorate(TextDecoration.BOLD).color(InscriptedPalette.NEUTRAL_GRAY.getColor());
            if (tier.ordinal()==0){
                Component desc = styleDescriptionLine("Ilvl 0 - " + tier.getMaxLevel());
                lore.add(Component.text("  ").append(tierName.append(desc)));
                continue;
            }
            Component desc = styleDescriptionLine("Ilvl "+Tiers.values()[i-1].getMaxLevel()+" - " + tier.getMaxLevel());
            lore.add(Component.text("  ").append(tierName.append(desc)));
        }
        lore.add(emptyLine);
        lore.add(Component.text("Click to reset selection").color(NamedTextColor.RED));
        return lore;
    }
    private Component styleDescriptionLine(String rawText){
        return Component.text(rawText).color(InscriptedPalette.DARK_GRAY.getColor());
    }
    private List<Component> getGenericButtonDesc(String desc){
        return List.of(styleDescriptionLine(desc),Component.text(""),Component.text("Click to Select").color(NamedTextColor.GREEN));
    }


    private Button[] getArchetypeButtons(){
        Button[] buttons = new Button[7];
        Component descriptionButtonName = Component.text(Utils.convertToPrettyString("Archetypes"))
                .color(InscriptedPalette.NEUTRAL_GRAY.getColor()).decorate(TextDecoration.BOLD);
        Component offset = Component.text("   ");
        Component divisor = Component.text(" | ").color(InscriptedPalette.DARK_GRAY.getColor());
        Component selectLine = Component.text("Click to reset selection").color(NamedTextColor.RED);
        List<Component> descriptionButtonDesc = List.of(
                Component.text("Select your desired item archetype")
                        .color(InscriptedPalette.DARK_GRAY.getColor()),
                Component.text(""),
                offset.append(getArchetypeDescriptor(Archetypes.MARAUDER,"Overpowering strength")),
                offset.append(getArchetypeDescriptor(Archetypes.GLADIATOR,"Elite combat prowess")),
                offset.append(getArchetypeDescriptor(Archetypes.MERCENARY,"Precision & Agility")),
                offset.append(getArchetypeDescriptor(Archetypes.ROGUE,"Vanish into shadow")),
                offset.append(getArchetypeDescriptor(Archetypes.SORCERER,"Master of elements")),
                offset.append(getArchetypeDescriptor(Archetypes.TEMPLAR,"Divine protection & might")),
                Component.text(""),
                selectLine
        );
        int archDescSlot = mapGridSlot(2,1);
        buttons[0] = new Button(archDescSlot, Material.NAME_TAG,1,false,descriptionButtonName,descriptionButtonDesc);
        buttons[0].setLeftClickFunction((player, gui) -> {
            List<Integer> archButtonSlots = new ArrayList<>();
            for (int i = 1; i <= 6; i++) {archButtonSlots.add(archDescSlot + i);}
            selectedArchetype = null;
            resetEnchantmentGlintFrom(archButtonSlots);
            gui.update();
            SoundAPI.playGenericSoundAtLocation(Audience.audience(player),player.getLocation(),"entity.experience_orb.pickup", 0.6f,0.3f);
        });
        Component STR = Component.text("STR").color(NamedTextColor.RED).decorate(TextDecoration.BOLD);
        Component DEX = Component.text("DEX").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD);
        Component INT = Component.text("INT").color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD);


        buttons[1] = generateArchetypeSelectionButton(Archetypes.MARAUDER,offset.append(STR),Material.SHEARS,archDescSlot);
        buttons[2] = generateArchetypeSelectionButton(Archetypes.GLADIATOR,offset.append(STR).append(divisor).append(DEX),Material.SHEARS,archDescSlot);
        buttons[3] = generateArchetypeSelectionButton(Archetypes.MERCENARY,offset.append(DEX),Material.BOW,archDescSlot);
        buttons[4] = generateArchetypeSelectionButton(Archetypes.ROGUE,offset.append(DEX).append(divisor).append(INT),Material.SHEARS,archDescSlot);
        buttons[5] = generateArchetypeSelectionButton(Archetypes.SORCERER,offset.append(INT),Material.BOW,archDescSlot);
        buttons[6] = generateArchetypeSelectionButton(Archetypes.TEMPLAR,offset.append(INT).append(divisor).append(STR),Material.SHEARS,archDescSlot);


        return buttons;
    }
    private Button generateArchetypeSelectionButton(Archetypes archetype, Component appendage,Material material, int descIconSlot){
        int rowOffset = archetype.ordinal();
        Component name = Component.text(Utils.convertToPrettyString(archetype.name())).color(archetype.getColorOnPalette().getColor()).decorate(TextDecoration.BOLD);
        name = name.append(appendage);
        List<Integer> archButtonSlots = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {archButtonSlots.add(descIconSlot + i);}
        Button archButton = new Button(descIconSlot+rowOffset, material,1,false,
                name,List.of(
                Component.text("Archetype").color(InscriptedPalette.DARK_GRAY.getColor()),
                Component.text(""),
                Component.text(" >| " + Utils.convertToPrettyString(archetype.getArmorType().name()) + " equipment")
                        .color(InscriptedPalette.DARK_GRAY.getColor()).decoration(TextDecoration.ITALIC, false),
                Component.text(" >| " + Utils.convertToPrettyString(archetype.getWeaponType().name()+"S"))
                        .color(InscriptedPalette.DARK_GRAY.getColor()).decoration(TextDecoration.ITALIC, false),
                Component.text(""),
                Component.text("Click to Select").color(NamedTextColor.GREEN)
        ));
        archButton.setLeftClickFunction(
                (player, gui) -> {
                    setSelectedArchetype(archetype);
                    resetEnchantmentGlintFrom(archButtonSlots);
                    getGUISlot(descIconSlot+rowOffset).applyGlint();
                    gui.update();
                    SoundAPI.playGenericSoundAtLocation(Audience.audience(player),player.getLocation(),"entity.experience_orb.pickup", 0.5f,0.8f);
                }
        );
        archButton.getIcon().editMeta(itemMeta -> itemMeta.setCustomModelData(archetype.ordinal()));
        return archButton;
    }
    private Component getArchetypeDescriptor(Archetypes archetype, String descr){
        return Component.text(Utils.convertToPrettyString(archetype.name())).color(archetype.getColorOnPalette().getColor()).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC,false)
                .append(Component.text(Utils.convertToPrettyString(" >| "+descr+"  ")).color(InscriptedPalette.DARK_GRAY.getColor())
                        .decorate(TextDecoration.ITALIC).decoration(TextDecoration.BOLD,false));
    }

    private Component getButtonName(String name, String hexColor){
        return Component.text(Utils.convertToPrettyString(name)).color(TextColor.fromHexString(hexColor)).decorate(TextDecoration.BOLD);
    }

    private void resetEnchantmentGlintFrom(List<Integer> buttonSlots){
        for (int slot : buttonSlots){
            Button b = getGUISlot(slot);
            if (b == null){continue;}
            b.removeGlint();
        }
    }
}
