package earth2b2t.anarchychat.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import earth2b2t.anarchychat.ignore.IgnorePlayer;
import earth2b2t.anarchychat.ignore.IgnorePlayerRepository;
import earth2b2t.anarchychat.utils.LocalDateTimeTypeAdapter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonIgnorePlayerRepository implements IgnorePlayerRepository, Listener {

    private final Map<Player, IgnorePlayer> ignorePlayerMap = Collections.synchronizedMap(new HashMap<>());
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .setPrettyPrinting()
            .create();

    private final Plugin plugin;
    private final Path dataFolder;

    @Override
    public IgnorePlayer findByPlayer(Player player) {
        return ignorePlayerMap.get(player);
    }

    private Path getPath(UUID uuid) {
        return dataFolder.resolve(uuid + ".json");
    }

    private String getLock(UUID uuid) {
        return (getClass().getSimpleName() + "-" + uuid).intern();
    }

    public JsonIgnorePlayer load(UUID uuid, String name) {
        synchronized (getLock(uuid)) {
            // load player file
            try {
                Path path = getPath(uuid);
                Files.createDirectories(dataFolder);
                if (Files.exists(path)) {
                    return gson.fromJson(Files.readString(path), JsonIgnorePlayerInfo.class).toJsonignorePlayer(this);
                } else {
                    return new JsonIgnorePlayer(this, uuid, name, new ArrayList<>());
                }
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }

    public void save(JsonIgnorePlayer ignorePlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            synchronized (getLock(ignorePlayer.getUniqueId())) {
                try {
                    Files.createDirectories(dataFolder);
                    Files.writeString(getPath(ignorePlayer.getUniqueId()), gson.toJson(new JsonIgnorePlayerInfo(ignorePlayer)));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        ignorePlayerMap.put(e.getPlayer(), load(e.getPlayer().getUniqueId(), e.getPlayer().getName()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        ignorePlayerMap.remove(e.getPlayer());
    }

    public static JsonIgnorePlayerRepository create(Plugin plugin, Path dataFolder) {
        JsonIgnorePlayerRepository ignorePlayerRepository = new JsonIgnorePlayerRepository(plugin, dataFolder);
        Bukkit.getPluginManager().registerEvents(ignorePlayerRepository, plugin);

        for (Player player : Bukkit.getOnlinePlayers()) {
            JsonIgnorePlayer ignorePlayer = ignorePlayerRepository.load(player.getUniqueId(), player.getName());
            ignorePlayerRepository.ignorePlayerMap.put(player, ignorePlayer);
        }

        return ignorePlayerRepository;
    }

}
