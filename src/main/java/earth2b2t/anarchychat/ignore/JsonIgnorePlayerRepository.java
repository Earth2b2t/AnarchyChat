package earth2b2t.anarchychat.ignore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    private final Map<Player, IgnorePlayer> chatPlayerMap = Collections.synchronizedMap(new HashMap<>());
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .setPrettyPrinting()
            .create();

    private final Plugin plugin;
    private final Path dataFolder;

    public static JsonIgnorePlayerRepository create(Plugin plugin, Path dataFolder) {
        JsonIgnorePlayerRepository chatPlayerRepository = new JsonIgnorePlayerRepository(plugin, dataFolder);
        Bukkit.getPluginManager().registerEvents(chatPlayerRepository, plugin);

        for (Player player : Bukkit.getOnlinePlayers()) {
            JsonIgnorePlayer chatPlayer = chatPlayerRepository.load(player.getUniqueId(), player.getName());
            chatPlayerRepository.chatPlayerMap.put(player, chatPlayer);
        }

        return chatPlayerRepository;
    }

    @Override
    public IgnorePlayer findByPlayer(Player player) {
        return chatPlayerMap.get(player);
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
                    return gson.fromJson(Files.readString(path), JsonChatPlayerInfo.class).toJsonChatPlayer(this);
                } else {
                    return new JsonIgnorePlayer(this, uuid, name, new ArrayList<>());
                }
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }

    public void save(JsonIgnorePlayer chatPlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            synchronized (getLock(chatPlayer.getUniqueId())) {
                try {
                    Files.createDirectories(dataFolder);
                    Files.writeString(getPath(chatPlayer.getUniqueId()), gson.toJson(new JsonChatPlayerInfo(chatPlayer)));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        chatPlayerMap.put(e.getPlayer(), load(e.getPlayer().getUniqueId(), e.getPlayer().getName()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        chatPlayerMap.remove(e.getPlayer());
    }
}
