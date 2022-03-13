package earth2b2t.anarchychat.command;

import earth2b2t.i18n.BukkitI18n;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IgnoreLangCommand implements CommandExecutor {

    private static final BukkitI18n i18n = BukkitI18n.get(IgnoreLangCommand.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            i18n.print(sender, "anarchychat.console-not-supported");
            return true;
        }

        if (args.length < 1) {
            i18n.print(sender, "anarchychat.lang.language-not-specified");
            return true;
        }
        i18n.getLanguageProvider().update(player.getUniqueId(), args[0]);

        boolean quiet = args.length > 1 && args[1].equalsIgnoreCase("quiet");
        if (!quiet) {
            i18n.print(sender, "anarchychat.lang.language-updated");
        }
        return true;
    }
}
