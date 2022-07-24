package earth2b2t.anarchychat.placeholder;

import org.bukkit.entity.Player;

/**
 * Does not resolve any placeholder, and only returns {@code null}.
 */
public class EmptyPlaceholderResolver implements PlaceholderResolver {

    @Override
    public String resolve(Player player, String placeholder) {
        return null;
    }
}
