package earth2b2t.anarchychat.command;

import earth2b2t.anarchychat.ignore.IgnorePlayer;
import earth2b2t.anarchychat.ignore.IgnorePlayerRepository;
import earth2b2t.anarchychat.ignore.IgnoreType;
import earth2b2t.anarchychat.mute.MutePlayerRepository;
import earth2b2t.i18n.BukkitI18n;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.Arrays;

@RequiredArgsConstructor
public class TellCommand implements CommandExecutor {

    private static final BukkitI18n i18n = BukkitI18n.get(TellCommand.class);
    private final IgnorePlayerRepository ignorePlayerRepository;
    private final MutePlayerRepository mutePlayerRepository;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            i18n.print(sender, "anarchychat.console-not-supported");
            return true;
        }

        if (args.length < 1) {
            i18n.print(sender, "anarchychat.tell.player-not-specified");
            return true;
        }

        if (args.length < 2) {
            i18n.print(sender, "anarchychat.tell.message-missing");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            i18n.print(sender, "anarchychat.tell.player-offline", args[0]);
            return true;
        }

        IgnorePlayer ignorePlayer = ignorePlayerRepository.findByPlayer(target);
        IgnoreType ignoreType = ignorePlayer.getIgnoreType(sender.getName());
        if (ignoreType != null && !(sender instanceof ConsoleCommandSender)) {
            i18n.print(sender, "anarchychat.tell.player-ignored", args[0]);
            return true;
        }

        boolean muted = mutePlayerRepository.findByPlayer(player).isPrivateMuted();
        if (muted) {
            i18n.print(sender, "anarchychat.tell.player-muted");
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        i18n.print(sender, "anarchychat.tell.message-sent", args[0], message);
        i18n.print(target, "anarchychat.tell.message-received", player.getName(), message);

        ignorePlayer.setLastMessageReceivedAt(LocalDateTime.now());
        ignorePlayer.setLastMessageSentBy(sender.getName());

        return true;
    }
}
