package earth2b2t.anarchychat.h2;

import earth2b2t.anarchychat.ignore.Ignore;
import earth2b2t.anarchychat.ignore.IgnorePlayer;
import earth2b2t.anarchychat.ignore.IgnoreType;
import earth2b2t.anarchychat.mute.MutePlayer;
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
public class H2Player implements IgnorePlayer, MutePlayer {


    private final H2PlayerRepository h2PlayerRepository;
    private final UUID uniqueId;
    private final String name;
    private final List<Ignore> ignoreList;
    private final Map<String, Ignore> nameIndex;
    private String lastMessageSentBy;
    private LocalDateTime lastMessageReceivedAt;
    private boolean globalMuted;
    private boolean privateMuted;

    public H2Player(H2PlayerRepository h2PlayerRepository, UUID uniqueId, String name,
                    boolean globalMuted, boolean privateMuted, List<Ignore> ignoreList) {
        this.h2PlayerRepository = h2PlayerRepository;
        this.uniqueId = uniqueId;
        this.name = name;
        this.globalMuted = globalMuted;
        this.privateMuted = privateMuted;
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

        h2PlayerRepository.setIgnoreType(this, name, ignoreType);
    }

    public boolean isGlobalMuted() {
        return globalMuted;
    }

    public void setGlobalMuted(boolean globalMuted) {
        this.globalMuted = globalMuted;
        h2PlayerRepository.setGlobalMuted(this, globalMuted);
    }

    public boolean isPrivateMuted() {
        return privateMuted;
    }

    public void setPrivateMuted(boolean privateMuted) {
        this.privateMuted = privateMuted;
        h2PlayerRepository.setPrivateMuted(this, privateMuted);
    }
}
