package sample;

public class WinLoseException extends Exception{
    boolean ifWon;
    public WinLoseException(Boolean ifWon) {
        this.ifWon=ifWon;
    }
}
