package no.eksamenPGR203.database;

import no.eksamenPGR203.PasswordHash;

import java.security.NoSuchAlgorithmException;

public class ProjectMember {
    private String firstName;
    private String lastName;
    private String emailaddress;
    private String hashedPw;
    private int id;

    public ProjectMember(String firstName, String lastName, String email, String password) throws NoSuchAlgorithmException {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailaddress = email;
        this.hashedPw = PasswordHash.md5Hash(password);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailaddress() {
        return emailaddress;
    }

    public String getHashedPw() {
        return hashedPw;
    }


}
