package lostfound;

public class LostItem extends Item {
    public LostItem(int id, String name, String category, String location,
                    String date, String description, String reportedBy) {
        super(id, name, category, location, date, description, reportedBy);
    }

    public void displayDetails() {
        System.out.println("[LOST  #" + getId() + "] " + getName() + " | " + getCategory()
                + " | " + getLocation() + " | " + getDate() + " | " + getStatus());
    }

    public String toFileLine() {
        return "LOST|" + commonFields();
    }
}
