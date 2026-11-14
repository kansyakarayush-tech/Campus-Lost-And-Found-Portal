package lostfound;

/** Interface: any matching method must give a score for a lost/found pair. */
public interface Matchable {
    int calculateScore(LostItem lost, FoundItem found);
}
