package earth2b2t.anarchychat.command;

import earth2b2t.anarchychat.mute.MutePlayer;
import earth2b2t.anarchychat.mute.MutePlayerRepository;
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

import java.util.List;

@RequiredArgsConstructor
public class MuteListCommand implements CommandExecutor {

    private static final int PAGE_SIZE = 17;
    private static final BukkitI18n i18n = BukkitI18n.get(MuteListCommand.class);
    private final MutePlayerRepository mutePlayerRepository;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int page = 0;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                // just use default page
            }
        }

        List<MutePlayer> muteList = mutePlayerRepository.findAll();

        if (muteList.isEmpty()) {
            i18n.print(sender, "anarchychat.mutelist.empty");
            return true;
        }

        int pageCount = (int) Math.ceil((double) muteList.size() / PAGE_SIZE) - 1;
        if (page < 0) page = 0;
        if (page > pageCount) page = pageCount;

        int from = page * PAGE_SIZE;
        int to = Math.min(muteList.size(), (page + 1) * PAGE_SIZE);

        i18n.print(sender, "anarchychat.mutelist.header");
        for (int i = from; i < to; i++) {
            String type;
            MutePlayer mutePlayer = muteList.get(i);
            if (mutePlayer.isGlobalMuted() && mutePlayer.isPrivateMuted()) {
                type = "global/private";
            } else if (mutePlayer.isGlobalMuted()) {
                type = "global";
            } else {
                type = "private";
            }
            sender.spigot().sendMessage(new ComponentBuilder("")
                    .append(mutePlayer.getName())
                    .color(ChatColor.GOLD)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(i18n.plain(sender, "anarchychat.mutelist.unmute"))))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mute " + mutePlayer.getName()))
                    .append(" [")
                    .append(type)
                    .color(ChatColor.DARK_AQUA)
                    .append("]")
                    .color(ChatColor.GOLD)
                    .create());
        }

        sender.spigot().sendMessage(new ComponentBuilder("")
                .append("<<  ")
                .color(ChatColor.GOLD)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(i18n.plain(sender, "anarchychat.mutelist.go-to-top"))))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mutelist"))
                .append("<  ")
                .color(ChatColor.GOLD)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(i18n.plain(sender, "anarchychat.mutelist.go-to-prev"))))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mutelist " + (page - 1)))
                .append("" + ChatColor.DARK_AQUA + (page + 1) + ChatColor.WHITE + "/" + ChatColor.DARK_AQUA + (pageCount + 1))
                .append("  >")
                .color(ChatColor.GOLD)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(i18n.plain(sender, "anarchychat.mutelist.go-to-next"))))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mutelist " + page + 1))
                .append("  >>")
                .color(ChatColor.GOLD)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(i18n.plain(sender, "anarchychat.mutelist.go-to-bottom"))))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mutelist " + pageCount))
                .create());
        i18n.print(sender, "anarchychat.mutelist.footer");
        return true;
    }
}
