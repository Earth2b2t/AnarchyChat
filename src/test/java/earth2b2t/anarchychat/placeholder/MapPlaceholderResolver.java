package earth2b2t.anarchychat.placeholder;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Map;

@RequiredArgsConstructor
public class MapPlaceholderResolver implements PlaceholderResolver {

    private final Map<String, String> placeholders;

    @Override
    public String resolve(Player player, String placeholder) {
        return placeholders.get(placeholder);
    }
}
