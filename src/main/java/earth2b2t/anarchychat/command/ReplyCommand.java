package earth2b2t.anarchychat.command;

import earth2b2t.anarchychat.player.ChatPlayer;
import earth2b2t.anarchychat.player.ChatPlayerRepository;
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
    private final ChatPlayerRepository chatPlayerRepository;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            i18n.print(sender, "anarchychat.console-not-supported");
            return true;
        }

        ChatPlayer chatPlayer = chatPlayerRepository.findByPlayer(player);
        if (chatPlayer.getLastMessageSentBy() == null ||
                chatPlayer.getLastMessageReceivedAt().plus(5, ChronoUnit.MINUTES).isBefore(LocalDateTime.now())) {
            i18n.print(sender, "anarchychat.reply.message-not-received");
            return true;
        }

        Bukkit.dispatchCommand(sender, "tell " + chatPlayer.getLastMessageSentBy() + " " + String.join(" ", args));

        return true;
    }
}
