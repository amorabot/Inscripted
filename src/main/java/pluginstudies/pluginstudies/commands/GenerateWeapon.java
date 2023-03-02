package pluginstudies.pluginstudies.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pluginstudies.pluginstudies.PluginStudies;
import pluginstudies.pluginstudies.components.CraftableWeapon;

public class GenerateWeapon implements CommandExecutor {

    private PluginStudies plugin;
    public GenerateWeapon(PluginStudies plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR){
            player.getInventory().addItem(new CraftableWeapon(Integer.parseInt(args[0]), Integer.parseInt(args[1])).generateItem(plugin));
            return true;
        }

        return false;
    }
}
