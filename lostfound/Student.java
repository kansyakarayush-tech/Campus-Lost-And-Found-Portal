package lostfound;

public class Student extends User {
    public Student(String username, String name, String password) {
        super(username, name, password);
    }

    public String getRole() {
        return "STUDENT";
    }
}
