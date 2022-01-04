package no.eksamenPGR203;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;

public abstract class HttpMessage {
    protected String firstLine;
    protected HashMap<String, String> headers;
    protected String body;


    public HttpMessage(Socket socket) throws IOException {
        firstLine = readLine(socket);
        headers = readHeaders(socket);
        String contentLength = headers.get("Content-Length");
        if (contentLength != null) {
            body = readBody(socket, Integer.parseInt(contentLength));
        }
    }

    public HttpMessage(String firstLine, HashMap<String, String> headers, String body) {
        this.firstLine = firstLine;
        this.headers = headers;
        this.body = body;
    }

    public static HashMap<String, String> readHeaders(Socket socket) throws IOException {
        HashMap<String, String> headers = new HashMap<>();
        while(true){
            String lineFromServer = readLine(socket);
            if(lineFromServer.equals("\n")){
                break;
            }
            String responseName = lineFromServer.split(": ")[0].trim();
            String responseValue = lineFromServer.split(": ")[1].trim();

            headers.put(responseName, responseValue);
        }
        return headers;
    }

    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }

    public static String readLine(Socket socket) throws IOException {
        StringBuilder line = new StringBuilder();
        int c;
        while((c = socket.getInputStream().read()) != 13) {

            line.append((char) c);
        }
       return URLDecoder.decode(line.toString(),"UTF-8");
    }

    static String readBody(Socket socket, int contLength) throws IOException {
        StringBuilder body = new StringBuilder();
        socket.getInputStream().read(); // read \n
        for (int i = 0; i < contLength; i++) {
            int c = socket.getInputStream().read();
            body.append((char) c);
        }
        return URLDecoder.decode(body.toString(),"UTF-8");
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getRequestLine() {
        return firstLine;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
