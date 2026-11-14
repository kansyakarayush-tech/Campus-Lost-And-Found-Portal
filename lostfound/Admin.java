package lostfound;

public class Admin extends User {
    public Admin(String username, String name, String password) {
        super(username, name, password);
    }

    public String getRole() {
        return "ADMIN";
    }
}
