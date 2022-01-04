package no.eksamenPGR203.controllers.post;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.PasswordHash;
import no.eksamenPGR203.QueryString;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.*;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;

public class LoginPostController implements HttpController {
    private final ProjectMembersDao projectMembersDao;
    private final TaskDao taskDao;

    public LoginPostController(ProjectMembersDao projectMembersDao, TaskDao taskDao) {
        this.projectMembersDao = projectMembersDao;
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        HttpResponse response = new HttpResponse();
        // Check if the request comes from clicking "sign in" button, of from submitting userId and pw.
        if (request.getBody().length() < 1) {
            //System.out.println("No form data");
            //get cookie
            String cookies = request.getHeaders().get("Cookie");
            //check cookie
            if (cookies.contains("userId")) {
                int index = cookies.indexOf("userId");
                String cookieValue = cookies.substring(index).split("=")[1];

                int memberId = 0;
                for (ProjectMember p : projectMembersDao.list()) {
                    if (PasswordHash.md5Hash(p.getEmailaddress() + projectMembersDao.getPassword(p.getEmailaddress())).equals(cookieValue)) {
                        memberId = p.getId(); // Get id of user matching cookie
                    }
                }
                if (memberId == 0) { // If no one matched, id=0, redirect to login page
                    response.setFirstLine("HTTP/1.1 302 Redirect");
                    response.putHeader("Location", "login.html");
                    response.write(clientSocket);
                    return;
                }
                ProjectMember member = projectMembersDao.retrieve(memberId);

                // logg in response
                StringBuilder sb = new StringBuilder();
                sb.append("<html lang='en'>");
                sb.append("<head><meta charset='UTF-8'>");
                sb.append("<title>KristianiaProject</title>");
                sb.append("<link rel='stylesheet' href='style.css'></head><body>");
                sb.append("You are logged in as <strong>").append(member.getFirstName()).append(" ").append(member.getLastName()).append("</strong><br>");
                // Get members tasks, if member has tasks
                if (taskDao.listAMembersTasks(memberId).size() > 0) {
                    sb.append("Your tasks are: ");
                    for (Task task : taskDao.listAMembersTasks(memberId)) {
                        sb.append("<li>").append(task.getTaskName()).append("</li>");
                    }
                    // Form to remove member from tasks
                    addRemoveMemberButton(member, sb);

                } else { // if member has no tasks
                    sb.append("You have no tasks.");
                }

                sb.append("<br>Return to <a href='/'>main</a>");
                sb.append("<form method='post' action='/logout'><button>Sign out</button>");
                sb.append("</body></html>");

                response.setBody(sb.toString());
                // Respons headers
                response.putHeader("Content-Length", String.valueOf(sb.toString().getBytes().length));
                response.putHeader("Content-Type", "text/html; charset=utf-8");
            } else {
                System.out.println("Member or pw not found");
                System.out.println("no cookie");
                response.setFirstLine("HTTP/1.1 302 Redirect");
                response.putHeader("Location", "login.html");
            }
            // Send response
        } else {
            //System.out.println("Form data");
            // If body was sent with request, logged in by filling out login form
            HashMap<String, String> loginInfo = QueryString.queryString(request.getBody()); // Get mail and pw from body
            String hashedPw = PasswordHash.md5Hash(loginInfo.get("password"));
            String memberEmail = loginInfo.get("emailAddress");
            StringBuilder sb = new StringBuilder();

            sb.append("<html lang='en'>");
            sb.append("<head><meta charset='UTF-8'>");
            sb.append("<title>KristianiaProject</title>");
            sb.append("<link rel='stylesheet' href='style.css'></head><body>");
            if (projectMembersDao.getPassword(memberEmail).equals(hashedPw)) { // check if pw matches pw of user email
                for (ProjectMember p : projectMembersDao.list()) {
                    if (p.getEmailaddress().equals(memberEmail)) { // Find member with same email as loggin email from form
                        // Set cookie = mail+pw hashed with md5
                        response.putHeader("Set-Cookie", "userId=" + PasswordHash.md5Hash(memberEmail + projectMembersDao.getPassword(memberEmail)));
                        // Using almost same code as above... feels like it could be set up smarter... :S
                        sb.append("Success! You are logged in as <strong>").append(p.getFirstName()).append(" ").append(p.getLastName()).append("</strong><br>");
                        if (taskDao.listAMembersTasks(p.getId()).size() > 0) {
                            sb.append("Your tasks are: ");
                            for (Task task : taskDao.listAMembersTasks(p.getId())) {
                                sb.append("<li>").append(task.getTaskName()).append("</li>");
                            }

                            addRemoveMemberButton(p, sb);

                        } else {
                            sb.append("You have no tasks.");
                        }
                    }
                }

                sb.append("<br>Return to <a href='/'>main</a>");

            } else { // If mail and pw dont match

                sb.append("Oops.. wrong username or password<br>" +
                        "<a href='/login.html'>Try again?</a><br>" +
                        "Return to <a href='/'>main</a>");
            }
            sb.append("</body></html>");
            response.setBody(sb.toString());
            response.putHeader("Content-Length", String.valueOf(sb.toString().getBytes().length));
            response.putHeader("Content-Type", "text/html; charset=utf-8");

        }
        response.write(clientSocket);
    }

    private void addRemoveMemberButton(ProjectMember member, StringBuilder sb) throws SQLException, IOException, NoSuchAlgorithmException {
        sb.append("<br><form method='POST' action='/removeMemberFromTask'>" + "<label>Remove</label>" + "<select id='projectMembers2' name='projectMember'><option value='").append(member.getId()).append("'>").append(member.getFirstName()).append("</option></select>").append("from <select id='removeProjectMembers' name='task'>");
        for (Task task : taskDao.listAMembersTasks(member.getId())) {
            sb.append("<option value='").append(task.getTaskId()).append("'>").append(task.getTaskName()).append("</option>");
        }
        sb.append("</select>" +
                "<button>Remove</button><br>" +
                "</form>");
    }
}
