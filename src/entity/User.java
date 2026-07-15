package entity;

import java.io.Serializable;

public class User implements Entity<Long>, Serializable {
    private static final long serialVersionUID = 1L;

    public enum Role {
        USER, ADMIN
    }

    private final long id;
    private String login;
    private String name;
    private String email;
    private String password;
    private Role role;
    private boolean blocked;

    public User(long id, String login, String name, String email, String password, Role role) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.blocked = false;
    }

    @Override
    public Long getId() { return id; }

    public String getLogin() { return login; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public boolean isBlocked() { return blocked; }

    public void setLogin(String login) { this.login = login; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }

    public boolean isAdmin() { return role == Role.ADMIN; }

    @Override
    public String toString() {
        return "User{id=" + id + ", login='" + login + "', name='" + name +
               "', email='" + email + "', role=" + role +
               (blocked ? ", BLOCKED" : "") + "}";
    }
}
