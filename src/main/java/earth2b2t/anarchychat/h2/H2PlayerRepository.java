package earth2b2t.anarchychat.h2;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import earth2b2t.anarchychat.ignore.Ignore;
import earth2b2t.anarchychat.ignore.IgnorePlayerRepository;
import earth2b2t.anarchychat.ignore.IgnoreType;
import earth2b2t.anarchychat.mute.MutePlayer;
import earth2b2t.anarchychat.mute.MutePlayerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.h2.Driver;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class H2PlayerRepository implements IgnorePlayerRepository, MutePlayerRepository, Listener, Closeable {

    private final HikariDataSource hikariDataSource;
    private final Map<Player, H2Player> h2PlayerMap = Collections.synchronizedMap(new HashMap<>());

    @Override
    public H2Player findByPlayer(Player player) {
        return h2PlayerMap.get(player);
    }

    @Override
    public Optional<MutePlayer> findByUniqueId(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) return Optional.of(h2PlayerMap.get(player));

        try (Connection connection = hikariDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                    SELECT name, global_muted, private_muted FROM players WHERE unique_id = ?;
                    """);
            preparedStatement.setObject(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                boolean globalMuted = resultSet.getBoolean("global_muted");
                boolean privateMuted = resultSet.getBoolean("private_muted");
                return Optional.of(new H2Player(this, uuid, name, globalMuted, privateMuted, List.of()));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    @Override
    public Optional<MutePlayer> findByName(String name) {
        Player player = Bukkit.getPlayerExact(name);
        if (player != null) {
            return Optional.of(h2PlayerMap.get(player));
        }

        try (Connection connection = hikariDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                    SELECT unique_id, global_muted, private_muted FROM players WHERE name = ?;
                    """);
            preparedStatement.setObject(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                UUID uuid = (UUID) resultSet.getObject("unique_id");
                boolean globalMuted = resultSet.getBoolean("global_muted");
                boolean privateMuted = resultSet.getBoolean("private_muted");
                return Optional.of(new H2Player(this, uuid, name, globalMuted, privateMuted, List.of()));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    @Override
    public Collection<MutePlayer> findAll() {
        HashSet<MutePlayer> result = new HashSet<>();

        try (Connection connection = hikariDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                    SELECT * FROM players WHERE global_muted = TRUE OR private_muted = TRUE;
                    """);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                UUID uuid = (UUID) resultSet.getObject("unique_id");
                String name = resultSet.getString("name");
                boolean globalMuted = resultSet.getBoolean("global_muted");
                boolean privateMuted = resultSet.getBoolean("private_muted");
                result.add(new H2Player(this, uuid, name, globalMuted, privateMuted, List.of()));
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
        return result;
    }

    public void setIgnoreType(H2Player h2Player, String name, IgnoreType ignoreType) {

        try (Connection connection = hikariDataSource.getConnection()) {
            if (ignoreType == null || ignoreType == IgnoreType.SOFT) {
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        DELETE FROM ignore_list WHERE player = ? AND target = ?;
                        """);
                preparedStatement.setObject(1, h2Player.getUniqueId());
                preparedStatement.setString(2, name);

                preparedStatement.execute();
            } else {
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        INSERT INTO ignore_list(player, target) VALUES(?, ?);
                        """);
                preparedStatement.setObject(1, h2Player.getUniqueId());
                preparedStatement.setString(2, name);

                preparedStatement.execute();
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    public void setGlobalMuted(H2Player h2Player, boolean globalMuted) {
        try (Connection connection = hikariDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                    UPDATE players SET global_muted = ? WHERE unique_id = ?
                    """);
            preparedStatement.setBoolean(1, globalMuted);
            preparedStatement.setObject(2, h2Player.getUniqueId());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    public void setPrivateMuted(H2Player h2Player, boolean privateMuted) {
        try (Connection connection = hikariDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                    UPDATE players SET private_muted = ? WHERE unique_id = ?
                    """);
            preparedStatement.setBoolean(1, privateMuted);
            preparedStatement.setObject(2, h2Player.getUniqueId());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    @Override
    public void close() {
        hikariDataSource.close();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        ArrayList<Ignore> ignoreList = new ArrayList<>();

        try (Connection connection = hikariDataSource.getConnection()) {
            PreparedStatement select = connection.prepareStatement("""
                    SELECT target FROM ignore_list WHERE player = ?;
                    """);
            select.setObject(1, p.getUniqueId());
            select.execute();
            ResultSet resultSet = select.executeQuery();

            while (resultSet.next()) {
                String target = resultSet.getString("target");
                ignoreList.add(new Ignore(target, IgnoreType.HARD));
            }

            PreparedStatement replace = connection.prepareStatement("""
                    MERGE INTO players AS t USING (VALUES(?, ?, ?, ?))
                        AS s(unique_id, name, global_muted, private_muted)
                        ON t.unique_id = s.unique_id
                    WHEN MATCHED THEN
                        UPDATE SET name = s.name
                    WHEN NOT MATCHED THEN
                        INSERT VALUES(s.unique_id, s.name, s.global_muted, s.private_muted);
                    """);
            replace.setObject(1, p.getUniqueId());
            replace.setString(2, p.getName());
            replace.setBoolean(3, false);
            replace.setBoolean(4, false);
            replace.execute();

        } catch (SQLException ex) {
            throw new UncheckedSQLException(ex);
        }

        H2Player h2Player = new H2Player(this, p.getUniqueId(), p.getName(), false, false, ignoreList);
        h2PlayerMap.put(p, h2Player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        h2PlayerMap.remove(e.getPlayer());
    }


    public static H2PlayerRepository create(Plugin plugin, String jdbcUrl) throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(Driver.class.getName());
        hikariConfig.setJdbcUrl(jdbcUrl);
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);

        try (Connection connection = hikariDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS players(
                        unique_id UUID PRIMARY KEY,
                        name VARCHAR NOT NULL,
                        global_muted BOOL,
                        private_muted BOOL
                    );
                    CREATE TABLE IF NOT EXISTS ignore_list(
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        player UUID NOT NULL,
                        target VARCHAR NOT NULL,
                        FOREIGN KEY (player) REFERENCES players(unique_id)
                    );
                    CREATE INDEX IF NOT EXISTS ix_players_name ON players(name);
                    CREATE INDEX IF NOT EXISTS ix_ignore_list_player ON ignore_list(player);
                    """);

            preparedStatement.execute();
        }

        H2PlayerRepository h2PlayerRepository = new H2PlayerRepository(hikariDataSource);
        Bukkit.getPluginManager().registerEvents(h2PlayerRepository, plugin);
        return h2PlayerRepository;
    }
}
