package xyz.skyjumper409.chessengine;

import static xyz.skyjumper409.chessengine.CoordUtils.validateCoord;
public class Square {
    private final int col, row;
    private Piece piece;

    public Square(int col, int row) {
        validateCoord(col, row);
        this.col = col;
        this.row = row;
    }
    public Square(int[] coord) {
        validateCoord(coord);
        this.col = coord[0];
        this.row = coord[1];
    }
    public int[] getICoord() {
        return new int[]{col, row};
    }
    public String getCoord() {
        return Character.toChars(col+97)[0] + String.valueOf(row+1);
    }
    public boolean isEmpty() {
        return piece == null;
    }
    public Piece getPiece() {
        return piece;
    }
    public Piece popPiece() {
        Piece p = this.piece;
        this.piece = null;
        return p;
    }
    public void setPiece(Piece p) {
        this.piece = p;
    }
    @Override
    public String toString() {
        return String.format("%s: {coord: %s (%d, %d), piece: %s}", getClass().getName(), getCoord(), col, row, piece == null ? "null" : piece.toString());
    }
}
