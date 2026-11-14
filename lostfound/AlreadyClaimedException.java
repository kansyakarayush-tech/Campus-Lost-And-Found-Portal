package lostfound;

/** Custom exception: AlreadyClaimedException */
public class AlreadyClaimedException extends Exception {
    public AlreadyClaimedException(String message) {
        super(message);
    }
}
