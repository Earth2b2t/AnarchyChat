package earth2b2t.anarchychat.service;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import earth2b2t.anarchychat.h2.H2PlayerRepository;
import earth2b2t.anarchychat.placeholder.MapPlaceholderResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class IgnoreServiceTests {

    private static ServerMock server;

    @BeforeAll
    public static void beforeAll() {
        server = MockBukkit.mock();
    }

    @AfterAll
    public static void afterAll() {
        MockBukkit.unmock();
    }

    @Test
    public void testFormat() {
        Map<String, String> placeholders = Map.of("randomstr", "randomvalue");
        MapPlaceholderResolver resolver = new MapPlaceholderResolver(placeholders);

        PlayerMock player = server.addPlayer();

        try (H2PlayerRepository repo = H2PlayerRepository.create(MockBukkit.createMockPlugin(), "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")) {

            String world = player.getWorld().getName();
            String name = player.getName();
            String msg = "randommsg";

            IgnoreService ignoreService = new IgnoreService(repo, repo, resolver, "{randomstr}[{world}]<{playerName}>{message}");
            String str = ignoreService.compileFormat(player).formatted(player.getName(), msg);
            assertEquals("randomvalue[%s]<%s>%s".formatted(world, name, msg), str);

            ignoreService = new IgnoreService(repo, repo, resolver, "{r}[{world}]<{playerName}>{message}");
            str = ignoreService.compileFormat(player).formatted(player.getName(), msg);
            assertEquals("{r}[%s]<%s>%s".formatted(world, name, msg), str);

            ignoreService = new IgnoreService(repo, repo, resolver, "{randomstr}{randomstr}[{world}]<{playerName}>{message}");
            str = ignoreService.compileFormat(player).formatted(player.getName(), msg);
            assertEquals("randomvaluerandomvalue[%s]<%s>%s".formatted(world, name, msg), str);
        }
    }
}
