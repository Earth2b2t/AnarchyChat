package earth2b2t.anarchychat.service;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;

public class TellPreventService implements Service {

    private static final List<String> VANILLA_TELL_COMMANDS = List.of(
            "/minecraft:msg",
            "/minecraft:tell",
            "/minecraft:w"
    );

    @EventHandler
    public void onAsyncPlayerChat(PlayerCommandPreprocessEvent e) {

        String msg = e.getMessage();
        if (!VANILLA_TELL_COMMANDS.contains(msg)) return;

        String[] split = msg.split(" ");
        String args = String.join(" ", Arrays.copyOfRange(split, 1, split.length));

        e.setCancelled(true);
        e.getPlayer().chat("/msg " + args);
    }
}
