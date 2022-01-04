package no.eksamenPGR203;

import no.eksamenPGR203.controllers.*;
import no.eksamenPGR203.controllers.get.*;
import no.eksamenPGR203.controllers.post.*;
import no.eksamenPGR203.database.*;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class HttpServer {
    private final ServerSocket serverSocket;
    private final int port;
    private final Map<String, HttpController> controllers;


    public HttpServer(int port, DataSource dataSource) throws IOException {
        this.port = port;
        ServerSocket serverSocket = new ServerSocket(port);
        ProjectMembersDao projectMembersDao = new ProjectMembersDao(dataSource);
        ProjectDao projectDao = new ProjectDao(dataSource);
        TaskDao taskDao = new TaskDao(dataSource);
        this.serverSocket = serverSocket;
        this.start();
        controllers = Map.ofEntries(
        // Gikk bare med 10 entries med "Map.of()" så vi gjorde sånn her...
            new AbstractMap.SimpleEntry<>("/createProject", new CreateProjectPostController(projectDao)),
            new AbstractMap.SimpleEntry<>("/createProjectMember", new CreateProjectMemberPostController(projectMembersDao)),
            new AbstractMap.SimpleEntry<>("/createTask", new CreateTaskPostController(taskDao, projectDao)),
            new AbstractMap.SimpleEntry<>("/taskAddMember", new TaskAddMemberPostController(taskDao, projectMembersDao)),
            new AbstractMap.SimpleEntry<>("/login", new LoginPostController(projectMembersDao, taskDao)),
            new AbstractMap.SimpleEntry<>("/project", new UlTagProjectsGetController(projectDao, taskDao, projectMembersDao)),
            new AbstractMap.SimpleEntry<>("/projectNames", new OptionTagProjectNamesGetController(projectDao)),
            new AbstractMap.SimpleEntry<>("/projectMemberNames", new OptionTagMemberNameGetController(projectMembersDao)),
            new AbstractMap.SimpleEntry<>("/projectMembers", new UlTagMembersGetController(projectMembersDao)),
            new AbstractMap.SimpleEntry<>("/taskNames", new OptionTagTaskNamesGetController(taskDao)),
            new AbstractMap.SimpleEntry<>("/tasks", new UlTagTasksGetController(taskDao, projectMembersDao)),
            new AbstractMap.SimpleEntry<>("/taskStatus", new OptionTagTaskStatusGetController(taskDao)),
            new AbstractMap.SimpleEntry<>("/returnTaskBasedOnStatus", new TaskSortByStatusPostController(taskDao, projectMembersDao)),
            new AbstractMap.SimpleEntry<>("/taskSortMember", new TaskSortByMemberPostController(taskDao, projectMembersDao)),
            new AbstractMap.SimpleEntry<>("/tasksWithCurrentStatus", new OptionTagTasksWithCurrentStatusGetController(taskDao)),
            new AbstractMap.SimpleEntry<>("/updateTaskStatus", new TaskUpdateStatusPostController(taskDao)),
            new AbstractMap.SimpleEntry<>("/mainpageAddingButtons", new MainPageAddingButtonsGetController(projectMembersDao, projectDao, taskDao)),
            new AbstractMap.SimpleEntry<>("/deleteTask", new TaskDeletePostController(taskDao, projectMembersDao)),
            new AbstractMap.SimpleEntry<>("/editTask", new TaskEditPostController(taskDao)),
            new AbstractMap.SimpleEntry<>("/removeMemberFromTask", new TaskRemoveMemberPostController(taskDao, projectMembersDao)),
            new AbstractMap.SimpleEntry<>("/projectEdit", new ProjectEditPostController(projectDao)),
            new AbstractMap.SimpleEntry<>("/projectAddMember", new ProjectAddMemberPostController(projectDao, projectMembersDao)),
            new AbstractMap.SimpleEntry<>("/projectRemoveMember", new ProjectRemoveMemberPostController(projectDao, projectMembersDao)),
            new AbstractMap.SimpleEntry<>("/projectDeletion", new ProjectDeletePostController(projectDao, projectMembersDao, taskDao)),
            new AbstractMap.SimpleEntry<>("/projectSortMembers", new ProjectSortMembers(projectDao, projectMembersDao, taskDao)),
            new AbstractMap.SimpleEntry<>("/logout", new LogoutPostController(projectMembersDao)),
            new AbstractMap.SimpleEntry<>("/taskPopulate", new TaskPopulate(taskDao)),
            new AbstractMap.SimpleEntry<>("/projectPopulate", new ProjecPopulate(projectDao)),
            new AbstractMap.SimpleEntry<>("/projectShowSingle", new ProjectGetSingle(projectDao, projectMembersDao, taskDao)),
            new AbstractMap.SimpleEntry<>("/deleteProjectMember", new ProjectMemberDeletePostController(projectDao, projectMembersDao, taskDao)),
            new AbstractMap.SimpleEntry<>("/echo", new EchoController())

        );
    }

    private void handleRequest(Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        HttpRequest request = new HttpRequest(clientSocket);
        System.out.println("Server: "+ request.getRequestMethod() + " " + request.getRequestPath());

        HttpController controller = controllers.get(request.getRequestPath());
        if(controller != null) {
            controller.handle(request, clientSocket);
        } else if (request.getRequestPath().contains("/echo")) {
            controller = controllers.get("/echo");
            controller.handle(request, clientSocket);
        } else {
            serveFile(clientSocket, request.getRequestPath());
        }
        HttpResponse response = new HttpResponse();
        response.write(clientSocket);
    }

    private void serveFile(Socket clientSocket, String requestPath) throws IOException {
        HttpResponse response = new HttpResponse();
        if (requestPath.equals("/")) {
            requestPath = "/index.html";
        }

        InputStream inputStream = getClass().getResourceAsStream(requestPath);

        if (inputStream == null) {
            response.setFirstLine("HTTP/1.1 404 Page not found");
            response.setBody(
                    "<h2>404: Page Not Found</h2>" +
                    "No such page as: " + requestPath);
            response.putHeader("Content-Length", String.valueOf(response.getBody().getBytes().length));
            response.putHeader("Content-Type", "text/html");
            response.write(clientSocket);
            return;
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        inputStream.transferTo(buffer);

        if (requestPath.endsWith(".html")) {
            response.putHeader("Content-Type", "text/html");
        } else if (requestPath.endsWith(".css")) {
            response.putHeader("Content-Type", "text/css");
        }else if(requestPath.endsWith(".txt")){
            response.putHeader("Content-Type", "text/plain");
        }

        response.putHeader("Content-Length", String.valueOf(buffer.toByteArray().length));
        response.write(clientSocket);
        clientSocket.getOutputStream().write(buffer.toByteArray());
    }


    public int getActualPort() {
        return port;
    }

    public static void main(String[] args) throws IOException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        Properties properties = new Properties();
        try (FileReader fileReader = new FileReader("pgr203.properties")) {
            properties.load(fileReader);
        }
        dataSource.setUrl(properties.getProperty("dataSource.url"));
        dataSource.setUser(properties.getProperty("dataSource.username"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));
        Flyway.configure().dataSource(dataSource).load().migrate();

        new HttpServer(8080, dataSource);

    }

    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    handleRequest(serverSocket.accept());
                } catch (IOException | SQLException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        System.out.println("Server running at http://localhost:8080");
    }
}
