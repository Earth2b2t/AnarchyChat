package earth2b2t.anarchychat.ignore;

import org.bukkit.entity.Player;

public interface IgnorePlayerRepository {

    IgnorePlayer findByPlayer(Player player);
}
