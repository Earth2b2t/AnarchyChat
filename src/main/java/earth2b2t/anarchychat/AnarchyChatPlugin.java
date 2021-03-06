package earth2b2t.anarchychat;

import earth2b2t.anarchychat.command.IgnoreCommand;
import earth2b2t.anarchychat.command.IgnoreHardCommand;
import earth2b2t.anarchychat.command.IgnoreLangCommand;
import earth2b2t.anarchychat.command.IgnoreListCommand;
import earth2b2t.anarchychat.command.MuteCommand;
import earth2b2t.anarchychat.command.MuteListCommand;
import earth2b2t.anarchychat.command.PMuteCommand;
import earth2b2t.anarchychat.command.ReplyCommand;
import earth2b2t.anarchychat.command.TellCommand;
import earth2b2t.anarchychat.h2.H2PlayerRepository;
import earth2b2t.anarchychat.ignore.IgnorePlayerRepository;
import earth2b2t.anarchychat.mute.MutePlayerRepository;
import earth2b2t.anarchychat.placeholder.EmptyPlaceholderResolver;
import earth2b2t.anarchychat.placeholder.PlaceholderApiResolver;
import earth2b2t.anarchychat.placeholder.PlaceholderResolver;
import earth2b2t.anarchychat.service.IgnoreService;
import earth2b2t.anarchychat.service.Service;
import earth2b2t.anarchychat.service.TellPreventService;
import earth2b2t.i18n.BukkitI18n;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class AnarchyChatPlugin extends JavaPlugin {

    private final ArrayList<Service> services = new ArrayList<>();
    private H2PlayerRepository h2PlayerRepository;

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
        saveDefaultConfig();

        BukkitI18n i18n = BukkitI18n.get(this);
        i18n.setDefaultLanguage("en_us");

        h2PlayerRepository = H2PlayerRepository.create(this,
                "jdbc:h2:file:" + new File(getDataFolder(), "data").getAbsolutePath());

        IgnorePlayerRepository ignorePlayerRepository = h2PlayerRepository;
        MutePlayerRepository mutePlayerRepository = h2PlayerRepository;

        Bukkit.getServicesManager().register(IgnorePlayerRepository.class, ignorePlayerRepository, this, ServicePriority.Normal);
        Bukkit.getServicesManager().register(MutePlayerRepository.class, mutePlayerRepository, this, ServicePriority.Normal);

        getCommand("ignore").setExecutor(new IgnoreCommand(ignorePlayerRepository));
        getCommand("ignorehard").setExecutor(new IgnoreHardCommand());
        getCommand("ignorelist").setExecutor(new IgnoreListCommand(ignorePlayerRepository));
        getCommand("ignorelang").setExecutor(new IgnoreLangCommand());
        getCommand("mute").setExecutor(new MuteCommand(mutePlayerRepository));
        getCommand("mutelist").setExecutor(new MuteListCommand(mutePlayerRepository));
        getCommand("pmute").setExecutor(new PMuteCommand());
        getCommand("reply").setExecutor(new ReplyCommand(ignorePlayerRepository));
        getCommand("tell").setExecutor(new TellCommand(ignorePlayerRepository, mutePlayerRepository));

        PlaceholderResolver placeholderResolver;
        if (PlaceholderApiResolver.isAvailable()) {
            placeholderResolver = new PlaceholderApiResolver();
        } else {
            placeholderResolver = new EmptyPlaceholderResolver();
        }

        register(new IgnoreService(ignorePlayerRepository, mutePlayerRepository, placeholderResolver, getConfig().getString("format")));
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

        h2PlayerRepository.close();
    }
}
