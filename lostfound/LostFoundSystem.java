package lostfound;

import java.util.ArrayList;

/** Main logic of the portal: users, items, search, matching and claims. */
public class LostFoundSystem {
    private ArrayList<User> users;
    private ArrayList<Item> items;
    private FileManager fileManager;
    private Matchable matcher;
    private int nextId;

    public LostFoundSystem() {
        fileManager = new FileManager();
        matcher = new MatchingService();
        users = fileManager.loadUsers();
        items = fileManager.loadItems();

        // next id = highest existing id + 1
        nextId = 1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() >= nextId) {
                nextId = items.get(i).getId() + 1;
            }
        }
        // make sure a default admin exists
        if (findUser("admin") == null) {
            users.add(new Admin("admin", "Administrator", "admin123"));
            save();
        }
    }

    public void save() {
        fileManager.saveUsers(users);
        fileManager.saveItems(items);
    }

    private User findUser(String username) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                return users.get(i);
            }
        }
        return null;
    }

    public boolean register(String username, String name, String password) {
        if (findUser(username) != null) {
            return false;               // username already used
        }
        users.add(new Student(username, name, password));
        save();
        return true;
    }

    public User login(String username, String password) throws InvalidLoginException {
        User u = findUser(username);
        if (u == null || !u.checkPassword(password)) {
            throw new InvalidLoginException("Wrong username or password.");
        }
        return u;
    }

    public LostItem addLostItem(String name, String category, String location,
                                String date, String description, User user) {
        LostItem item = new LostItem(nextId, name, category, location, date, description, user.getUsername());
        nextId++;
        items.add(item);
        save();
        return item;
    }

    public FoundItem addFoundItem(String name, String category, String location, String date,
                                  String description, User user,
                                  String keptAt, String question, String answer) {
        FoundItem item = new FoundItem(nextId, name, category, location, date, description,
                user.getUsername(), keptAt, question, answer);
        nextId++;
        items.add(item);
        save();
        return item;
    }

    public ArrayList<Item> getAllItems() {
        return items;
    }

    // method overloading: two versions of search
    public ArrayList<Item> search(String keyword) {
        ArrayList<Item> result = new ArrayList<Item>();
        String k = keyword.toLowerCase();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getName().toLowerCase().contains(k)
                    || item.getDescription().toLowerCase().contains(k)
                    || item.getLocation().toLowerCase().contains(k)) {
                result.add(item);
            }
        }
        return result;
    }

    public ArrayList<Item> search(String keyword, String category) {
        ArrayList<Item> result = new ArrayList<Item>();
        ArrayList<Item> byKeyword = search(keyword);
        for (int i = 0; i < byKeyword.size(); i++) {
            if (byKeyword.get(i).getCategory().equalsIgnoreCase(category)) {
                result.add(byKeyword.get(i));
            }
        }
        return result;
    }

    public LostItem getLostItem(int id) throws ItemNotFoundException {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == id && items.get(i) instanceof LostItem) {
                return (LostItem) items.get(i);
            }
        }
        throw new ItemNotFoundException("No lost item with id " + id);
    }

    public FoundItem getFoundItem(int id) throws ItemNotFoundException {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == id && items.get(i) instanceof FoundItem) {
                return (FoundItem) items.get(i);
            }
        }
        throw new ItemNotFoundException("No found item with id " + id);
    }

    public int getScore(LostItem lost, FoundItem found) {
        return matcher.calculateScore(lost, found);
    }

    /** Returns found items matching the lost item, best score first. */
    public ArrayList<FoundItem> findMatches(LostItem lost) {
        ArrayList<FoundItem> matches = new ArrayList<FoundItem>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) instanceof FoundItem) {
                FoundItem f = (FoundItem) items.get(i);
                if (f.getStatus().equals("REPORTED")
                        && getScore(lost, f) >= MatchingService.MIN_SCORE) {
                    matches.add(f);
                }
            }
        }
        // bubble sort (highest score first)
        for (int i = 0; i < matches.size() - 1; i++) {
            for (int j = 0; j < matches.size() - 1 - i; j++) {
                if (getScore(lost, matches.get(j)) < getScore(lost, matches.get(j + 1))) {
                    FoundItem temp = matches.get(j);
                    matches.set(j, matches.get(j + 1));
                    matches.set(j + 1, temp);
                }
            }
        }
        return matches;
    }

    public boolean claim(int foundId, User user, String answer)
            throws ItemNotFoundException, AlreadyClaimedException {
        FoundItem f = getFoundItem(foundId);
        boolean correct = f.claim(user.getUsername(), answer);
        save();
        return correct;
    }

    public ArrayList<FoundItem> getPendingClaims() {
        ArrayList<FoundItem> list = new ArrayList<FoundItem>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) instanceof FoundItem && items.get(i).getStatus().equals("CLAIMED")) {
                list.add((FoundItem) items.get(i));
            }
        }
        return list;
    }

    public void approveReturn(int foundId) throws ItemNotFoundException {
        FoundItem f = getFoundItem(foundId);
        if (!f.getStatus().equals("CLAIMED")) {
            throw new ItemNotFoundException("Item #" + foundId + " has no pending claim.");
        }
        f.setStatus("RETURNED");
        save();
    }
}
