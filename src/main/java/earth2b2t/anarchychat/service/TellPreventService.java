package earth2b2t.anarchychat.service;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;

public class TellPreventService implements Service {

    @EventHandler
    public void onAsyncPlayerChat(PlayerCommandPreprocessEvent e) {

        String msg = e.getMessage();
        if (!msg.startsWith("/minecraft:msg") && !msg.startsWith("/minecraft:tell")) {
            return;
        }

        String[] split = msg.split(" ");
        String args = String.join(" ", Arrays.copyOfRange(split, 1, split.length));

        e.setCancelled(true);
        e.getPlayer().chat("/msg " + args);
    }
}
