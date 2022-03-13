package earth2b2t.anarchychat;

import earth2b2t.anarchychat.command.IgnoreCommand;
import earth2b2t.anarchychat.command.IgnoreHardCommand;
import earth2b2t.anarchychat.command.IgnoreListCommand;
import earth2b2t.anarchychat.command.IgnoreLangCommand;
import earth2b2t.anarchychat.command.ReplyCommand;
import earth2b2t.anarchychat.command.TellCommand;
import earth2b2t.anarchychat.player.JsonChatPlayerRepository;
import earth2b2t.anarchychat.service.IgnoreService;
import earth2b2t.anarchychat.service.Service;
import earth2b2t.anarchychat.service.TellPreventService;
import earth2b2t.i18n.BukkitI18n;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;

public class AnarchyChatPlugin extends JavaPlugin {

    private final ArrayList<Service> services = new ArrayList<>();

    public void register(Service service) {
        service.onEnable();
        Bukkit.getPluginManager().registerEvents(service, this);
        services.add(service);
    }

    @Override
    public void onEnable() {

        BukkitI18n i18n = BukkitI18n.get(this);
        i18n.setDefaultLanguage("en_us");

        JsonChatPlayerRepository chatPlayerRepository = JsonChatPlayerRepository.create(this, getDataFolder().toPath().resolve("players"));

        getCommand("ignore").setExecutor(new IgnoreCommand(chatPlayerRepository));
        getCommand("ignorehard").setExecutor(new IgnoreHardCommand());
        getCommand("ignorelist").setExecutor(new IgnoreListCommand(chatPlayerRepository));
        getCommand("ignorelang").setExecutor(new IgnoreLangCommand());
        getCommand("reply").setExecutor(new ReplyCommand(chatPlayerRepository));
        getCommand("tell").setExecutor(new TellCommand(chatPlayerRepository));

        register(new IgnoreService(chatPlayerRepository));
        register(new TellPreventService());
    }

    @Override
    public void onDisable() {
        Collections.reverse(services);
        for (Service service : services) {
            service.onDisable();
            HandlerList.unregisterAll(service);
        }
        services.clear();
    }
}
