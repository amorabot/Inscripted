package pluginstudies.pluginstudies.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pluginstudies.pluginstudies.PluginStudies;
import pluginstudies.pluginstudies.components.PlayerComponents.Attributes;

import static pluginstudies.pluginstudies.utils.Utils.log;

public class ResetAttributes implements CommandExecutor {

    private PluginStudies plugin;

    public ResetAttributes(PluginStudies plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            return false;
        }
        Player player = (Player) sender;
        plugin.getProfileManager().getPlayerProfile(player.getUniqueId()).setAttributes(new Attributes(10, 0, 0, 0));
        log("resetting " + player.getDisplayName()+ "'s profile.");
        plugin.getProfileManager().saveProfile(player.getUniqueId());
        return true;
    }
}
