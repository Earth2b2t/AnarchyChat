package earth2b2t.anarchychat.command;

import earth2b2t.anarchychat.mute.MutePlayer;
import earth2b2t.anarchychat.mute.MutePlayerRepository;
import earth2b2t.i18n.BukkitI18n;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class MuteCommand implements CommandExecutor {

    private static final BukkitI18n i18n = BukkitI18n.get(MuteCommand.class);
    private final MutePlayerRepository mutePlayerRepository;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            i18n.print(sender, "anarchychat.gmute.player-not-specified");
            return true;
        }
        if (args[0].length() > 16) {
            i18n.print(sender, "anarchychat.gmute.player-name-too-long");
        }

        MutePlayer mutePlayer = mutePlayerRepository.findByName(args[0]).orElse(null);
        if (mutePlayer == null) {
            i18n.print(sender, "anarchychat.gmute.invalid-player");
            return true;
        }

        boolean global;
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("global")) {
                global = true;
            } else if (args[2].equalsIgnoreCase("private")) {
                global = false;
            } else {
                i18n.print(sender, "anarchychat.gmute.invalid-mute-type");
                return true;
            }
        } else {
            global = true;
        }

        if (global) {
            mutePlayer.setGlobalMuted(!mutePlayer.isGlobalMuted());
            if (mutePlayer.isGlobalMuted()) {
                i18n.print(sender, "anarchychat.gmute.player-global-muted", args[0]);
            } else {
                i18n.print(sender, "anarchychat.gmute.player-global-unmuted", args[0]);
            }
        } else {
            mutePlayer.setPrivateMuted(!mutePlayer.isPrivateMuted());
            if (mutePlayer.isGlobalMuted()) {
                i18n.print(sender, "anarchychat.gmute.player-private-muted", args[0]);
            } else {
                i18n.print(sender, "anarchychat.gmute.player-private-unmuted", args[0]);
            }
        }

        return true;
    }
}
