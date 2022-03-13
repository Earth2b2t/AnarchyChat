package earth2b2t.anarchychat.command;

import earth2b2t.anarchychat.ignore.IgnorePlayer;
import earth2b2t.anarchychat.ignore.IgnorePlayerRepository;
import earth2b2t.anarchychat.ignore.IgnoreType;
import earth2b2t.i18n.BukkitI18n;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class IgnoreCommand implements CommandExecutor {

    private static final BukkitI18n i18n = BukkitI18n.get(IgnoreCommand.class);
    private final IgnorePlayerRepository ignorePlayerRepository;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            i18n.print(sender, "anarchychat.console-not-supported");
            return true;
        }

        if (args.length < 1) {
            i18n.print(sender, "anarchychat.ignore.player-not-specified");
            return true;
        }
        if (args[0].length() > 16) {
            i18n.print(sender, "anarchychat.ignore.player-name-too-long");
        }

        IgnorePlayer ignorePlayer = ignorePlayerRepository.findByPlayer(player);

        IgnoreType ignoreType;
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("hard")) {
                ignoreType = IgnoreType.HARD;
            } else if (args[1].equalsIgnoreCase("soft")) {
                ignoreType = IgnoreType.SOFT;
            } else {
                i18n.print(sender, "anarchychat.ignore.invalid-ignore-type");
                return true;
            }
        } else {
            if (ignorePlayer.getIgnoreType(args[0]) == null) {
                ignoreType = IgnoreType.SOFT;
            } else {
                ignoreType = null;
            }
        }

        ignorePlayer.setIgnoreType(args[0], ignoreType);
        if (ignoreType == null) {
            i18n.print(sender, "anarchychat.ignore.player-unignored", args[0]);
        } else if (ignoreType == IgnoreType.HARD) {
            i18n.print(sender, "anarchychat.ignore.player-permanently-ignored", args[0]);
        } else {
            i18n.print(sender, "anarchychat.ignore.player-ignored", args[0]);
        }

        return true;
    }
}
