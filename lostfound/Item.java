package lostfound;

/** Abstract parent class of LostItem and FoundItem. */
public abstract class Item {
    private int id;
    private String name;
    private String category;
    private String location;
    private String date;          // format yyyy-mm-dd
    private String description;
    private String reportedBy;
    private String status;        // REPORTED, CLAIMED or RETURNED

    public Item(int id, String name, String category, String location,
                String date, String description, String reportedBy) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.location = location;
        this.date = date;
        this.description = description;
        this.reportedBy = reportedBy;
        this.status = "REPORTED";
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
    public String getReportedBy() { return reportedBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // common fields written to the file
    protected String commonFields() {
        return id + "|" + name + "|" + category + "|" + location + "|" + date
                + "|" + description + "|" + reportedBy + "|" + status;
    }

    public abstract void displayDetails();

    public abstract String toFileLine();
}
