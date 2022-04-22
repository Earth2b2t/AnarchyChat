package earth2b2t.anarchychat.service;

import earth2b2t.anarchychat.ignore.IgnorePlayer;
import earth2b2t.anarchychat.ignore.IgnorePlayerRepository;
import earth2b2t.anarchychat.ignore.IgnoreType;
import earth2b2t.anarchychat.mute.MutePlayerRepository;
import earth2b2t.anarchychat.placeholder.PlaceholderResolver;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class IgnoreService implements Service {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(.*?)}");
    private final IgnorePlayerRepository ignorePlayerRepository;
    private final MutePlayerRepository mutePlayerRepository;
    private final PlaceholderResolver placeholderResolver;
    private final String format;

    public String compileFormat(Player player) {
        String compiled = format
                .replace("{playerName}", "%1$s")
                .replace("{message}", "%2$s")
                .replace("{world}", player.getWorld().getName());

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(compiled);
        while (matcher.find()) {
            String group = matcher.group(1);
            String resolved = placeholderResolver.resolve(player, group);
            if (resolved != null) {
                compiled = compiled.replaceFirst("\\{" + group + "}", resolved);
            }
        }

        return compiled;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.getRecipients().removeIf(it -> {
            IgnorePlayer ignorePlayer = ignorePlayerRepository.findByPlayer(it);
            IgnoreType ignoreType = ignorePlayer.getIgnoreType(e.getPlayer().getName());

            boolean muted = mutePlayerRepository.findByPlayer(e.getPlayer()).isGlobalMuted();

            return e.getPlayer() != it && (ignoreType != null || muted);
        });

        e.setFormat(compileFormat(e.getPlayer()));
    }
}
