package earth2b2t.anarchychat.h2;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import earth2b2t.anarchychat.ignore.Ignore;
import earth2b2t.anarchychat.ignore.IgnorePlayer;
import earth2b2t.anarchychat.ignore.IgnorePlayerRepository;
import earth2b2t.anarchychat.ignore.IgnoreType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.h2.Driver;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.WeakHashMap;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class H2IgnorePlayerRepository implements IgnorePlayerRepository, Closeable {

    private final HikariDataSource hikariDataSource;
    private final WeakHashMap<Player, H2IgnorePlayer> cache = new WeakHashMap<>();

    @Override
    public IgnorePlayer findByPlayer(Player player) {

        H2IgnorePlayer ignorePlayer = cache.get(player);
        if (ignorePlayer != null) return ignorePlayer;

        ArrayList<Ignore> ignoreList = new ArrayList<>();

        try (Connection connection = hikariDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT target FROM ignore_list WHERE player = ?
                    """);
            preparedStatement.setObject(1, player.getUniqueId());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String target = resultSet.getString("target");
                ignoreList.add(new Ignore(target, IgnoreType.HARD));
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }

        ignorePlayer = new H2IgnorePlayer(this, player.getUniqueId(), player.getName(), ignoreList);
        cache.put(player, ignorePlayer);
        return ignorePlayer;
    }

    public void setIgnoreType(H2IgnorePlayer ignorePlayer, String name, IgnoreType ignoreType) {

        try (Connection connection = hikariDataSource.getConnection()) {
            if (ignoreType == null || ignoreType == IgnoreType.SOFT) {
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        DELETE FROM ignore_list WHERE player = ? AND target = ?
                        """);
                preparedStatement.setObject(1, ignorePlayer.getUniqueId());
                preparedStatement.setString(2, name);

                preparedStatement.execute();
            } else {
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        INSERT INTO ignore_list(player, target) VALUES(?, ?)
                        """);
                preparedStatement.setObject(1, ignorePlayer.getUniqueId());
                preparedStatement.setString(2, name);

                preparedStatement.execute();
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    @Override
    public void close() {
        hikariDataSource.close();
    }

    public static H2IgnorePlayerRepository create(String jdbcUrl) throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(Driver.class.getName());
        hikariConfig.setJdbcUrl(jdbcUrl);
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);

        try (Connection connection = hikariDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS ignore_list(
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        player UUID NOT NULL,
                        target VARCHAR NOT NULL
                    );
                                        
                    CREATE INDEX IF NOT EXISTS ix_ignore_list_player ON ignore_list(player);
                    """);

            preparedStatement.execute();
        }

        return new H2IgnorePlayerRepository(hikariDataSource);
    }
}
