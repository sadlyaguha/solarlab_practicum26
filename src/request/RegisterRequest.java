package request;

public class RegisterRequest {
    private final String login;
    private final String name;
    private final String email;
    private final String password;

    public RegisterRequest(String login, String name, String email, String password) {
        this.login = login;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getLogin() { return login; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
