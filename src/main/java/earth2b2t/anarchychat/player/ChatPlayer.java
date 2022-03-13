package earth2b2t.anarchychat.player;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ChatPlayer {

    UUID getUniqueId();

    String getName();

    List<Ignore> getIgnoreList();

    IgnoreType getIgnoreType(String name);

    void setIgnoreType(String name, IgnoreType ignoreType);

    String getLastMessageSentBy();

    void setLastMessageSentBy(String name);

    LocalDateTime getLastMessageReceivedAt();

    void setLastMessageReceivedAt(LocalDateTime localDateTime);
}
