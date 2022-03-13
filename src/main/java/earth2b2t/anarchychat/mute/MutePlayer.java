package earth2b2t.anarchychat.mute;

import java.util.UUID;

public interface MutePlayer {

    UUID getUniqueId();

    String getName();

    boolean isGlobalMuted();

    void setGlobalMuted(boolean globalMuted);

    boolean isPrivateMuted();

    void setPrivateMuted(boolean privateMuted);
}
