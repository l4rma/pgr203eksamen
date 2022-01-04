package no.eksamenPGR203;

import java.io.IOException;
import java.net.Socket;

public class HttpRequest extends HttpMessage {

    public HttpRequest(Socket socket) throws IOException {
        super(socket);
    }

    public String getRequestLine() {
        return super.getRequestLine();
    }

    public String getRequestPath() {
        return super.getRequestLine().split(" ")[1];
    }

    public String getRequestMethod() {
        return super.getRequestLine().split(" ")[0];
    }
}
