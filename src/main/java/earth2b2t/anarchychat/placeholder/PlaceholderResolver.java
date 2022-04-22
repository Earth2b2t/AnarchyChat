package earth2b2t.anarchychat.placeholder;

import org.bukkit.entity.Player;

/**
 * Resolves placeholders.
 * All methods in this class must be thread-safe.
 */
@FunctionalInterface
public interface PlaceholderResolver {

    /**
     * Resolves a placeholder.
     *
     * @param player      a player requesting this placeholder
     * @param placeholder placeholder
     * @return resolved {@link String}, or {@code null} if appropriate placeholder was not found
     */
    String resolve(Player player, String placeholder);
}
