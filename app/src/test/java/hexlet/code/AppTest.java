package hexlet.code;

import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.DataSourceConfigurator;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTest {
    static Javalin app;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final PrintStream standardOut = System.out;
    private static MockWebServer mockServer;
    private static String mockUrl;

    @BeforeAll
    public static final void setUpAll() throws Exception {
        DataSourceConfigurator.prepareDataBase("jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;", "schemaH2.sql");

        mockServer = new MockWebServer();
        MockResponse mockResponse = new MockResponse();
        String testHtmlPageBody = Files.readString(Paths.get("src/test/resources/testPage.html"));
        mockResponse.setBody(testHtmlPageBody);
        mockServer.enqueue(mockResponse);
        mockServer.start();
        mockUrl = mockServer.url("/").toString();
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
    public static void closeDataSource() throws IOException {
        BaseRepository.dataSource.close();
        mockServer.shutdown();
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

            String requestBody = "url=" + mockUrl;
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains(mockUrl.substring(0, mockUrl.length() - 1)));

            assertEquals(++recordsCount, UrlRepository.getEntities().size());
        });
    }

    @Test
    @Order(3)
    public final void testUrlPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath("1"));
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains(mockUrl.substring(0, mockUrl.length() - 1)));
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

    @Test
    @Order(5)
    public final void testUrlCheck() {
        JavalinTest.test(app, (server, client) -> {
                var response = client.post(NamedRoutes.postCheckPath("1"));
                String responseBodyString = response.body().string();
                assertEquals(200, response.code());
                assertTrue(responseBodyString.contains("Проверка title"));
                assertTrue(responseBodyString.contains("Проверка description"));
                assertTrue(responseBodyString.contains("Проверка h1"));
            }
        );
    }
}
