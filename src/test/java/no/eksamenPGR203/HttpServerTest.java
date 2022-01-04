package no.eksamenPGR203;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {
    private JdbcDataSource dataSource;
    @BeforeEach
    void setUp() {
        dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
    }

    @Test
    void shouldReadResponseCode() throws IOException {
        HttpServer server = new HttpServer(10000, dataSource);
        server.start();
        int port = server.getActualPort();
        HttpClient client = new HttpClient("localhost", port, "/echo?test=super");

        assertEquals("200", client.getStatusCode());
    }

    @Test
    void shouldParseRequestParameters() throws IOException {
        HttpServer server = new HttpServer(10001, dataSource);
        int port = server.getActualPort();
        HttpClient client = new HttpClient("localhost", port, "/echo?status=401");

        assertEquals("401", client.getStatusCode());
    }
    @Test
    void shouldParseRequestParameterss() throws IOException {
        HttpServer server = new HttpServer(10002, dataSource);
        int port = server.getActualPort();
        HttpClient client = new HttpClient("localhost", port, "/echo?status=302&Location=http://www.example.com");

        assertEquals("http://example.com", client.getResponseHeader("Location"));
    }

    @Test
    void shouldParseRequestParametersBody() throws IOException {
        HttpServer server = new HttpServer(10003, dataSource);
        int port = server.getActualPort();
        HttpClient client = new HttpClient("localhost", port, "/echo?status=200&body=HelloWorld");

        assertEquals("HelloWorld", client.getResponseBody());
    }

    @Test
    void shouldReturnFileOnDisk() throws IOException {
        HttpServer server = new HttpServer(10004, dataSource);
        File documentRoot = new File("target/test-classes");
        String fileContent = "Test " + new Date();
        Files.writeString(new File(documentRoot, "test.txt").toPath(), fileContent);
        HttpClient client = new HttpClient("localhost", 10004, "/test.txt");

        assertEquals(fileContent, client.getResponseBody());
    }
    @Test
    void shouldReturn404IfFileNotFound() throws IOException {
        HttpServer server = new HttpServer(10006, dataSource);
        HttpClient client = new HttpClient("localhost", 10006, "/nonexistingFile.txt");

        assertEquals("404", client.getStatusCode());
    }

    @Test
    void shouldReturnCorrectContentType() throws IOException {
        HttpServer server = new HttpServer(10007, dataSource);
        File documentRoot = new File("target/test-classes");
        Files.writeString(new File(documentRoot, "test.txt").toPath(), "Hello world");
        HttpClient client = new HttpClient("localhost", 10007, "/test.txt");

        assertEquals("text/plain", client.getResponseHeader("Content-Type"));

        Files.writeString(new File(documentRoot, "index.html").toPath(), "<html>Hello world</html>");
        client = new HttpClient("localhost", 10007, "/index.html");

        assertEquals("text/html", client.getResponseHeader("Content-Type"));
    }

}
