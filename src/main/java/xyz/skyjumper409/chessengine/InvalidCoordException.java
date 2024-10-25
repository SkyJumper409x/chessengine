package xyz.skyjumper409.chessengine;

import xyz.skyjumper409.util.ArrayUtils;

public class InvalidCoordException extends RuntimeException {
    public InvalidCoordException() {
        super();
    }
    public InvalidCoordException(String message) {
        super(message);
    }
    public InvalidCoordException(Throwable cause) {
        super(cause);
    }
    public InvalidCoordException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidCoordException fromCoord(int[] coord) {
        return fromCoord(coord, "");
    }
    public static InvalidCoordException fromCoord(int[] coord, String additionalMessage) {
        if(coord == null) {
            return fromCoord("null", additionalMessage);
        } else if(coord.length != 2) {
            return fromCoord(ArrayUtils.join(false, ArrayUtils.integerValuesOf(coord)), additionalMessage);
        }
        return fromCoord(coord[0], coord[1], additionalMessage);
    }

    public static InvalidCoordException fromCoord(int col, int row) {
        return fromCoord(col, row, "");
    }
    public static InvalidCoordException fromCoord(int col, int row, String additionalMessage) {
        return fromCoord(col >= 0 && col <= 25 ? String.valueOf(Character.toChars(col+97)[0]) + String.valueOf(row+1) : ("(" + col + ", " + row + ") (0-based)"), additionalMessage);
    }
    public static InvalidCoordException fromCoord(String coord) {
        return fromCoord(coord, "");
    }
    public static InvalidCoordException fromCoord(String coord, String additionalMessage) {
        return new InvalidCoordException("xyz.skyjumper409.chessengine.InvalidCoordinateException: The coord " + coord + " is not allowed. " + additionalMessage);
    }
}
