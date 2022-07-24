package earth2b2t.anarchychat.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Uses PlaceholderAPI to resolve placeholders.
 */
public class PlaceholderApiResolver implements PlaceholderResolver {

    @Override
    public String resolve(Player player, String placeholder) {
        String str = "%" + placeholder + "%";
        String resolved = PlaceholderAPI.setPlaceholders(player, str);
        if (str.equals(resolved)) return null;
        else return resolved;
    }

    public static boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }
}
