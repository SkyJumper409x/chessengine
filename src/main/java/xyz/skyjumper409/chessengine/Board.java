package xyz.skyjumper409.chessengine;

import java.util.TreeMap;
import static xyz.skyjumper409.chessengine.CoordUtils.validateCoord;

public class Board implements Cloneable {
    // first index is colummn/letter, second index is row/number. null = empty square
    private Square[][] squareMatrix;

    // for access via String coord
    private TreeMap<String, Square> squareMap = new TreeMap<String, Square>(new java.util.Comparator<String>() {
        public int compare(String arg0, String arg1) {
            return arg0.toLowerCase().compareTo(arg1.toLowerCase());
        }        
    });

    public Board() {
        this.squareMatrix = createEmptySquareMatrix();
        for (int i = 0; i < squareMatrix.length; i++) {
            for (int j = 0; j < squareMatrix[i].length; j++) {
                squareMap.put(squareMatrix[i][j].getCoord(), squareMatrix[i][j]);
            }
        }
        assert(squareMatrix.length == 8 && squareMatrix[0].length == 8 && squareMap.size() == 64) : "sanity check fail lol (at the end of Board() constructor)";
    }

    private static Square[][] createEmptySquareMatrix() {
        Square[][] squareMatrix = new Square[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squareMatrix[i][j] = new Square(i,j);
            }
        }
        return squareMatrix;
    }

    void clearPosition() {
        for (Square[] squares : squareMatrix) {
            for (Square square : squares) {
                square.setPiece(null);
            }
        }
    }
    void resetPosition() {
        for (int col = 0; col < 4; col++) {
            setPieceMirrored(col, 1, Piece.create(PieceConstants.PAWN), 2);
            setPieceMirrored(col, 2, null, 2);
            setPieceMirrored(col, 3, null, 2);
        }
        setPieceMirrored(0, 0, Piece.create(PieceConstants.ROOK), 2);
        setPieceMirrored(1, 0, Piece.create(PieceConstants.KNIGHT), 2);
        setPieceMirrored(2, 0, Piece.create(PieceConstants.BISHOP), 2);
        setPieceMirrored(3, 0, Piece.create(PieceConstants.QUEEN), 1);
        setPieceMirrored(4, 0, Piece.create(PieceConstants.KING), 1);
    }
    private void setPieceMirrored(int col, int row, Piece piece, int axis) { // util for resetPosition() 0 is vert 1 is horiz 2 is vert then horiz
        setPiece(col , row, piece);
        if(axis % 2 == 0) {
            setPiece(7-col,row, piece == null ? null : Piece.create(piece.constants, piece.color));
            if(axis == 2) {
                setPieceMirrored(col, row, piece, 1);
                setPieceMirrored(7-col,row, piece, 1);
            }
        } else {
            setPiece(col,7-row, piece == null ? null : Piece.create(piece.constants, Piece.Color.invert(piece.color)));
        }
    }
    private void setPieceMirrored(int[] iCoord, Piece piece, int axis) {
        setPieceMirrored(iCoord[0], iCoord[1], piece,axis);
    }

    void setPiece(String coord, Piece piece) {
        validateCoord(coord);
        squareMap.get(coord).setPiece(piece);
    }
    void setPiece(int[] iCoord, Piece piece) {
        validateCoord(iCoord);
        setPiece(iCoord[0], iCoord[1], piece);
    }
    void setPiece(int col, int row, Piece piece) {
        validateCoord(col, row);
        squareMatrix[col][row].setPiece(piece);
    }
    public Piece pieceAt(int row, int col) {
        validateCoord(row, col);
        return squareMatrix[row][col].getPiece();
    }
    public Piece pieceAt(int[] iCoord) {
        return pieceAt(iCoord[0], iCoord[1]);
    }
    public Piece pieceAt(String coord) {
        validateCoord(coord);
        return squareMap.get(coord).getPiece();
    }

    public java.util.ArrayList<int[]> findPieceCoords(PieceConstants p, Piece.Color c) {
        return findPieceCoords(new PieceConstants[]{p}, new Piece.Color[]{c});
    }
    public java.util.ArrayList<int[]> findPieceCoords(PieceConstants[] ps, Piece.Color[] colors) {
        java.util.ArrayList<int[]> foundLocations = new java.util.ArrayList<>();
        int loopcount = 0;
        for(int col = 0; col < 8; col++) {
            for(int row = 0; row < 8; row++) {
                // if(row == 7 && Game.itison) {
                //     System.out.println("meow");
                // }
                Square square = squareMatrix[col][row];
                if(!square.isEmpty()) { 
                    Piece piece = square.getPiece();
                    for(int i = 0; i < ps.length; i++) {
                        if(piece.constants == ps[i] && (colors == null || i >= colors.length || colors[i] == null || piece.color == colors[i])) {
                            foundLocations.add(square.getICoord());
                        }
                    }
                    loopcount++;
                    if(loopcount > 255) {
                        System.out.println("loopcount too high, exiting");
                        System.exit(1);
                    }
                }
            }
        }
        return foundLocations;
    }
    public java.util.ArrayList<int[]> findPieceCoords(PieceConstants[] ps, Piece.Color[] colors, int col, int row, boolean limitToCol, boolean limitToRow) {
        java.util.ArrayList<int[]> foundLocations = new java.util.ArrayList<>();
        if(!limitToCol) {
            col = 0;
        }
        if(!limitToRow) {
            row = 0;
        }
        int loopcount = 0;
        byte c = (byte) col;
        while((limitToCol && c == col) || (!limitToCol && c < 8)) {
            byte r = (byte) row;
            while((limitToRow && r == row) || (!limitToRow && r < 8)) {
                Square square  = null;
                try {
                    square = squareMatrix[c][r];
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println();
                }
                if(!square.isEmpty()) { 
                    Piece piece = square.getPiece();
                    for(int i = 0; i < ps.length; i++) {
                        if(piece.constants == ps[i] && (colors == null || i >= colors.length || colors[i] == null || piece.color == colors[i])) {
                            foundLocations.add(square.getICoord());
                        }
                    }
                }
                loopcount++;
                if(loopcount > 255) {
                    System.out.println("loopcount too high, exiting");
                    System.exit(1);
                }
                r++;
            }
            c++;
        }
        return foundLocations;
    }

    @Override
    public Board clone() {
        Board b = new Board();
        for (Square[] squares : squareMatrix) {
            for (Square square : squares) {
                b.setPiece(square.getCoord(), square.getPiece());
            }
        }
        return b;
    }
    public String toString() {
        StringBuilder result = new StringBuilder("a b c d e f g h\n");
        for (int row = squareMatrix.length-1; row >= 0; row--) {
            for (int col = 0; col < squareMatrix.length; col++) {
                Piece p = pieceAt(col, row);
                if(p == null) {
                    result = result.append("-");
                } else {
                    if(p.color == Piece.Color.WHITE) {
                        result = result.append(p.constants.getInitial());
                    } else if(p.color == Piece.Color.BLACK) {
                        result = result.append(Character.toLowerCase(p.constants.getInitial()));
                    } else {
                        result.append("Ω");
                    }
                }
                result = result.append(" ");
            }
            result = result.append(String.valueOf(row+1)).append("\n");
        }
        return result.toString();
    }
}
