package earth2b2t.anarchychat.player;

import org.bukkit.entity.Player;

public interface ChatPlayerRepository {

    ChatPlayer findByPlayer(Player player);
}
