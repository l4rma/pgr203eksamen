package no.eksamenPGR203;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;


public class HttpResponse extends HttpMessage {

    public HttpResponse(Socket socket) throws IOException {
        super(socket);
    }

    public HttpResponse(String body) {
        super("HTTP/1.1 200 OK", new HashMap<>(), body);
        headers.put("Content-Length", String.valueOf(body.getBytes().length));
        headers.put("Content-Type", "text/html; charset=utf-8");
    }

    public HttpResponse() {
        super("HTTP/1.1 200 OK", new HashMap<>(), null);
    }

    public void write(Socket socket) throws IOException {
        socket.getOutputStream().write((firstLine + "\r\n").getBytes());
        for (String headerName : headers.keySet()) {
            socket.getOutputStream().write((headerName + ": " + headers.get(headerName) + "\r\n").getBytes());
        }
        socket.getOutputStream().write(("\r\n").getBytes());
        if (body != null) {
            socket.getOutputStream().write(body.getBytes());
        }
    }

    public void putHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
    }
}
