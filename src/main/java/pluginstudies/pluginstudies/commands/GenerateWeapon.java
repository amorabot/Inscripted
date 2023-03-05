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
        if (args.length != 2){
            player.sendMessage(color("&cInvalid command syntax"));
            return true;
        }
        int ilvlArg = Integer.parseInt(args[0]);
        int rarityArg = Integer.parseInt(args[1]);

        if (player.getInventory().getItemInMainHand().getType() == Material.AIR){
//            player.getInventory().addItem(new CraftableWeapon(Integer.parseInt(args[0])).generateItem(plugin, Integer.parseInt(args[1])));
            player.getInventory().addItem(craftingManager.generateWeapon(ilvlArg, rarityArg));
            return true;
        }

        return false;
    }
}
