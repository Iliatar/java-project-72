package hexlet.code;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hexlet.code.model.Url;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.DataSourceConfigurator;
import hexlet.code.repository.UrlRepository;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryTest {
    private static Url url;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final PrintStream standardOut = System.out;
    @BeforeAll
    public static void prepareDataBase() throws Exception {
        DataSourceConfigurator.prepareDataBase("jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;", "schemaH2.sql");
    }
    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(output));
    }
    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    @AfterAll
    public static void closeDataSource() {
        BaseRepository.dataSource.close();
    }

    @Test
    @Order(1)
    public void createRecord() throws SQLException {
        int recordsCount = UrlRepository.getEntities().size();

        url = new Url("https://ru.hexlet.io/courses/java-web/lessons/flash/theory_unit");
        UrlRepository.save(url);

        assertEquals(++recordsCount, UrlRepository.getEntities().size());
    }

    @Test
    @Order(2)
    public void findRecordById() throws SQLException {
        Url storedUrl = UrlRepository.find(url.getId()).get();
        assertEquals(url.getId(), storedUrl.getId());
        assertEquals(url.getName(), storedUrl.getName());
        assertEquals(url.getCreatedAt().getDate(), storedUrl.getCreatedAt().getDate());
        assertEquals(url.getCreatedAt().getTime(), storedUrl.getCreatedAt().getTime());
    }

    @Test
    @Order(3)
    public void findRecordByName() throws SQLException {
        Url storedUrl = UrlRepository.find(url.getName()).get();
        assertEquals(url.getId(), storedUrl.getId());
        assertEquals(url.getName(), storedUrl.getName());
        assertEquals(url.getCreatedAt().getDate(), storedUrl.getCreatedAt().getDate());
        assertEquals(url.getCreatedAt().getTime(), storedUrl.getCreatedAt().getTime());
    }
}
