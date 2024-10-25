package xyz.skyjumper409.chessengine;

public enum PieceConstants {
    PAWN("Pawn", 1, 'P', 0, "Bauer"), KNIGHT("Knight", 3, 'N', 1, "Springer"), BISHOP("Bishop", 3, 'B', 2, "Läufer"), ROOK("Rook", 5, 'R', 3, "Turm"), QUEEN("Queen", 9, 'Q', 4, "Dame"), KING("King", 0, 'K', 5, "König");

    PieceConstants(String name, int pointValue, char initial, int index, String germanName) {
        this.name = name;
        this.germanName = germanName;
        this.initial = initial;
        this.pointValue = (byte) pointValue;
        this.index = (byte) pointValue;
    }

    public static boolean useGerman = false;
    
    private final String name, germanName;
    private final char initial;
    private final byte pointValue;
    private final byte index;

    public String getLocalizedName() {
        return useGerman ? germanName : name;
    }
    public String getName() {
        return name;
    }

    public byte getPointValue() {
        return pointValue;
    }
    public char getInitial() {
        return initial;
    }
    public byte getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return String.format("%s@%x: {name: %s, pointValue: %d, initial: %s index: %d}", this.getClass().getName(), hashCode(), getLocalizedName(), pointValue, initial, index);
    }
}
