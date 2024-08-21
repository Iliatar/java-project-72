package hexlet.code.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.App;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.stream.Collectors;

@Slf4j
public class DataSourceConfigurator {
    private static final String SCHEMA_FILE_NAME = "schema.sql";
    public static void prepareDataBase(String jdbcUrl) throws SQLException {
        log.trace("Begin prepare database");
        log.trace("jdbcUrl = " + jdbcUrl);

        var hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(jdbcUrl);
        var dataSource = new HikariDataSource(hikariConfig);

        var url = App.class.getClassLoader().getResourceAsStream(SCHEMA_FILE_NAME);
        var sql = new BufferedReader(new InputStreamReader(url))
                .lines().collect(Collectors.joining("\n"));

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.dataSource = dataSource;
        log.trace("Database prepared successfully");
    }
}
