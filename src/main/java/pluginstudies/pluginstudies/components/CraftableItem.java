package pluginstudies.pluginstudies.components;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pluginstudies.pluginstudies.Crafting.Weapons.AxeAffixes;
import pluginstudies.pluginstudies.PluginStudies;
import pluginstudies.pluginstudies.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static pluginstudies.pluginstudies.utils.Utils.color;

public abstract class CraftableItem {

    protected boolean identified;
    protected int ilvl;
    protected int rarity; // 0 = Common, 1 = Magic, 2 = Rare, TODO: 3 = Unique
    protected int affixLimit;
    protected Material itemType;
    protected String implicit;
    protected String[] prefixes;
    protected String[] suffixes;
    protected String[][] affixes = {prefixes, suffixes};

    public ItemStack generateItem(PluginStudies plugin, int rarity){
        ItemStack item = null;
        switch(rarity){
            case 0:
                item =  generateCommon(plugin);
                break;
            case 1:
                item = generateMagic(plugin);
                break;
            case 2:
                item = generateRare(plugin);
                break;
        }
        return item;
    }

    private ItemStack generateCommon(PluginStudies plugin){
        setRarity(0);

        ItemStack item = new ItemStack(itemType);
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

        dataContainer.set(new NamespacedKey(plugin, "state"), PersistentDataType.STRING, "IDED");
        dataContainer.set(new NamespacedKey(plugin, "ilvl"), PersistentDataType.INTEGER, ilvl);
        dataContainer.set(new NamespacedKey(plugin, "rarity"), PersistentDataType.INTEGER, rarity);
        dataContainer.set(new NamespacedKey(plugin, "modifiers"), PersistentDataType.INTEGER, 0);

        itemMeta.setDisplayName(color("&f&lCommon item"));
        itemMeta.setLore(Arrays.asList(color("&7Item level: " + "&6&l" + ilvl)));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(itemMeta);
        return item;
    }
    private ItemStack generateMagic(PluginStudies plugin){
        setRarity(1);
        int modifiers = getRandomNumber(1, affixLimit);

        ItemStack item = new ItemStack(itemType);
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

        dataContainer.set(new NamespacedKey(plugin, "state"), PersistentDataType.STRING, "UNIDED");
        dataContainer.set(new NamespacedKey(plugin, "ilvl"), PersistentDataType.INTEGER, ilvl);
        dataContainer.set(new NamespacedKey(plugin, "rarity"), PersistentDataType.INTEGER, rarity);
        dataContainer.set(new NamespacedKey(plugin, "modifiers"), PersistentDataType.INTEGER, modifiers);
        for (int i = 0; i<modifiers; i++){ //ADICIONANDO STATS BASEADOS NO RNG
            String genericStatKey = String.format("stat%d",i);
            dataContainer.set(new NamespacedKey(plugin, genericStatKey), PersistentDataType.INTEGER, (int) (Math.random()*10));
        }

        //TESTING BLOCK
        List<String> affixes = selectAffixes(modifiers);
        for (String affix : affixes){
            Utils.log(affix);
        }

        itemMeta.setDisplayName(color("&4&lUnidentified item"));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        return item;
    }
    private ItemStack generateRare(PluginStudies plugin){
        setRarity(2);
        int modifiers = getRandomNumber(3, affixLimit);

        ItemStack item = new ItemStack(itemType);
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

        dataContainer.set(new NamespacedKey(plugin, "state"), PersistentDataType.STRING, "UNIDED");
        dataContainer.set(new NamespacedKey(plugin, "ilvl"), PersistentDataType.INTEGER, ilvl);
        dataContainer.set(new NamespacedKey(plugin, "rarity"), PersistentDataType.INTEGER, rarity);
        dataContainer.set(new NamespacedKey(plugin, "modifiers"), PersistentDataType.INTEGER, modifiers);
        for (int i = 0; i<modifiers; i++){ //ADICIONANDO STATS BASEADOS NO RNG
            String genericStatKey = String.format("stat%d",i);
            dataContainer.set(new NamespacedKey(plugin, genericStatKey), PersistentDataType.INTEGER, (int) (Math.random()*10));
        }

        itemMeta.setDisplayName(color("&4&lUnidentified item"));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        return item;
    }
    private Map<String, Integer[]> selectTiers(){
        return null;
    }
    private List<String> selectAffixes(int modifiers){ //TODO fazer o método de geração de affixes (testes com magic)
        List<String> selectedAffixes = new ArrayList<>();
        for (int i = 0; i < modifiers; i++){
            selectedAffixes.add(getRandomAffix(selectedAffixes));
        }
        return selectedAffixes;
    }
    private String getRandomAffix(List<String> selectedAffixes){
        String affix = "";

        double selector = Math.random();
        if (selector > 0.5){
            //Prefix
            boolean found = false;
            while (!found){
                int rng = (int) (Math.random() * (AxeAffixes.PREFIXES.getAffixList().length));
                String randomAffix = AxeAffixes.PREFIXES.getAffixList()[rng];
                if (!(selectedAffixes.contains(randomAffix))){
                    affix = randomAffix;
                    found = true;
                }
            }
        } else {
            //Suffix
            boolean found = false;
            while (!found){
                int rng = (int) (Math.random() * (AxeAffixes.SUFFIXES.getAffixList().length));
                String randomAffix = AxeAffixes.SUFFIXES.getAffixList()[rng];
                if (!(selectedAffixes.contains(randomAffix))){
                    affix = randomAffix;
                    found = true;
                }
            }
        }
        return affix;
    }
    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * ((max - min)+1)) + min);
    }
    private void setRarity(int rarity){
        this.rarity = rarity;
        switch(rarity){
            case 0:
                this.affixLimit = 0;
                break;
            case 1:
                this.affixLimit = 2;
                break;
            case 2:
                this.affixLimit = 6;
                break;
        }
    }
}