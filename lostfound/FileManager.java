package lostfound;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/** Saves and loads data from simple text files (one record per line). */
public class FileManager {
    private static final String USER_FILE = "users.txt";
    private static final String ITEM_FILE = "items.txt";

    public void saveUsers(ArrayList<User> users) {
        try {
            FileWriter fw = new FileWriter(USER_FILE);
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                fw.write(u.getRole() + "|" + u.getUsername() + "|" + u.getName()
                        + "|" + u.getPassword() + "\n");
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Could not save users: " + e.getMessage());
        }
    }

    public void saveItems(ArrayList<Item> items) {
        try {
            FileWriter fw = new FileWriter(ITEM_FILE);
            for (int i = 0; i < items.size(); i++) {
                fw.write(items.get(i).toFileLine() + "\n");
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Could not save items: " + e.getMessage());
        }
    }

    public ArrayList<User> loadUsers() {
        ArrayList<User> users = new ArrayList<User>();
        File file = new File(USER_FILE);
        if (!file.exists()) {
            return users;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|", -1);
                if (p.length < 4) continue;
                if (p[0].equals("ADMIN")) {
                    users.add(new Admin(p[1], p[2], p[3]));
                } else {
                    users.add(new Student(p[1], p[2], p[3]));
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Could not read users: " + e.getMessage());
        }
        return users;
    }

    public ArrayList<Item> loadItems() {
        ArrayList<Item> items = new ArrayList<Item>();
        File file = new File(ITEM_FILE);
        if (!file.exists()) {
            return items;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|", -1);
                Item item = null;
                if (p[0].equals("LOST") && p.length >= 9) {
                    item = new LostItem(Integer.parseInt(p[1]), p[2], p[3], p[4], p[5], p[6], p[7]);
                } else if (p[0].equals("FOUND") && p.length >= 13) {
                    FoundItem f = new FoundItem(Integer.parseInt(p[1]), p[2], p[3], p[4], p[5],
                            p[6], p[7], p[9], p[10], p[11]);
                    f.setClaimedBy(p[12]);
                    item = f;
                }
                if (item != null) {
                    item.setStatus(p[8]);
                    items.add(item);
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Could not read items: " + e.getMessage());
        }
        return items;
    }
}
