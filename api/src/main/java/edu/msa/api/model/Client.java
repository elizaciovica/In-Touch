package edu.msa.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import java.util.Objects;

@Entity(name = "Client")
public class Client extends AbstractEntity {

    @Id
    @Column(unique = true)
    private String firebaseId;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    public Client() {

    }

    public Client(String firebaseId, String firstName, String lastName, String username, String email) {
        this.firebaseId = firebaseId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
    }

    @PostPersist
    public void afterSave() {
        System.out.println("Saved the Client " + getFirebaseId());
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Client{" +
               "id=" + firebaseId +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client that)) {
            return false;
        }
        return Objects.equals(firebaseId, that.firebaseId) &&
               Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firebaseId, username);
    }
}

