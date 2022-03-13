package earth2b2t.anarchychat.utils;

import earth2b2t.anarchychat.ignore.IgnorePlayer;
import earth2b2t.anarchychat.ignore.IgnorePlayerRepository;
import earth2b2t.anarchychat.mute.MutePlayer;
import earth2b2t.anarchychat.mute.MutePlayerRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

abstract public class CachedPlayerRepository<T extends IgnorePlayer & MutePlayer>
        implements IgnorePlayerRepository, MutePlayerRepository, Listener {

    protected final Map<Player, T> onlineCache = Collections.synchronizedMap(new HashMap<>());
    protected final Set<T> offlineCache = Collections.newSetFromMap(new WeakHashMap<>());

    @Override
    public T findByPlayer(Player player) {
        return onlineCache.get(player);
    }

    @Override
    public Optional<MutePlayer> findByUniqueId(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) return Optional.of(findByPlayer(player));
        T result = offlineCache.parallelStream()
                .filter(it -> it.getUniqueId().equals(uuid))
                .findAny().orElse(null);
        if (result == null) {
            result = load(uuid, null);
            offlineCache.add(result);
        }
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<MutePlayer> findByName(String name) {
        Player player = Bukkit.getPlayerExact(name);
        if (player != null) return Optional.of(findByPlayer(player));
        T result = offlineCache.parallelStream()
                .filter(it -> it.getName().equals(name))
                .findAny().orElse(null);
        if (result == null) {
            result = load(null, name);
            offlineCache.add(result);
        }
        return Optional.ofNullable(result);
    }

    protected void loadOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            onlineCache.put(player, load(player.getUniqueId(), player.getName()));
        }
    }

    abstract protected void updateName(Player player);

    abstract protected T load(UUID uuid, String name);

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        T value = offlineCache.stream()
                .filter(it -> it.getUniqueId().equals(e.getPlayer().getUniqueId()))
                .findAny().orElse(null);

        updateName(e.getPlayer());
        if (value == null) {
            onlineCache.put(e.getPlayer(), load(e.getPlayer().getUniqueId(), e.getPlayer().getName()));
        } else {
            offlineCache.remove(value);
            onlineCache.put(e.getPlayer(), value);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        onlineCache.remove(e.getPlayer());
    }
}
