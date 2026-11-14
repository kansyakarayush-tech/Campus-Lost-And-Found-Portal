package lostfound;

/** Abstract parent class of Student and Admin. */
public abstract class User {
    private String username;
    private String name;
    private String password;

    public User(String username, String name, String password) {
        this.username = username;
        this.name = name;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getPassword() { return password; }

    public boolean checkPassword(String input) {
        return password.equals(input);
    }

    // every child class must give its own role (polymorphism)
    public abstract String getRole();
}
