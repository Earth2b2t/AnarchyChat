package earth2b2t.anarchychat.mute;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface MutePlayerRepository {

    Optional<MutePlayer> findByUniqueId(UUID uuid);

    Optional<MutePlayer> findByName(String name);

    Collection<MutePlayer> findAll();
}
