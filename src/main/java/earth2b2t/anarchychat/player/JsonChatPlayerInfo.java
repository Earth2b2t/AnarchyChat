package earth2b2t.anarchychat.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class JsonChatPlayerInfo {
    private final UUID uniqueId;
    private final String name;
    private final List<Ignore> ignoreList;

    public JsonChatPlayerInfo(JsonChatPlayer player) {
        this(player.getUniqueId(), player.getName(), player.getIgnoreList());
    }

    public JsonChatPlayer toJsonChatPlayer(JsonChatPlayerRepository chatPlayerRepository) {
        return new JsonChatPlayer(chatPlayerRepository, uniqueId, name, ignoreList);
    }
}
