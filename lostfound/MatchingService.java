package lostfound;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Gives a score out of 100: category 40, location 30, date 20, name keyword 10. */
public class MatchingService implements Matchable {

    public static final int MIN_SCORE = 50;

    public int calculateScore(LostItem lost, FoundItem found) {
        int score = 0;

        if (lost.getCategory().equalsIgnoreCase(found.getCategory())) {
            score = score + 40;
        }
        if (lost.getLocation().equalsIgnoreCase(found.getLocation())) {
            score = score + 30;
        }

        LocalDate d1 = LocalDate.parse(lost.getDate());
        LocalDate d2 = LocalDate.parse(found.getDate());
        long days = Math.abs(ChronoUnit.DAYS.between(d1, d2));
        if (days <= 2) {
            score = score + 20;
        }

        if (haveCommonWord(lost.getName(), found.getName())) {
            score = score + 10;
        }
        return score;
    }

    // checks if the two names share at least one word (3+ letters)
    private boolean haveCommonWord(String a, String b) {
        String[] wordsA = a.toLowerCase().split(" ");
        String[] wordsB = b.toLowerCase().split(" ");
        for (int i = 0; i < wordsA.length; i++) {
            for (int j = 0; j < wordsB.length; j++) {
                if (wordsA[i].length() >= 3 && wordsA[i].equals(wordsB[j])) {
                    return true;
                }
            }
        }
        return false;
    }
}
