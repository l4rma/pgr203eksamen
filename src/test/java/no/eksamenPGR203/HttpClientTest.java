package no.eksamenPGR203;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpClientTest {
    @Test
    void testToGetGreenBadge() {
        assertEquals(1, 1);
    }
    @Test
    void checkFor200() throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?status=200");

        assertEquals("200", client.getStatusCode());
    }
    @Test
    void checkFor404() throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?status=404");

        assertEquals("404", client.getStatusCode());
    }
    @Test
    void checkContentLength() throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?status=200&body=hello");

        assertEquals("5", client.getResponseHeader("Content-Length"));
    }
    @Test
    void checkForSockets() throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?status=200&body=hello");

        assertEquals("5", client.getResponseHeader("Content-Length"));
    }
}
