package pluginstudies.pluginstudies.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pluginstudies.pluginstudies.RPGElements;
import pluginstudies.pluginstudies.components.PlayerComponents.Attributes;

import static pluginstudies.pluginstudies.utils.Utils.log;

public class ResetAttributes implements CommandExecutor {

    private RPGElements plugin;

    public ResetAttributes(RPGElements plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            return false;
        }
        Player player = (Player) sender;
        //TODO: implementar reset de perfis
        return true;
    }
}
