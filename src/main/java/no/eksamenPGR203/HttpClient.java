package no.eksamenPGR203;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class HttpClient {
    private final String requestBody;
    private String httpMethod;
    String statusCode;
    private String request;
    private String host;
    private int port;
    private String responseFirstLine;
    HashMap<String, String> responseHeaders = new HashMap<>();

    private String responseBody;

    public HttpClient(String host, int port, String request) throws IOException {
        this(host, port, request, "GET", null);
    }

    public HttpClient(String host, int port, String requestTarget, String httpMethod, String requestBody) throws IOException {
        this.host = host;
        this.request = requestTarget;
        this.port = port;
        this.httpMethod = httpMethod;
        this.requestBody = requestBody;
        executeRequest();
    }

    public void executeRequest() throws IOException {
        Socket socket = new Socket(host, port);

        String contentLengthHeader = requestBody != null ? "Content-Length: " + requestBody.length() + "\r\n" : "";

        String req = httpMethod + " " + request + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                contentLengthHeader +
                "\r\n";

        socket.getOutputStream().write(req.getBytes());

        if (requestBody != null) {
            socket.getOutputStream().write(requestBody.getBytes());
        }

        HttpResponse response = new HttpResponse(socket);
        responseFirstLine = response.getRequestLine();
        statusCode = responseFirstLine.split(" ")[1];
        responseHeaders = response.getHeaders();
        responseBody = response.getBody();
    }

    public String getResponseHeader(String responseHeader) {

        return responseHeaders.get(responseHeader);
    }

    public String getStatusCode() {

        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

}
