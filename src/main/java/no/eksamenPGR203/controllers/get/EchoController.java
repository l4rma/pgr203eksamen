package no.eksamenPGR203.controllers.get;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.QueryString;
import no.eksamenPGR203.controllers.HttpController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;

public class EchoController implements HttpController {

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        HttpResponse response = handle(request);
        response.write(clientSocket);
    }

    public HttpResponse handle(HttpRequest request) throws UnsupportedEncodingException {
        HttpResponse response = new HttpResponse();
        String requestTarget = request.getRequestLine().split(" ")[1];
        int positionOfQuestionMark = requestTarget.indexOf("?");
        String queryString = requestTarget.substring(positionOfQuestionMark + 1);
        HashMap<String, String> getQueryString = QueryString.queryString(queryString);

        if (getQueryString.containsKey("status")) {
            String statusCode = getQueryString.get("status");
            response.setFirstLine("HTTP/1.1 " + statusCode + " Ok");
        }
        if (getQueryString.containsKey("Location")) {
            String location = getQueryString.get("Location");
            while (location.contains("www")) {
                int wwwPos = location.indexOf("www");
                for (int i = 0; i < 4; i++) {
                    location = location.substring(0, wwwPos) + location.substring(wwwPos + 1);
                }
            }
            response.putHeader("Location", location);
        }
        if (getQueryString.containsKey("body".toLowerCase())) {
            response.setBody(getQueryString.get("body".toLowerCase()));
        } else {
            response.setBody("");
        }
        response.putHeader("Content-Length", String.valueOf(response.getBody().getBytes().length));
        return response;
    }
}
