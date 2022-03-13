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
    private final List<String> ignoreList;

    public JsonChatPlayerInfo(JsonChatPlayer player) {
        this(player.getUniqueId(), player.getName(),
                player.getIgnoreList().stream()
                        .filter(it -> it.getIgnoreType() == IgnoreType.HARD)
                        .map(Ignore::getName)
                        .toList());
    }

    public JsonChatPlayer toJsonChatPlayer(JsonChatPlayerRepository chatPlayerRepository) {
        return new JsonChatPlayer(chatPlayerRepository, uniqueId, name,
                ignoreList.stream().map(it -> new Ignore(it, IgnoreType.HARD)).toList());
    }
}
