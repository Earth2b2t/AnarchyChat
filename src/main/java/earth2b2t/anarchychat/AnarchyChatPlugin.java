package earth2b2t.anarchychat;

import earth2b2t.anarchychat.command.IgnoreCommand;
import earth2b2t.anarchychat.command.IgnoreHardCommand;
import earth2b2t.anarchychat.command.IgnoreLangCommand;
import earth2b2t.anarchychat.command.IgnoreListCommand;
import earth2b2t.anarchychat.command.ReplyCommand;
import earth2b2t.anarchychat.command.TellCommand;
import earth2b2t.anarchychat.h2.H2IgnorePlayerRepository;
import earth2b2t.anarchychat.service.IgnoreService;
import earth2b2t.anarchychat.service.Service;
import earth2b2t.anarchychat.service.TellPreventService;
import earth2b2t.i18n.BukkitI18n;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class AnarchyChatPlugin extends JavaPlugin {

    private final ArrayList<Service> services = new ArrayList<>();
    private H2IgnorePlayerRepository ignorePlayerRepository;

    public AnarchyChatPlugin() {
        super();
    }

    public AnarchyChatPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    public void register(Service service) {
        service.onEnable();
        Bukkit.getPluginManager().registerEvents(service, this);
        services.add(service);
    }

    @Override
    public void onEnable() {
        try {
            BukkitI18n i18n = BukkitI18n.get(this);
            i18n.setDefaultLanguage("en_us");

            ignorePlayerRepository = H2IgnorePlayerRepository.create("jdbc:h2:file:" + new File(getDataFolder(), "data").getCanonicalPath());

            getCommand("ignore").setExecutor(new IgnoreCommand(ignorePlayerRepository));
            getCommand("ignorehard").setExecutor(new IgnoreHardCommand());
            getCommand("ignorelist").setExecutor(new IgnoreListCommand(ignorePlayerRepository));
            getCommand("ignorelang").setExecutor(new IgnoreLangCommand());
            getCommand("reply").setExecutor(new ReplyCommand(ignorePlayerRepository));
            getCommand("tell").setExecutor(new TellCommand(ignorePlayerRepository));

            register(new IgnoreService(ignorePlayerRepository));
            register(new TellPreventService());
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        Collections.reverse(services);
        for (Service service : services) {
            service.onDisable();
            HandlerList.unregisterAll(service);
        }
        services.clear();

        ignorePlayerRepository.close();
    }
}
