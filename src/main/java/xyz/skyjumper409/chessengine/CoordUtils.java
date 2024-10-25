package xyz.skyjumper409.chessengine;

public class CoordUtils {
    
    public static int[] coordToPositionIndexes(String coord) {
        validateCoord(coord);

        int[] coords = new int[2];
        coords[0] = Character.toLowerCase(coord.charAt(0))-97;
        coords[1] = coord.charAt(1)-49;
        return coords;
    }
    public static boolean isValidCoord(String coord) {
        if(coord == null || coord.length() != 2) {
            System.out.println("Coord string is null or coord.length() != 2");
            return false;
        }
        if(!isValidCol(coord.charAt(0))) {
            System.out.println("Coord string charAt(0) isn't a valid column (coord string is \"" + coord + "\")");
            return false;
        }
        if(!isValidRow(coord.charAt(1))) {
            System.out.println("Coord string charAt(1) isn't a valid row (coord string is \"" + coord + "\")");
            return false;
        }
        return true;
    }
    public static boolean isValidCoord(int col, int row) { 
        return isValidColZeroBased(col) && isValidRowZeroBased(row);
    }
    public static boolean isValidCoord(int[] iCoord) {
        // if(iCoord == null || iCoord.length != 2 || iCoord[0] < 0 || iCoord[0] > 7 || iCoord[1] < 0 || iCoord[1] > 7) {
        //     return false;
        // }
        // return true;
        return iCoord != null && iCoord.length == 2 && isValidCoord(iCoord[0], iCoord[1]);
    }

    static void validateCoord(int col, int row) {
        if(!isValidCoord(col, row)) {
            throw InvalidCoordException.fromCoord(col, row, "in CoordUtils");
        }
    }
    static void validateCoord(int[] iCoord) {
        if(!isValidCoord(iCoord)) {
            throw InvalidCoordException.fromCoord(iCoord, "in CoordUtils");
        }
    }
    static void validateCoord(String coord) {
        if(!isValidCoord(coord)) {
            throw InvalidCoordException.fromCoord(coord, "in CoordUtils");
        }
    }
    

    public static boolean isValidColZeroBased(int i) {
        return isValidRowNotIncremented(i);
    }
    public static boolean isValidCol(char c) {
        return isValidRowNotIncremented(c-97); // 'a' = 97
    }
    public static boolean isValidCol(String s) {
        return isValidRowIncremented(s);
    }

    public static boolean isValidRow(int i) {
        return isValidRowIncremented(i);
    }
    public static boolean isValidRow(char c) {
        return isValidRowIncremented(c);
    }
    public static boolean isValidRow(String s) {
        return isValidRowIncremented(s);
    }
    public static boolean isValidRowZeroBased(int i) {
        return isValidRowNotIncremented(i);
    }
    public static boolean isValidRowZeroBased(char c) {
        return isValidRowNotIncremented(c);
    }
    public static boolean isValidRowZeroBased(String s) {
        return isValidRowNotIncremented(s);
    }
    // these private static methods are there to reduce duplicate/chaotic code
    private static boolean isValidRowModified(int i, int increment) {
        return (i >= increment && i <= (7+increment));
    }
    private static boolean isValidRowModified(char c, int increment) {
        return isValidRowModified((int) (c - 48), increment);
    }
    private static boolean isValidRowModified(String s, int increment) {
        if(s.length() == 1) {
            return isValidRowModified(s.charAt(0), increment);
        }
        return false;
    }
    private static boolean isValidRowNotIncremented(int i) {
        return isValidRowModified(i, 0);
    }
    private static boolean isValidRowNotIncremented(char c) {
        return isValidRowModified(c, 0);
    }
    private static boolean isValidRowNotIncremented(String s) {
        return isValidRowModified(s, 0);
    }
    private static boolean isValidRowIncremented(int i) {
        return isValidRowModified(i, 1);
    }
    private static boolean isValidRowIncremented(char c) {
        return isValidRowModified(c, 1);
    }
    private static boolean isValidRowIncremented(String s) {
        return isValidRowModified(s, 1);
    }
}
