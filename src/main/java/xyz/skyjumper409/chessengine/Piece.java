package xyz.skyjumper409.chessengine;

import java.util.Objects;

public class Piece {
    public static enum Color {
        BLACK(1), WHITE(0);
        private Color(int iValue) {
            this.iValue = (byte) iValue;
        }
        private final byte iValue;
        public byte numValue() {
            return this.iValue;
        }
        public static Color invert(Color c) {
            if(c == WHITE) {
                return BLACK;
            } else if(c == BLACK) {
                return WHITE;
            }
            return c;
        }
    }

    public final PieceConstants constants;
    public final Color color;

    private Piece(PieceConstants constants, Color color) {
        this.constants = constants;
        this.color = color;
    }
    private Piece(PieceConstants constants) {
        this(constants,Color.WHITE);
    }
    public static Piece create(PieceConstants pieceConstants, Color pieceColor) {
        Objects.requireNonNull(pieceConstants);
        Objects.requireNonNull(pieceColor);
        return new Piece(pieceConstants, pieceColor);
    }
    static Piece create(PieceConstants pieceConstants) {
        Objects.requireNonNull(pieceConstants);
        return new Piece(pieceConstants);
    }
}
