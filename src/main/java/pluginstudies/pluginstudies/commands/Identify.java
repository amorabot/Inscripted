package pluginstudies.pluginstudies.commands;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pluginstudies.pluginstudies.Crafting.Weapons.AxeAffixes;
import pluginstudies.pluginstudies.Crafting.Weapons.ShortswordAffixes;
import pluginstudies.pluginstudies.CustomDataTypes.ModifierInfoDataType;
import pluginstudies.pluginstudies.CustomDataTypes.ModifierInformation;
import pluginstudies.pluginstudies.RPGElements;

import java.util.ArrayList;
import java.util.List;

import static pluginstudies.pluginstudies.utils.Utils.color;

public class Identify implements CommandExecutor {

    private RPGElements plugin;

    public Identify(RPGElements plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            return true; //Se não é um player usando
        }
        Player player = (Player) sender;
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR){
            return true; // Se o player não estiver segurando nada
        }
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!heldItem.hasItemMeta()){
            return true; //Se ele estivar algo que não tenha ItemMeta (no caso, um persistentDataContainer)
        }
        ItemMeta heldItemMeta = heldItem.getItemMeta();
        PersistentDataContainer dataContainer = heldItemMeta.getPersistentDataContainer();

        if (dataContainer.has(new NamespacedKey(plugin, "state"), PersistentDataType.STRING)){
            if (!dataContainer.get(new NamespacedKey(plugin, "state"), PersistentDataType.STRING).equalsIgnoreCase("UNIDED")){
                player.sendMessage(color("&cThis item is already identified."));
                return true;
            }
//            String ilvl = dataContainer.get(new NamespacedKey(plugin, "ilvl"), PersistentDataType.INTEGER).toString();
            List<String> lore = new ArrayList<>();
//            int modifiers = dataContainer.get(new NamespacedKey(plugin,"modifiers"), PersistentDataType.INTEGER);

            switch (dataContainer.get(new NamespacedKey(plugin, "rarity"), PersistentDataType.INTEGER)){
                case 0:
                    player.sendMessage("Common items cannot be identified.");
                    break;
                case 1:
                    heldItemMeta.setDisplayName(color("&9&lMagic item"));
                    renderStats(dataContainer, lore);

                    if (dataContainer.has(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY)){ // If weapon:
                        String weaponType = dataContainer.get(new NamespacedKey(plugin, "weapon-type"), PersistentDataType.STRING);

                        heldItemMeta.setDisplayName(color("&9&lMagic " + weaponType.toLowerCase()));
                        apllyPhysicalMods(dataContainer, weaponType, lore);

                        addElementalDmg(dataContainer, lore);
                    }
                    heldItemMeta.setLore(lore);
                    break;
                case 2:
                    heldItemMeta.setDisplayName(color("&e&lRare item"));
                    renderStats(dataContainer, lore);

                    if (dataContainer.has(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY)){ // If weapon:
                        String weaponType = dataContainer.get(new NamespacedKey(plugin, "weapon-type"), PersistentDataType.STRING);

                        heldItemMeta.setDisplayName(color("&e&lRare " + weaponType.toLowerCase()));
                        apllyPhysicalMods(dataContainer, weaponType, lore);

                        addElementalDmg(dataContainer, lore);
                        //TODO: POISON E OUTROS TIPOS DE FLAT DMG
                    }
                    heldItemMeta.setLore(lore);
                    break;
            }
            dataContainer.set(new NamespacedKey(plugin, "state"), PersistentDataType.STRING, "IDED");
        }
        heldItem.setItemMeta(heldItemMeta);

        return true;
    }

    private boolean modifyBaseDmg(PersistentDataContainer heldItemDataContainer, String flatPhys, String percentPhys){
        boolean modified = false;
        if(heldItemDataContainer.has(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType())){
            ModifierInformation modInfo = heldItemDataContainer.get(
                    new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType() );
            if (modInfo == null){
                return false;
            }
            boolean hasFlatPhys = modInfo.getModifierNames().contains(flatPhys);
            boolean hasPercentPhys = modInfo.getModifierNames().contains(percentPhys);
            if (hasFlatPhys){ //se tem flat phys
                int[] baseDmg = heldItemDataContainer.get(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY);
                baseDmg[0] = baseDmg[0] + modInfo.getMappedModifiers().get(flatPhys)[0];
                baseDmg[1] = baseDmg[1] + modInfo.getMappedModifiers().get(flatPhys)[1];
                heldItemDataContainer.set(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY, baseDmg);
                modified = true;
            }
            if (hasPercentPhys){
                int[] baseDmg = heldItemDataContainer.get(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY);
                baseDmg[0] = baseDmg[0] * (1 + (modInfo.getMappedModifiers().get(percentPhys)[0]/100));
                baseDmg[1] = baseDmg[1] + (1 + (modInfo.getMappedModifiers().get(percentPhys)[0]/100));
                heldItemDataContainer.set(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY, baseDmg);
                modified = true;
            }
        }
        return modified;
    }
    private void apllyPhysicalMods(PersistentDataContainer dataContainer, String weaponType, List<String> lore){
        switch (weaponType){
            case "AXE":
                if (modifyBaseDmg(dataContainer, AxeAffixes.PREFIXES.getAffixNameArray()[0], AxeAffixes.PREFIXES.getAffixNameArray()[1])){
                    int[] dmgRange = dataContainer.get(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY);
                    String dmgLine = color("&c DMG: " + "&l" + dmgRange[0] + "-" + dmgRange[1]);
                    lore.set(0, dmgLine);
                }
                break;
            case "SHORTSWORD":
                if (modifyBaseDmg(dataContainer, ShortswordAffixes.PREFIXES.getAffixNameArray()[0], ShortswordAffixes.PREFIXES.getAffixNameArray()[1])){
                    int[] dmgRange = dataContainer.get(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY);
                    String dmgLine = color("&c DMG: " + "&l" + dmgRange[0] + "-" + dmgRange[1]);
                    lore.set(0, dmgLine);
                }
                break;
        }
    }
    private void addElementalDmg(PersistentDataContainer dataContainer, List<String> lore){
        String fireDmgMod = "Added fire DMG";
        String coldDmgMod = "Added cold DMG";
        String lighningDmgMod = "Added lightning DMG";
        //Por ora, nomenclatura dos MODS elementais de flat dmg deve seguir o padrão "Added element DMG".
        if (dataContainer.has(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType())){ //ADIÇÃO DE DANOS ELEMENTAIS ABAIXO DO DMG
            ModifierInformation modInfo = dataContainer.get(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType());
            if (modInfo != null){
                boolean hasFireDmg = modInfo.getModifierNames().contains(fireDmgMod);
                if (hasFireDmg){
                    int[] fireDmg = modInfo.getMappedModifiers().get(fireDmgMod);
                    lore.add(1, color("       &4" + fireDmg[0] + "-" + fireDmg[1]));
                }

                boolean hasColdDmg = modInfo.getModifierNames().contains(coldDmgMod);
                if (hasColdDmg){
                    int[] coldDmg = modInfo.getMappedModifiers().get(coldDmgMod);
                    lore.add(1, color("       &1" + coldDmg[0] + "-" + coldDmg[1]));
                }

                boolean hasLighningDmg = modInfo.getModifierNames().contains(lighningDmgMod);
                if (hasLighningDmg){
                    int[] lightningDmg = modInfo.getMappedModifiers().get(lighningDmgMod);
                    lore.add(1, color("       &e" + lightningDmg[0] + "-" + lightningDmg[1]));
                }
            }
        }
    }
    private void renderStats(PersistentDataContainer dataContainer, List<String> lore){
        if (dataContainer.get(new NamespacedKey(plugin, "type"), PersistentDataType.STRING).equalsIgnoreCase("WEAPON")){
            // Garantimos que é uma weapon magica
            int[] dmgRange = dataContainer.get(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY);
            lore.add(color("&c DMG: " + dmgRange[0] + "-" + dmgRange[1]));
            lore.add(color("&7"));
        } else {
            //se não é arma, é armadura
        }
        int ilvl = dataContainer.get(new NamespacedKey(plugin, "ilvl"), PersistentDataType.INTEGER);
        lore.add(color("&7    Ilvl: " + "&6&l" + ilvl + "&8&l    " + dataContainer.get(new NamespacedKey(plugin, "implicit"), PersistentDataType.STRING)));
        lore.add(color("&7&l___________________"));
//        lore.add(color("&7Item level: " + "&6&l" + ilvl)); //DISPLAY ANTIGO
//        lore.add(color("&7&l______" + "&7&l&n" + dataContainer.get(new NamespacedKey(plugin, "implicit"), PersistentDataType.STRING) +"&7&l______"));
        if (dataContainer.has(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType())){
            ModifierInformation modInfo = dataContainer.get(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType());
            for (String affix : modInfo.getModifierNames()){
                int[] affixValue = modInfo.getMappedModifiers().get(affix);

                if (affixValue.length == 2){ //Se forem 2 valores:
                    lore.add(color("&b +" + affixValue[0] + "-" + affixValue[1] + ": &7" + affix));
                } else {
                    //Se tiver apenas 1 valor:
                    if (affix.contains("%")){ //Se o valor é uma porcentagem:
                        String name = affix.replace("%", "");
                        lore.add(color("&b +" + affixValue[0] + "%: &7" + name));
                    } else {
                        lore.add(color("&b +" + affixValue[0] + ": &7" + affix));
                    }
                }
            }
        }
    }
//    switch (weaponType){
//        case "AXE": //Apply any axe-specific modifiers
//            break;
//        case "SHORTSWORD": //Apply any sword-specific mods.
//            break;
//    }
}