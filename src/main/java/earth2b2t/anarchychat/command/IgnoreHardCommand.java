package earth2b2t.anarchychat.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class IgnoreHardCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String concat = args.length == 0 ? "" : String.join(" ", args);
        Bukkit.dispatchCommand(sender, "ignore " + concat + " hard");
        return true;
    }
}
