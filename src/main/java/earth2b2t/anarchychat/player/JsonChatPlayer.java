package earth2b2t.anarchychat.player;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class JsonChatPlayer implements ChatPlayer {

    private final JsonChatPlayerRepository chatPlayerRepository;
    private final UUID uniqueId;
    private final String name;
    private final List<Ignore> ignoreList;
    private final HashMap<String, Ignore> nameIndex;
    private String lastMessageSentBy;
    private LocalDateTime lastMessageReceivedAt;

    public JsonChatPlayer(JsonChatPlayerRepository chatPlayerRepository, UUID uniqueId, String name, List<Ignore> ignoreList) {
        this.chatPlayerRepository = chatPlayerRepository;
        this.uniqueId = uniqueId;
        this.name = name;
        this.ignoreList = new ArrayList<>(ignoreList);
        this.nameIndex = new HashMap<>();
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

        chatPlayerRepository.save(this);
    }
}
