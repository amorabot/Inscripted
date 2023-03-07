package pluginstudies.pluginstudies.commands;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pluginstudies.pluginstudies.Crafting.Weapons.AxeAffixes;
import pluginstudies.pluginstudies.CustomDataTypes.ModifierInfoDataType;
import pluginstudies.pluginstudies.CustomDataTypes.ModifierInformation;
import pluginstudies.pluginstudies.PluginStudies;
import pluginstudies.pluginstudies.components.CraftableWeapon;
import pluginstudies.pluginstudies.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static pluginstudies.pluginstudies.utils.Utils.color;
import static pluginstudies.pluginstudies.utils.Utils.log;

public class Identify implements CommandExecutor {

    private PluginStudies plugin;

    public Identify(PluginStudies plugin){
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
            String ilvl = dataContainer.get(new NamespacedKey(plugin, "ilvl"), PersistentDataType.INTEGER).toString();
            List<String> lore = new ArrayList<>();
            int modifiers = dataContainer.get(new NamespacedKey(plugin,"modifiers"), PersistentDataType.INTEGER);

            switch (dataContainer.get(new NamespacedKey(plugin, "rarity"), PersistentDataType.INTEGER)){
                case 0:
                    player.sendMessage("Common items cannot be identified.");
                    break;
                case 1:
                    heldItemMeta.setDisplayName(color("&9&lMagic item"));
                    if (dataContainer.get(new NamespacedKey(plugin, "type"), PersistentDataType.STRING).equalsIgnoreCase("WEAPON")){
                        // Garantimos que é uma weapon magica
                        int[] dmgRange = dataContainer.get(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY);
                        lore.add(color("&c DMG: " + dmgRange[0] + "-" + dmgRange[1]));
                        lore.add(color("&7"));
                    } else {
                        //se não é arma, é armadura
                    }
                    lore.add(color("&7Item level: " + "&6&l" + ilvl));
                    lore.add(color("&7&l________________")); //-------------------------------------------
                    if (dataContainer.has(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType())){
                        ModifierInformation modInfo = dataContainer.get(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType());
                        for (String affix : modInfo.getModifierNames()){
                            int[] affixValue = modInfo.getMappedModifiers().get(affix);

                            if (affixValue.length == 2){ //Se forem 2 valores:
                                lore.add(color("&b+" + affixValue[0] + "-" + affixValue[1] + ": &7" + affix));
                            } else {
                                //Se tiver apenas 1 valor:
                                if (affix.contains("%")){ //Se o valor é uma porcentagem:
                                    String name = affix.replace("%", "");
                                    lore.add(color("&b+" + affixValue[0] + "%: &7" + name));
                                } else {
                                    lore.add(color("&b+" + affixValue[0] + ": &7" + affix));
                                }
                            }
                        }
                    }

                    if (dataContainer.has(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY)){
                        if (modifyBaseDmg(dataContainer)){
                            int[] dmgRange = dataContainer.get(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY);
                            String dmgLine = color("&c DMG: " + "&l" + dmgRange[0] + "-" + dmgRange[1]);
                            lore.set(0, dmgLine);
                        }
                    }
                    if (dataContainer.has(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType())){ //ADIÇÃO DE DANOS ELEMENTAIS ABAIXO DO DMG
                        ModifierInformation modInfo = dataContainer.get(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType());
                        if (modInfo != null){
                            String fireDmgMod = AxeAffixes.PREFIXES.getAffixList()[2];
                            boolean hasFireDmg = modInfo.getModifierNames().contains(fireDmgMod);
                            if (hasFireDmg){
                                int[] fireDmg = modInfo.getMappedModifiers().get(fireDmgMod);
                                // (FIRE DMG) TODO: adicionar os outros tipos de dano elemental (switch com as armas)
                                lore.add(1, color("       &4" + fireDmg[0] + "-" + fireDmg[1]));
                            }
                        }
                    }
                    heldItemMeta.setLore(lore);
                    break;
                case 2:
                    heldItemMeta.setDisplayName(color("&e&lRare item"));
                    if (dataContainer.get(new NamespacedKey(plugin, "type"), PersistentDataType.STRING).equalsIgnoreCase("WEAPON")){
                        // Garantimos que é uma weapon magica
                        int[] dmgRange = dataContainer.get(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY);
                        lore.add(color("&c DMG: " + dmgRange[0] + "-" + dmgRange[1]));
                        lore.add(color("&7"));
                    } else {
                        //se não é arma, é armadura
                    }
                    lore.add(color("&7Item level: " + "&6&l" + ilvl));
                    lore.add(color("&7&l________________"));
                    if (dataContainer.has(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType())){
                        ModifierInformation modInfo = dataContainer.get(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType());
                        for (String affix : modInfo.getModifierNames()){
                            int[] affixValue = modInfo.getMappedModifiers().get(affix);

                            if (affixValue.length == 2){ //Se forem 2 valores:
                                lore.add(color("&b+" + affixValue[0] + "-" + affixValue[1] + ": &7" + affix));
                            } else {
                                //Se tiver apenas 1 valor:
                                if (affix.contains("%")){ //Se o valor é uma porcentagem:
                                    String name = affix.replace("%", "");
                                    lore.add(color("&b+" + affixValue[0] + "%: &7" + name));
                                } else {
                                    lore.add(color("&b+" + affixValue[0] + ": &7" + affix));
                                }
                            }
                        }
                    }

                    if (dataContainer.has(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY)){
                        if (modifyBaseDmg(dataContainer)){
                            int[] dmgRange = dataContainer.get(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY);
                            String dmgLine = color("&c DMG: " + "&l" + dmgRange[0] + "-" + dmgRange[1]);
                            lore.set(0, dmgLine);
                        }
                    }
                    if (dataContainer.has(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType())){ //ADIÇÃO DE DANOS ELEMENTAIS ABAIXO DO DMG
                        ModifierInformation modInfo = dataContainer.get(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType());
                        if (modInfo != null){
                            String fireDmgMod = AxeAffixes.PREFIXES.getAffixList()[2];
                            boolean hasFireDmg = modInfo.getModifierNames().contains(fireDmgMod);
                            if (hasFireDmg){
                                int[] fireDmg = modInfo.getMappedModifiers().get(fireDmgMod);
                                // (FIRE DMG) TODO: adicionar os outros tipos de dano elemental (switch com as armas)
                                lore.add(1, color("       &4" + fireDmg[0] + "-" + fireDmg[1]));
                            }
                        }
                    }

                    heldItemMeta.setLore(lore);
                    break;
            }
            dataContainer.set(new NamespacedKey(plugin, "state"), PersistentDataType.STRING, "IDED");
        }
        heldItem.setItemMeta(heldItemMeta);

        return true;
    }

    private boolean modifyBaseDmg(PersistentDataContainer heldItemDataContainer){
        boolean modified = false;
        if(heldItemDataContainer.has(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType())){
            ModifierInformation modInfo = heldItemDataContainer.get(
                    new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType() );
            if (modInfo == null){
                return false;
            }
            String flatPhys = AxeAffixes.PREFIXES.getAffixList()[0];
            String percentPhys = AxeAffixes.PREFIXES.getAffixList()[1];
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
}
