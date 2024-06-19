package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.DamageComponent;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Arrays;
import java.util.Map;

public class StatsCommand implements CommandExecutor {

    private Inscripted plugin;

    public StatsCommand(Inscripted plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            return true;
        }

        Player player = (Player) sender;
        Profile profile = JSONProfileManager.getProfile(player.getUniqueId());
        String[] defencesPrefixes = new String[DefenceTypes.values().length];
        String[] attackPrefixes = new String[DamageTypes.values().length];

        for (int i = 0; i < defencesPrefixes.length; i++){
            DefenceTypes currDef = DefenceTypes.values()[i];
            String defString = currDef.getTextColor() + currDef.getSpecialChar();
            defencesPrefixes[i] = defString;
        }
        for (int i = 0; i < attackPrefixes.length; i++){
            DamageTypes dmgDef = DamageTypes.values()[i];
            String atkString = dmgDef.getColor() + dmgDef.getCharacter();
            attackPrefixes[i] = atkString;
        }


        String defHeader = Utils.color("&b&f-------- " + DefenceTypes.ARMOR.getSpecialChar() + " Defences --------");
        String HP = ColorUtils.translateColorCodes(defencesPrefixes[0] + " " + (int)(profile.getHealthComponent().getMaxHealth()));
        String WARD = ColorUtils.translateColorCodes(defencesPrefixes[1] + " " + (int)(profile.getHealthComponent().getMaxWard()));

        DefenceComponent def = profile.getDefenceComponent();
        String FIRERES = ColorUtils.translateColorCodes(defencesPrefixes[2] + " " + def.getFireResistance());
        String LIGHTRES = ColorUtils.translateColorCodes(defencesPrefixes[3] + " " + def.getLightningResistance());
        String COLDRES = ColorUtils.translateColorCodes(defencesPrefixes[4] + " " + def.getColdResistance());
        String ABYSSRES = ColorUtils.translateColorCodes(defencesPrefixes[5] + " " + def.getAbyssalResistance());

        String ARMOR = ColorUtils.translateColorCodes(defencesPrefixes[6] + " " + (int)(def.getArmor()));
        String DODGE = ColorUtils.translateColorCodes(defencesPrefixes[7] + " " + (def.getDodge()));


        String atkHeader = Utils.color("&b&f-------- " + DamageTypes.PHYSICAL.getCharacter() + " Offence --------");
        DamageComponent dmg = profile.getDamageComponent();
        Map<DamageTypes, int[]> damages = dmg.getHitData().getDamages();
        int[] physRange = damages.get(DamageTypes.PHYSICAL);
        String PHYS = "";
        if (physRange[0] != 0 && physRange[1] != 0){
            PHYS = ColorUtils.translateColorCodes(attackPrefixes[0] + Arrays.toString(physRange));
        }
        int[] fireRange = damages.get(DamageTypes.FIRE);
        String FIRE = "";
        if (fireRange != null){
            FIRE = ColorUtils.translateColorCodes(" &f| " + attackPrefixes[1] + fireRange[0] + "-" + fireRange[1]);
        }
        int[] lightningRange = damages.get(DamageTypes.LIGHTNING);
        String LIGHTNING = "";
        if (lightningRange != null){
            LIGHTNING = ColorUtils.translateColorCodes(" &f| " + attackPrefixes[2] + lightningRange[0] + "-" + lightningRange[1]);
        }
        int[] coldRange = damages.get(DamageTypes.COLD);
        String COLD = "";
        if (coldRange != null){
            COLD = ColorUtils.translateColorCodes(" &f| " + attackPrefixes[3] + coldRange[0] + "-" + coldRange[1]);
        }
        int[] abyssRange = damages.get(DamageTypes.ABYSSAL);
        String ABYSS = "";
        if (abyssRange != null){
            ABYSS = ColorUtils.translateColorCodes(" &f| " + attackPrefixes[4] + abyssRange[0] + "-" + abyssRange[1]);
        }

        int indent = 6;

        Utils.msgPlayer(player, defHeader);
        player.sendMessage("");
        Utils.msgPlayer(player, (HP + " &f| " + WARD).indent(indent));
        player.sendMessage("");
        Utils.msgPlayer(player, (FIRERES + " &f| " + LIGHTRES + " &f| " + COLDRES + " &f| " + ABYSSRES).indent(indent));
        player.sendMessage("");
        Utils.msgPlayer(player, (ARMOR + " &f| " + DODGE).indent(indent));
        player.sendMessage("");
        Utils.msgPlayer(player, atkHeader);
        player.sendMessage("");
        Utils.msgPlayer(player, (PHYS + FIRE + LIGHTNING + COLD + ABYSS).indent(indent));
        player.sendMessage("");
        return true;
    }
}
