package earth2b2t.anarchychat;

import earth2b2t.anarchychat.command.IgnoreCommand;
import earth2b2t.anarchychat.command.IgnoreHardCommand;
import earth2b2t.anarchychat.command.IgnoreListCommand;
import earth2b2t.anarchychat.player.JsonChatPlayerRepository;
import earth2b2t.i18n.BukkitI18n;
import org.bukkit.plugin.java.JavaPlugin;

public class AnarchyChatPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        BukkitI18n i18n = BukkitI18n.get(this);
        i18n.setDefaultLanguage("ja_jp");

        JsonChatPlayerRepository chatPlayerRepository = JsonChatPlayerRepository.create(this, getDataFolder().toPath().resolve("players"));

        getCommand("ignore").setExecutor(new IgnoreCommand(chatPlayerRepository));
        getCommand("ignorehard").setExecutor(new IgnoreHardCommand());
        getCommand("ignorelist").setExecutor(new IgnoreListCommand(chatPlayerRepository));
    }
}
