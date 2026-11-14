package lostfound;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

/** Console menu of the Campus Lost & Found Portal. */
public class Main {
    static Scanner sc = new Scanner(System.in);
    static LostFoundSystem system = new LostFoundSystem();
    static final String[] CATEGORIES = {"ELECTRONICS", "DOCUMENTS", "ACCESSORIES", "BOOKS", "KEYS", "OTHERS"};

    public static void main(String[] args) {
        System.out.println("===== Campus Lost & Found Portal =====");
        int choice = -1;
        while (choice != 0) {
            System.out.println("\n1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");
            choice = readInt("Enter choice: ");
            if (choice == 1) {
                login();
            } else if (choice == 2) {
                register();
            } else if (choice != 0) {
                System.out.println("Invalid choice.");
            }
        }
        System.out.println("Goodbye!");
    }

    static void register() {
        String username = ask("Username");
        String name = ask("Full name");
        String password = ask("Password");
        if (system.register(username, name, password)) {
            System.out.println("Registered successfully. Please login.");
        } else {
            System.out.println("Username already taken.");
        }
    }

    static void login() {
        try {
            User user = system.login(ask("Username"), ask("Password"));
            System.out.println("Welcome, " + user.getName() + " (" + user.getRole() + ")");
            if (user instanceof Admin) {
                adminMenu();
            } else {
                studentMenu(user);
            }
        } catch (InvalidLoginException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void studentMenu(User user) {
        int choice = -1;
        while (choice != 0) {
            System.out.println("\n--- Student Menu ---");
            System.out.println("1. Report LOST item");
            System.out.println("2. Report FOUND item");
            System.out.println("3. View all items");
            System.out.println("4. Search items");
            System.out.println("5. Check matches for my lost item");
            System.out.println("6. Claim a found item");
            System.out.println("0. Logout");
            choice = readInt("Enter choice: ");
            try {
                if (choice == 1) {
                    reportLost(user);
                } else if (choice == 2) {
                    reportFound(user);
                } else if (choice == 3) {
                    showItems(system.getAllItems());
                } else if (choice == 4) {
                    searchItems();
                } else if (choice == 5) {
                    LostItem lost = system.getLostItem(readInt("Your lost item id: "));
                    showMatches(lost);
                } else if (choice == 6) {
                    claimItem(user);
                } else if (choice != 0) {
                    System.out.println("Invalid choice.");
                }
            } catch (ItemNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (AlreadyClaimedException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    static void adminMenu() {
        int choice = -1;
        while (choice != 0) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. View all items");
            System.out.println("2. View pending claims");
            System.out.println("3. Approve return");
            System.out.println("0. Logout");
            choice = readInt("Enter choice: ");
            if (choice == 1) {
                showItems(system.getAllItems());
            } else if (choice == 2) {
                ArrayList<FoundItem> pending = system.getPendingClaims();
                if (pending.size() == 0) {
                    System.out.println("No pending claims.");
                }
                for (int i = 0; i < pending.size(); i++) {
                    pending.get(i).displayDetails();
                    System.out.println("    claimed by: " + pending.get(i).getClaimedBy());
                }
            } else if (choice == 3) {
                try {
                    system.approveReturn(readInt("Found item id: "));
                    System.out.println("Item marked as RETURNED.");
                } catch (ItemNotFoundException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else if (choice != 0) {
                System.out.println("Invalid choice.");
            }
        }
    }

    static void reportLost(User user) {
        String name = ask("Item name");
        String category = askCategory();
        String location = ask("Place where lost");
        String date = askDate();
        String description = ask("Description");
        LostItem item = system.addLostItem(name, category, location, date, description, user);
        System.out.println("Lost report saved. Your item id is " + item.getId());
        showMatches(item);
    }

    static void reportFound(User user) {
        String name = ask("Item name");
        String category = askCategory();
        String location = ask("Place where found");
        String date = askDate();
        String description = ask("Description");
        String keptAt = ask("Where is it kept now");
        String question = ask("Verification question (only owner can answer)");
        String answer = ask("Correct answer");
        FoundItem item = system.addFoundItem(name, category, location, date, description,
                user, keptAt, question, answer);
        System.out.println("Found report saved. Item id is " + item.getId());
    }

    static void searchItems() {
        String keyword = ask("Keyword (name/description/place)");
        String category = ask("Category (press Enter to skip)");
        ArrayList<Item> result;
        if (category.equals("")) {
            result = system.search(keyword);
        } else {
            result = system.search(keyword, category);
        }
        showItems(result);
    }

    static void showMatches(LostItem lost) {
        ArrayList<FoundItem> matches = system.findMatches(lost);
        if (matches.size() == 0) {
            System.out.println("No matches found yet.");
            return;
        }
        System.out.println("Possible matches:");
        for (int i = 0; i < matches.size(); i++) {
            matches.get(i).displayDetails();
            System.out.println("    match score: " + system.getScore(lost, matches.get(i)) + "%");
        }
    }

    static void claimItem(User user) throws ItemNotFoundException, AlreadyClaimedException {
        FoundItem f = system.getFoundItem(readInt("Found item id: "));
        System.out.println("Question: " + f.getQuestion());
        String answer = ask("Your answer");
        if (system.claim(f.getId(), user, answer)) {
            System.out.println("Correct! Your claim is sent to the admin for approval.");
        } else {
            System.out.println("Wrong answer. Claim rejected.");
        }
    }

    static void showItems(ArrayList<Item> list) {
        if (list.size() == 0) {
            System.out.println("No items found.");
        }
        for (int i = 0; i < list.size(); i++) {
            list.get(i).displayDetails();     // polymorphism
        }
    }

    // ---------- helper methods for input ----------
    static String ask(String label) {
        System.out.print(label + ": ");
        String text = sc.nextLine().trim();
        return text.replace("|", " ");        // '|' is used as separator in files
    }

    static int readInt(String label) {
        while (true) {
            try {
                System.out.print(label);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    static String askCategory() {
        System.out.println("Categories:");
        for (int i = 0; i < CATEGORIES.length; i++) {
            System.out.println("  " + (i + 1) + ". " + CATEGORIES[i]);
        }
        int n = readInt("Choose category number: ");
        while (n < 1 || n > CATEGORIES.length) {
            n = readInt("Invalid. Choose 1-" + CATEGORIES.length + ": ");
        }
        return CATEGORIES[n - 1];
    }

    static String askDate() {
        while (true) {
            String d = ask("Date (yyyy-mm-dd)");
            try {
                LocalDate.parse(d);
                return d;
            } catch (Exception e) {
                System.out.println("Wrong format. Example: 2026-11-10");
            }
        }
    }
}
