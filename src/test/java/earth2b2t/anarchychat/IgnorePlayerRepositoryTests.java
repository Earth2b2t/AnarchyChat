package earth2b2t.anarchychat;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.google.common.jimfs.Jimfs;
import earth2b2t.anarchychat.h2.H2IgnorePlayerRepository;
import earth2b2t.anarchychat.ignore.IgnorePlayer;
import earth2b2t.anarchychat.ignore.IgnorePlayerRepository;
import earth2b2t.anarchychat.ignore.IgnoreType;
import earth2b2t.anarchychat.json.JsonIgnorePlayerRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IgnorePlayerRepositoryTests {

    private static ServerMock server;

    @BeforeAll
    public static void beforeAll() {
        server = MockBukkit.mock();
    }

    @AfterAll
    public static void afterAll() {
        MockBukkit.unmock();
    }

    public static List<IgnorePlayerRepository> newIgnorePlayerRepositories() throws SQLException {
        return List.of(
                JsonIgnorePlayerRepository.create(MockBukkit.createMockPlugin(), Jimfs.newFileSystem().getPath("dir")),
                H2IgnorePlayerRepository.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
        );
    }

    @ParameterizedTest
    @MethodSource("newIgnorePlayerRepositories")
    public void testFindPlayer(IgnorePlayerRepository ignorePlayerRepository) {
        PlayerMock playerMock = server.addPlayer();
        IgnorePlayer ignorePlayer = ignorePlayerRepository.findByPlayer(playerMock);
        assertEquals(0, ignorePlayer.getIgnoreList().size());
    }

    @ParameterizedTest
    @MethodSource("newIgnorePlayerRepositories")
    public void testSetIgnoreType(IgnorePlayerRepository ignorePlayerRepository) {
        PlayerMock playerMock = server.addPlayer();
        IgnorePlayer ignorePlayer = ignorePlayerRepository.findByPlayer(playerMock);

        String soft = "soft";
        ignorePlayer.setIgnoreType(soft, IgnoreType.SOFT);
        assertEquals(IgnoreType.SOFT, ignorePlayer.getIgnoreType(soft));

        String hard = "hard";
        ignorePlayer.setIgnoreType(hard, IgnoreType.HARD);
        assertEquals(IgnoreType.HARD, ignorePlayer.getIgnoreType(hard));
    }

}
