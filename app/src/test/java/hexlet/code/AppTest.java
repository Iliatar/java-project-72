package hexlet.code;

import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.DataSourceConfigurator;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTest {
    static Javalin app;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final PrintStream standardOut = System.out;
    private final String urlString = "https://ru.hexlet.io";

    @BeforeAll
    public static final void setUpAll() throws Exception {
        DataSourceConfigurator.prepareDataBase("jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;", "schemaH2.sql");
    }
    @BeforeEach
    public void setUpEach() throws Exception {
        app = App.getApp();
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
    public final void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("Бесплатно проверяйте сайты на SEO пригодность"));
        });
    }

    @Test
    @Order(2)
    public final void createUrl() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            int recordsCount = UrlRepository.getEntities().size();

            String requestBody = "url=" + urlString;
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains(urlString));

            assertEquals(++recordsCount, UrlRepository.getEntities().size());
        });
    }

    @Test
    @Order(3)
    public final void testUrlPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath("1"));
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains(urlString));
        });
    }

    @Test
    @Order(4)
    public final void testNotFoundUrlPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath("999"));
            assertEquals(404, response.code());
        });
    }
}
