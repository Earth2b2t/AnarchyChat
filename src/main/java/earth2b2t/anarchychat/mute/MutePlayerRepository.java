package earth2b2t.anarchychat.mute;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MutePlayerRepository {

    MutePlayer findByPlayer(Player player);

    Optional<MutePlayer> findByUniqueId(UUID uuid);

    Optional<MutePlayer> findByName(String name);

    List<MutePlayer> findAll();
}
