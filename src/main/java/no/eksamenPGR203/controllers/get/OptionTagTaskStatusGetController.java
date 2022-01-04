package no.eksamenPGR203.controllers.get;


import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.Task;
import no.eksamenPGR203.database.TaskDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;

public class OptionTagTaskStatusGetController implements HttpController {
    private TaskDao taskDao;

    public OptionTagTaskStatusGetController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        HashSet<String> set = new HashSet<>();
        StringBuilder body = new StringBuilder();
        for (Task task : taskDao.list()) {
            set.add("<option>" + task.getStatus() + "</option>");
        }
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()){
            body.append(iterator.next());
        }

        HttpResponse response = new HttpResponse(body.toString());
        response.write(clientSocket);
    }
}
