package lostfound;

public class FoundItem extends Item {
    private String keptAt;
    private String question;
    private String answer;
    private String claimedBy;

    public FoundItem(int id, String name, String category, String location, String date,
                     String description, String reportedBy,
                     String keptAt, String question, String answer) {
        super(id, name, category, location, date, description, reportedBy);
        this.keptAt = keptAt;
        this.question = question;
        this.answer = answer;
        this.claimedBy = "";
    }

    public String getKeptAt() { return keptAt; }
    public String getQuestion() { return question; }
    public String getClaimedBy() { return claimedBy; }
    public void setClaimedBy(String claimedBy) { this.claimedBy = claimedBy; }

    /** Returns true if the answer is correct. Throws exception if already claimed. */
    public boolean claim(String username, String givenAnswer) throws AlreadyClaimedException {
        if (!getStatus().equals("REPORTED")) {
            throw new AlreadyClaimedException("Item #" + getId() + " is already claimed or returned.");
        }
        if (answer.equalsIgnoreCase(givenAnswer.trim())) {
            claimedBy = username;
            setStatus("CLAIMED");
            return true;
        }
        return false;
    }

    public void displayDetails() {
        System.out.println("[FOUND #" + getId() + "] " + getName() + " | " + getCategory()
                + " | " + getLocation() + " | " + getDate() + " | kept at: " + keptAt
                + " | " + getStatus());
    }

    public String toFileLine() {
        return "FOUND|" + commonFields() + "|" + keptAt + "|" + question + "|" + answer + "|" + claimedBy;
    }
}
