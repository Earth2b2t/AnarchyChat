package earth2b2t.anarchychat.command;

import earth2b2t.anarchychat.player.ChatPlayer;
import earth2b2t.anarchychat.player.ChatPlayerRepository;
import earth2b2t.anarchychat.player.IgnoreType;
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
    private final ChatPlayerRepository chatPlayerRepository;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            i18n.print(sender, "anarchychat.tell.player-not-specified");
            return true;
        }

        if (args.length < 2) {
            i18n.print(sender, "anarchychat.tell.message-missing");
            return true;
        }

        Player player = Bukkit.getPlayerExact(args[0]);
        if (player == null) {
            i18n.print(sender, "anarchychat.tell.player-offline", args[0]);
            return true;
        }

        ChatPlayer chatPlayer = chatPlayerRepository.findByPlayer(player);
        IgnoreType ignoreType = chatPlayer.getIgnoreType(sender.getName());
        if (ignoreType != null && !(sender instanceof ConsoleCommandSender)) {
            i18n.print(sender, "anarchychat.tell.player-ignored", args[0]);
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        i18n.print(sender, "anarchychat.tell.message-sent", args[0], message);
        i18n.print(player, "anarchychat.tell.message-received", args[0], message);

        chatPlayer.setLastMessageReceivedAt(LocalDateTime.now());
        chatPlayer.setLastMessageSentBy(sender.getName());

        return true;
    }
}
