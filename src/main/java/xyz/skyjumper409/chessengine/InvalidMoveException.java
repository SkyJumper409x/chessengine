package xyz.skyjumper409.chessengine;

public class InvalidMoveException extends RuntimeException {
    public InvalidMoveException() {
        super();
    }
    public InvalidMoveException(String message) {
        super(message);
    }
    public InvalidMoveException(Throwable cause) {
        super(cause);
    }
    public InvalidMoveException(String message, Throwable cause) {
        super(message, cause);
    }
    public static InvalidMoveException fromMove(Move move) {
        return fromMove(move.rawMoveString(), "");
    }
    public static InvalidMoveException fromMove(Move move, String additionalMessage) {
        return fromMove(move.rawMoveString(), additionalMessage);
    }
    public static InvalidMoveException fromMove(String move) {
        return fromMove(move, "");
    }
    public static InvalidMoveException fromMove(String move, String additionalMessage) {
        return new InvalidMoveException("xyz.skyjumper409.chessengine.InvalidMoveException: The move " + move + " is not allowed. " + additionalMessage);
    }
}
