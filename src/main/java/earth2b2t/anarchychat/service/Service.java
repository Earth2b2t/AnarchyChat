package earth2b2t.anarchychat.service;

import org.bukkit.event.Listener;

public interface Service extends Listener {

    default void onEnable() {
    }

    default void onTick(int tick) {
    }

    default void onDisable() {
    }
}
