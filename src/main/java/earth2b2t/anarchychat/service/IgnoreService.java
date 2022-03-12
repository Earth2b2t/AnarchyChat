package earth2b2t.anarchychat.service;

import earth2b2t.anarchychat.player.ChatPlayer;
import earth2b2t.anarchychat.player.ChatPlayerRepository;
import earth2b2t.anarchychat.player.IgnoreType;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@RequiredArgsConstructor
public class IgnoreService implements Service {

    private final ChatPlayerRepository chatPlayerRepository;

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.getRecipients().removeIf(it -> {
            ChatPlayer chatPlayer = chatPlayerRepository.findByPlayer(it);
            IgnoreType ignoreType = chatPlayer.getIgnoreType(e.getPlayer().getName());
            return ignoreType != null;
        });
    }
}
