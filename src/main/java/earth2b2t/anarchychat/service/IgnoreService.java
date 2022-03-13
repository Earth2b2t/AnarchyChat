package earth2b2t.anarchychat.service;

import earth2b2t.anarchychat.ignore.IgnorePlayer;
import earth2b2t.anarchychat.ignore.IgnorePlayerRepository;
import earth2b2t.anarchychat.ignore.IgnoreType;
import earth2b2t.anarchychat.mute.MutePlayerRepository;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@RequiredArgsConstructor
public class IgnoreService implements Service {

    private final IgnorePlayerRepository ignorePlayerRepository;
    private final MutePlayerRepository mutePlayerRepository;

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.getRecipients().removeIf(it -> {
            IgnorePlayer ignorePlayer = ignorePlayerRepository.findByPlayer(it);
            IgnoreType ignoreType = ignorePlayer.getIgnoreType(e.getPlayer().getName());

            boolean muted = mutePlayerRepository.findByPlayer(e.getPlayer()).isGlobalMuted();

            return e.getPlayer() != it && (ignoreType != null || muted);
        });
    }
}
