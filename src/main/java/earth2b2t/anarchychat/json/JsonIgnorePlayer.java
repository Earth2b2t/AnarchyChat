package earth2b2t.anarchychat.json;

import earth2b2t.anarchychat.ignore.Ignore;
import earth2b2t.anarchychat.ignore.IgnorePlayer;
import earth2b2t.anarchychat.ignore.IgnoreType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class JsonIgnorePlayer implements IgnorePlayer {

    private final JsonIgnorePlayerRepository ignorePlayerRepository;
    private final UUID uniqueId;
    private final String name;
    private final List<Ignore> ignoreList;
    private final Map<String, Ignore> nameIndex;
    private String lastMessageSentBy;
    private LocalDateTime lastMessageReceivedAt;

    public JsonIgnorePlayer(JsonIgnorePlayerRepository ignorePlayerRepository, UUID uniqueId, String name, List<Ignore> ignoreList) {
        this.ignorePlayerRepository = ignorePlayerRepository;
        this.uniqueId = uniqueId;
        this.name = name;
        this.ignoreList = Collections.synchronizedList(new ArrayList<>(ignoreList));
        this.nameIndex = Collections.synchronizedMap(new HashMap<>());
        for (Ignore ignore : ignoreList) {
            nameIndex.put(ignore.getName(), ignore);
        }
    }

    public List<Ignore> getIgnoreList() {
        return Collections.unmodifiableList(ignoreList);
    }

    @Override
    public IgnoreType getIgnoreType(String name) {
        Ignore ignore = nameIndex.get(name);
        if (ignore == null) return null;
        else return ignore.getIgnoreType();
    }

    @Override
    public void setIgnoreType(String name, IgnoreType ignoreType) {
        Ignore value = nameIndex.get(name);

        ignoreList.remove(value);
        if (ignoreType == null) {
            nameIndex.remove(name);
        } else {
            Ignore ignore = new Ignore(name, ignoreType);

            ignoreList.add(ignore);
            nameIndex.put(name, ignore);
        }

        ignorePlayerRepository.save(this);
    }
}
