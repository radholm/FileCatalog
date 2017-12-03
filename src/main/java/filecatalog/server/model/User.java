package filecatalog.server.model;

import javax.persistence.*;
import java.io.Serializable;

@NamedQueries({
        @NamedQuery(
                name = "findUserByName",
                query = "SELECT user FROM User user WHERE user.username LIKE :username"
        ),
        @NamedQuery(
                name = "deleteUserByName",
                query = "DELETE FROM User user WHERE user.username LIKE :username"
        )
})

@Entity(name = "User")
public class User implements Serializable{
    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userID;

    @Column(name = "username",nullable = false)
    private String username;

    @Column(name = "password",nullable = false)
    private String password;

    public User() {
        this(null,null);
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public long getUserID() {
        return userID;
    }
}
