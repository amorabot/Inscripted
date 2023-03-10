package pluginstudies.pluginstudies.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pluginstudies.pluginstudies.PluginStudies;
import pluginstudies.pluginstudies.components.CraftableWeapon;
import pluginstudies.pluginstudies.managers.CraftingManager;

import static pluginstudies.pluginstudies.utils.Utils.color;

public class GenerateWeapon implements CommandExecutor {

    private PluginStudies plugin;
    private CraftingManager craftingManager;
    public GenerateWeapon(PluginStudies plugin){
        this.plugin = plugin;
        this.craftingManager = new CraftingManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 3 || (Integer.parseInt(args[1]) >3 || Integer.parseInt(args[1])<1)){
            player.sendMessage(color("&cInvalid command syntax"));
            player.sendMessage(color("&cCorrect syntax: /generateweapon ilvl [1,2,3] type"));
            return true;
        }
        //TODO check dos tipos de arma
        int ilvlArg = Integer.parseInt(args[0]);
        int rarityArg = Integer.parseInt(args[1]);
        String weaponType = args[2];

        if (player.getInventory().getItemInMainHand().getType() == Material.AIR){
            player.getInventory().addItem(craftingManager.generateWeapon(ilvlArg, rarityArg, weaponType));
            return true;
        }
        player.sendMessage(color("&cThis command must be used with a empty hand."));
        return false;
    }
}
