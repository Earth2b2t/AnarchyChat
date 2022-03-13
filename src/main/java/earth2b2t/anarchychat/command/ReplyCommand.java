package earth2b2t.anarchychat.command;

import earth2b2t.anarchychat.ignore.IgnorePlayer;
import earth2b2t.anarchychat.ignore.IgnorePlayerRepository;
import earth2b2t.i18n.BukkitI18n;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
public class ReplyCommand implements CommandExecutor {

    private static final BukkitI18n i18n = BukkitI18n.get(ReplyCommand.class);
    private final IgnorePlayerRepository ignorePlayerRepository;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            i18n.print(sender, "anarchychat.console-not-supported");
            return true;
        }

        IgnorePlayer ignorePlayer = ignorePlayerRepository.findByPlayer(player);
        if (ignorePlayer.getLastMessageSentBy() == null ||
                ignorePlayer.getLastMessageReceivedAt().plus(5, ChronoUnit.MINUTES).isBefore(LocalDateTime.now())) {
            i18n.print(sender, "anarchychat.reply.message-not-received");
            return true;
        }

        Bukkit.dispatchCommand(sender, "tell " + ignorePlayer.getLastMessageSentBy() + " " + String.join(" ", args));

        return true;
    }
}
