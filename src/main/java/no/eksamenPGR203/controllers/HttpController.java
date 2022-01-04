package no.eksamenPGR203.controllers;


import no.eksamenPGR203.HttpRequest;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public interface HttpController {
    void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException;
}
