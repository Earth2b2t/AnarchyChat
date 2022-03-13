package earth2b2t.anarchychat;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import earth2b2t.anarchychat.h2.H2PlayerRepository;
import earth2b2t.anarchychat.mute.MutePlayer;
import earth2b2t.anarchychat.mute.MutePlayerRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MutePlayerRepositoryTests {

    private static ServerMock server;

    @BeforeAll
    public static void beforeAll() {
        server = MockBukkit.mock();
    }

    @AfterAll
    public static void afterAll() {
        MockBukkit.unmock();
    }

    public static List<MutePlayerRepository> newMutePlayerRepositories() {
        return List.of(
                H2PlayerRepository.create(MockBukkit.createMockPlugin(), "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
        );
    }

    @ParameterizedTest
    @MethodSource("newMutePlayerRepositories")
    public void testFindByUniqueId(MutePlayerRepository mutePlayerRepository) {
        PlayerMock playerMock = server.addPlayer();
        MutePlayer mutePlayer = mutePlayerRepository.findByUniqueId(playerMock.getUniqueId()).orElseThrow();
        assertFalse(mutePlayer.isGlobalMuted());
        assertFalse(mutePlayer.isPrivateMuted());

        mutePlayer.setGlobalMuted(true);
        assertTrue(mutePlayer.isGlobalMuted());
        assertFalse(mutePlayer.isPrivateMuted());

        mutePlayer.setPrivateMuted(true);
        assertTrue(mutePlayer.isGlobalMuted());
        assertTrue(mutePlayer.isPrivateMuted());

        playerMock.kickPlayer(null);
        mutePlayer = mutePlayerRepository.findByUniqueId(playerMock.getUniqueId()).orElseThrow();
        assertTrue(mutePlayer.isGlobalMuted());
        assertTrue(mutePlayer.isPrivateMuted());

        mutePlayer.setGlobalMuted(false);
        assertFalse(mutePlayer.isGlobalMuted());
        assertTrue(mutePlayer.isPrivateMuted());

        mutePlayer.setPrivateMuted(false);
        assertTrue(mutePlayer.isGlobalMuted());
        assertFalse(mutePlayer.isPrivateMuted());
    }

    @ParameterizedTest
    @MethodSource("newMutePlayerRepositories")
    public void testFindByName(MutePlayerRepository mutePlayerRepository) {
        PlayerMock playerMock = server.addPlayer();
        MutePlayer mutePlayer = mutePlayerRepository.findByUniqueId(playerMock.getUniqueId()).orElseThrow();
        assertFalse(mutePlayer.isGlobalMuted());
        assertFalse(mutePlayer.isPrivateMuted());

        mutePlayer.setGlobalMuted(true);
        assertTrue(mutePlayer.isGlobalMuted());
        assertFalse(mutePlayer.isPrivateMuted());

        mutePlayer.setPrivateMuted(true);
        assertTrue(mutePlayer.isGlobalMuted());
        assertTrue(mutePlayer.isPrivateMuted());

        playerMock.kickPlayer(null);
        mutePlayer = mutePlayerRepository.findByName(playerMock.getName()).orElseThrow();
        assertTrue(mutePlayer.isGlobalMuted());
        assertTrue(mutePlayer.isPrivateMuted());

        mutePlayer.setGlobalMuted(false);
        assertFalse(mutePlayer.isGlobalMuted());
        assertTrue(mutePlayer.isPrivateMuted());

        mutePlayer.setPrivateMuted(false);
        assertTrue(mutePlayer.isGlobalMuted());
        assertFalse(mutePlayer.isPrivateMuted());
    }


    @ParameterizedTest
    @MethodSource("newMutePlayerRepositories")
    public void testFindAll(MutePlayerRepository mutePlayerRepository) {
        PlayerMock playerMock = server.addPlayer();

        MutePlayer mutePlayer = mutePlayerRepository.findByUniqueId(playerMock.getUniqueId()).orElseThrow();
        mutePlayer.setGlobalMuted(true);
        playerMock.kickPlayer(null);

        playerMock = server.addPlayer();
        mutePlayer = mutePlayerRepository.findByUniqueId(playerMock.getUniqueId()).orElseThrow();
        mutePlayer.setPrivateMuted(true);
        playerMock.kickPlayer(null);

        playerMock = server.addPlayer();
        mutePlayer = mutePlayerRepository.findByUniqueId(playerMock.getUniqueId()).orElseThrow();
        mutePlayer.setGlobalMuted(true);
        mutePlayer.setPrivateMuted(true);
        playerMock.kickPlayer(null);

        playerMock = server.addPlayer();
        playerMock.kickPlayer(null);

        Collection<MutePlayer> mutePlayers = mutePlayerRepository.findAll();
        assertEquals(3, mutePlayers.size());
        int counter = 0;
        for (MutePlayer m : mutePlayers) {
            if (m.isGlobalMuted() && m.isPrivateMuted()) {
                counter += 1;
            } else if (m.isGlobalMuted()) {
                counter += 2;
            } else if (m.isPrivateMuted()) {
                counter += 4;
            }
        }

        assertEquals(7, counter);
    }
}
