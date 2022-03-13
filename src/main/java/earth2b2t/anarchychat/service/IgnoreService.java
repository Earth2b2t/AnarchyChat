package earth2b2t.anarchychat.service;

import earth2b2t.anarchychat.ignore.IgnorePlayer;
import earth2b2t.anarchychat.ignore.IgnorePlayerRepository;
import earth2b2t.anarchychat.ignore.IgnoreType;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@RequiredArgsConstructor
public class IgnoreService implements Service {

    private final IgnorePlayerRepository ignorePlayerRepository;

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.getRecipients().removeIf(it -> {
            IgnorePlayer ignorePlayer = ignorePlayerRepository.findByPlayer(it);
            IgnoreType ignoreType = ignorePlayer.getIgnoreType(e.getPlayer().getName());
            return ignoreType != null;
        });
    }
}
