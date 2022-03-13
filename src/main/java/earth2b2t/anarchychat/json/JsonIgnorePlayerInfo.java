package earth2b2t.anarchychat.json;

import earth2b2t.anarchychat.ignore.Ignore;
import earth2b2t.anarchychat.ignore.IgnoreType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class JsonIgnorePlayerInfo {
    private final UUID uniqueId;
    private final String name;
    private final List<String> ignoreList;

    public JsonIgnorePlayerInfo(JsonIgnorePlayer player) {
        this(player.getUniqueId(), player.getName(),
                player.getIgnoreList().stream()
                        .filter(it -> it.getIgnoreType() == IgnoreType.HARD)
                        .map(Ignore::getName)
                        .toList());
    }

    public JsonIgnorePlayer toJsonignorePlayer(JsonIgnorePlayerRepository ignorePlayerRepository) {
        return new JsonIgnorePlayer(ignorePlayerRepository, uniqueId, name,
                ignoreList.stream().map(it -> new Ignore(it, IgnoreType.HARD)).toList());
    }
}
