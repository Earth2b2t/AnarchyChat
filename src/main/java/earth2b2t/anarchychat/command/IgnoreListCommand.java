package earth2b2t.anarchychat.command;

import earth2b2t.anarchychat.ignore.Ignore;
import earth2b2t.anarchychat.ignore.IgnorePlayerRepository;
import earth2b2t.i18n.BukkitI18n;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class IgnoreListCommand implements CommandExecutor {

    private static final int PAGE_SIZE = 16;
    private static final BukkitI18n i18n = BukkitI18n.get(IgnoreListCommand.class);
    private final IgnorePlayerRepository ignorePlayerRepository;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            i18n.print(sender, "anarchychat.console-not-supported");
            return true;
        }

        int page = 0;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                // just use default page
            }
        }

        List<Ignore> ignoreList = new ArrayList<>(ignorePlayerRepository.findByPlayer(player).getIgnoreList());
        Collections.reverse(ignoreList);

        if (ignoreList.isEmpty()) {
            i18n.print(sender, "anarchychat.ignorelist.empty");
            return true;
        }

        int pageCount = (int) Math.ceil((double) ignoreList.size() / PAGE_SIZE) - 1;
        if (page < 0) page = 0;
        if (page > pageCount) page = pageCount;

        int from = page * PAGE_SIZE;
        int to = Math.min(ignoreList.size(), (page + 1) * PAGE_SIZE);

        i18n.print(sender, "anarchychat.ignorelist.header");
        for (int i = from; i < to; i++) {
            Ignore ignore = ignoreList.get(i);
            sender.spigot().sendMessage(new ComponentBuilder("")
                    .append(ignore.getName())
                    .color(ChatColor.GOLD)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(i18n.plain(sender, "anarchychat.ignorelist.unignore"))))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ignore " + ignore.getName()))
                    .append(" [")
                    .append(ignore.getIgnoreType().toString().toLowerCase())
                    .color(ChatColor.DARK_AQUA)
                    .append("]")
                    .color(ChatColor.GOLD)
                    .create());
        }

        sender.sendMessage("");
        sender.spigot().sendMessage(new ComponentBuilder("           ")
                .append("<<  ")
                .color(ChatColor.GOLD)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(i18n.plain(sender, "anarchychat.ignorelist.go-to-top"))))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ignorelist"))
                .append("<  ")
                .color(ChatColor.GOLD)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(i18n.plain(sender, "anarchychat.ignorelist.go-to-prev"))))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ignorelist " + (page - 1)))
                .append("" + ChatColor.DARK_AQUA + (page + 1) + ChatColor.WHITE + "/" + ChatColor.DARK_AQUA + (pageCount + 1))
                .append("  >")
                .color(ChatColor.GOLD)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(i18n.plain(sender, "anarchychat.ignorelist.go-to-next"))))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ignorelist " + (page + 1)))
                .append("  >>")
                .color(ChatColor.GOLD)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(i18n.plain(sender, "anarchychat.ignorelist.go-to-bottom"))))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ignorelist " + pageCount))
                .create());
        i18n.print(sender, "anarchychat.ignorelist.footer");
        return true;
    }
}
