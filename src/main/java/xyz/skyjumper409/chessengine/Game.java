package xyz.skyjumper409.chessengine;

import static xyz.skyjumper409.chessengine.Piece.Color;
import static xyz.skyjumper409.chessengine.Piece.Color.*; // teehee

import java.util.ArrayList;

public class Game {
    Board board;
    private boolean allowedCastles[][] = new boolean[][]{{true, true},{true, true}}; // first index is color, second index is 0 for kingside and 1 for queenside
    @SuppressWarnings("unchecked")
    private ArrayList<Piece>[] takenPieces = (ArrayList<Piece>[]) new ArrayList[] {new ArrayList<Piece>(), new ArrayList<Piece>()}; // for the piece color iValues, contains the pieces that color has taken. so white's index (0) contains the black pieces that were taken by white.
    private Color colorToMove = WHITE;
    private boolean gameOver = false;
    private byte lastGameResult = -1; // 0 = color with numValue 0 won, 1 = color with numValue 1 won, 2 = draw
    private GameScore score;
    public static final class GameScore {
        private short[] playerScores = new short[2];
        private short drawCount = 0; // to avoid floating point values in the scores

        public void addWin(Color c) {
            playerScores[c.numValue()]++;
        }
        public void addWhiteWin() {
            addWin(WHITE);
        }
        public void addBlackWin() {
            addWin(BLACK);
        }
        public void addDraw() {
            drawCount++;
        }

        public int getWins(Color c) {
            return playerScores[c.numValue()];
        }
        public float getScore(Color c) {
            return getWins(c) + (drawCount / 2f);
        }
        public float getWhiteScore() {
            return getScore(WHITE);
        }
        public float getBlackScore() {
            return getScore(BLACK);
        }

        private void reset() {
            playerScores = new short[2];
            drawCount = 0;
        }
    }
    public Game() {
        // standard position
        board = new Board();
        board.resetPosition();
        score = new GameScore();
    }
    public void reset() {
        board.resetPosition();
        takenPieces[0].clear();
        takenPieces[1].clear();

        gameOver = false;
        lastGameResult = -1;
        colorToMove = WHITE;
    }
    void fullReset() {
        reset();
        score.reset();
    }
    public boolean isGameOver() {
        return gameOver;
    }
    public byte getLastGameResult() {
        return lastGameResult;
    }
    public Piece[] getPiecesTakenBy(Color c) {
        return takenPieces[c.numValue()].toArray(new Piece[0]);
    }

    // for checking en passant
    Object[] previousMoveTokens = new Object[5]; // pieceChar, startCoord, takeTargetPiece, targetCoord, raw move string

    // moves always have to be double disambiugated (for now), eg "Bb4xc5 instead of "Bxc5"
    public boolean isValidMove(Move move) {
        char pieceChar = move.pieceChar();

        int[] startCoord = move.startCoord();
        int[] targetCoord = move.targetCoord();

        boolean takeTargetPiece = move.takeTargetPiece();
        boolean isCastle = move.isCastle(), isCastleShort = move.isCastleShort(), isCastleLong = move.isCastleLong();

        if(isCastle) {
            if(pieceChar != 'K') {
                System.out.println("isCastle is true but pieceChar isn't 'K', this shouldn't happen.");
                return false;
            }
            if(isKingInCheck(board, colorToMove)) {
                System.out.println("Cannot castle out of check");
                return false;
            }
            if(isCastleShort && !allowedCastles[colorToMove.numValue()][0]) {
                System.out.println("Castling short is not allowed");
                return false;
            }
            if(isCastleLong && !allowedCastles[colorToMove.numValue()][1]) {
                System.out.println("Castling long is not allowed");
                return false;
            }
            if(takeTargetPiece) {
                System.out.println("Castling may not take any pieces (this shouldn't happen)");
                return false;
            }
            startCoord = new int[]{ 4, (colorToMove.numValue()*7) };
            targetCoord = new int[] { 2, (colorToMove.numValue()*7) };
            if(board.pieceAt(startCoord) == null || board.pieceAt(startCoord).color != colorToMove) {
                // this shouln't be reachable because, if the king moved off its starting square, both allowedCastles should be false.
                System.out.println("Invalid startCoord for castling (this shouldnt happen)");
            }
            if(isCastleShort) {
                targetCoord[0] = 6;
            }
        } else {
            // these checks aren't needed when a move is castles, since they are dealt with seperately for castling moves
            if(targetCoord[0] == startCoord[0] && targetCoord[1] == startCoord[1]) {
                System.out.println("End and start coords are identical");
                return false;
            }
            if(board.pieceAt(startCoord) == null || board.pieceAt(startCoord).color != colorToMove) {
                System.out.println("Invalid startCoord");
                return false;
            }
            /* 
            * This code isnt even sightreadable (so let me explain it :3)
            * if there is no piece at the target location, the move must not contain an x (as it is not taking anything)
            * and if there is a piece at the target location, the move must contain an x
            * an exception is en passant, which is why this check assumes the taking part of the move is valid if the pieceChar is 'P', 
            * because it will be checked later by the code that is repsonsible for diagonal pawn moves.
            */
            if(((board.pieceAt(targetCoord) == null) == takeTargetPiece) && (pieceChar != 'P')) {
                System.out.println("(board.pieceAt(targetCoord) == null) == takeTargetPiece");
                return false;
            }
            if(board.pieceAt(targetCoord) != null && board.pieceAt(targetCoord).color == colorToMove) {
                System.out.println("Tried to take piece of own color");
                return false;
            }
        }
        
        int distanceHoriz = targetCoord[0] - startCoord[0];
        int distanceVert = targetCoord[1] - startCoord[1];

        // these are rather shorthands than anything else
        int lengthHoriz = Math.abs(distanceHoriz);
        int lengthVert = Math.abs(distanceVert);

        // the ternary part is just there so we dont divide by 0
        int directionHoriz = lengthHoriz == 0 ? 0 : (distanceHoriz / lengthHoriz);
        int directionVert = lengthVert == 0 ? 0 : (distanceVert / lengthVert);

        if(distanceVert == 0 && distanceHoriz == 0) {
            System.out.println("distanceVert == 0 && distanceHoriz == 0 (Weird, this should be covered by \"End and start coords are identical\")");   
            return false;
        }
        if(pieceChar == 'K') {
            if(isCastle) {
                if(lengthVert > 0 || lengthHoriz > 3) {
                    System.out.println("Tried to castle up or too far sideways (this shouldn't happen)");
                    return false;
                }
                // iterate over all coords from the startcoord to the corresponding rook (except for the startCoord itself),
                // as there may not be any pieces in the way. and the King must not be in check on any square it moves through while castling.
                for(int i = startCoord[0]+directionHoriz; i != (((directionHoriz+1)/2)*7); i += directionHoriz) {
                    
                    if(board.pieceAt(i, startCoord[1]) != null) {
                        System.out.println("Move is obstructed (there may not be any pieces between the King and the Rook when castling)");
                        return false;
                    }
                    // we dont need to check if the king would be in check for squares that are more than 2 squares away from the start coord,
                    // as the King only moves 2 squares.
                    if(i != startCoord[0] && Math.abs(i - startCoord[0]) <= 2) {
                        Board tmp = board.clone();
                        tmp.setPiece(i, startCoord[1], Piece.create(PieceConstants.KING, colorToMove));
                        tmp.setPiece(startCoord, null);
                        // itison=true;
                        if(isKingInCheck(tmp, colorToMove)) {
                            System.out.println("Can't castle through check");
                            return false;
                        }
                        itison=false;
                    }   
                }
            } else if(lengthVert > 1 || lengthHoriz > 1) {
                    System.out.println("Tried to move the king more than one square");
                    return false;
            }
        }

        // TODO King move checks (including castling)
        if(pieceChar == 'P' && (lengthVert > 2 || lengthHoriz > 1)) {
            System.out.println("Pawns aren't Rooks.");
            return false;
        }
        if(distanceVert == 0 || distanceHoriz == 0) {
            if(pieceChar == 'B' || pieceChar == 'N') {
                System.out.printf("Tried to move %s or %s horizontally or vertically (pieceChar is '%s')%n", 
                    PieceConstants.BISHOP.getLocalizedName(), PieceConstants.KNIGHT.getLocalizedName(), pieceChar);
                return false;
            }
            if(pieceChar == 'P') {
                // pawn move
                // TODO pawn promotion
                if(takeTargetPiece) {
                    System.out.printf("Tried to take piece with a non-diagonal %s move%n", PieceConstants.PAWN.getLocalizedName());
                    return false;
                }
                // for checking if the pawn tries to move "backwards", as that means different things depending on if a black or white pawn is being moved
                int relativeDistanceVert = (1 - (2*colorToMove.numValue())) * distanceVert;

                boolean invalid = false;
                switch (relativeDistanceVert) {
                    case 1:
                        if(board.pieceAt(targetCoord) != null) {
                            System.out.println("Move is obstructed (pieceChar is 'P')");
                            return false;
                        }
                        break;
            
                    case 2:
                        if(!((colorToMove == WHITE && startCoord[1] == 1) || (colorToMove == BLACK && startCoord[1] == 6))) {
                            System.out.println("Invalid starting position for double-move");
                            return false;
                        }
                        if(board.pieceAt(targetCoord[0],targetCoord[1]-directionVert) != null) {
                            System.out.println("Move is obstructed (pieceChar is 'P')");
                            return false;
                        }
                        break;

                    case 0:
                        System.out.printf("Tried to move %s horizonally", PieceConstants.PAWN.getLocalizedName());
                    default:
                        if(distanceVert < 0) {
                            System.out.printf("Tried to move %s backwards", PieceConstants.PAWN.getLocalizedName());
                        } else if(distanceVert > 2) {
                            System.out.printf("Tried to move %s more than 2 squares", PieceConstants.PAWN.getLocalizedName());
                        }
                        invalid = true;
                        break;
                }
                if(invalid) {
                    System.out.printf(" (Vertical distance is %d)%n", distanceVert);
                    return false;
                }
            } else {
                int relevantIndex = 0;
                int relevantDirection = directionHoriz;
                if(distanceHoriz == 0) {
                    relevantIndex = 1;
                    relevantDirection = directionVert;
                }
                int i = startCoord[relevantIndex]+relevantDirection;
                while (targetCoord[relevantIndex] != i) {
                    int[] newCoord = new int[2];
                    newCoord[relevantIndex] = i;
                    newCoord[Math.abs(relevantIndex-1)] = startCoord[Math.abs(relevantIndex-1)];
                    System.out.println("Piece obstruction checks (" + (new String[]{"horizontally", "vertically"}[relevantIndex]) + ")");
                    System.out.println(i + " " + newCoord[0] + "," + newCoord[1]);
                    if(board.pieceAt(newCoord) != null) {
                        System.out.printf("Move is obstructed (pieceChar is '%s')%n", pieceChar);
                        return false;
                    }
                    i += relevantDirection;
                }
            }
        } else if(Math.abs(distanceVert) == Math.abs(distanceHoriz)) {
            // diag    
            if(pieceChar == 'R' || pieceChar == 'N') {
                System.out.printf("Tried to move a %s or %s diagonally (pieceChar is '%s')%n", 
                    PieceConstants.ROOK.getLocalizedName(), PieceConstants.KNIGHT.getLocalizedName(), pieceChar);
                return false;
            }
            if(pieceChar == 'P') {
                if(distanceVert > 1) {
                    System.out.printf("Tried to move a %s diagonally more than 1 square%n", PieceConstants.PAWN.getLocalizedName());
                    return false;
                }
                if(!takeTargetPiece) {
                    System.out.printf("Tried to move a %s diagonally without taking something%n", PieceConstants.PAWN.getLocalizedName());
                    return false;
                }
                int[] previousStartCoord = (int[]) previousMoveTokens[1], previousTargetCoord = (int[]) previousMoveTokens[3];
                if(
                    board.pieceAt(targetCoord) == null
                    && !(
                        /* check for en passant */
                        ((char) previousMoveTokens[0]) == 'P'
                        && targetCoord[0] == previousStartCoord[0] && targetCoord[0] == previousTargetCoord[0]
                        && (targetCoord[1]+directionVert) == previousStartCoord[1] && (targetCoord[1]-directionVert) == previousTargetCoord[1]    
                    )
                ) {
                    System.out.println("There is no piece on the target square and the move is not taking en passant");
                    return false; 
                }
            } else {
                int col = startCoord[0]+directionHoriz, row = startCoord[1]+directionVert;
                while (col != targetCoord[0] && row != targetCoord[1]) {
                    if(board.pieceAt(col, row) != null) {
                        System.out.printf("Move is obstructed (pieceChar is '%s')%n", pieceChar);
                        return false;
                    }
                    col += directionHoriz;
                    row += directionVert;
                }
            }
        } else if(lengthVert > 2 || lengthHoriz > 2 || (lengthVert > 1 && lengthHoriz > 1)) {
            // if not diag and both are >= 2, or one is >= 3 move must be invalid
            System.out.println("invalid movement direction");
            return false;
        } else if(lengthVert == 2 || lengthHoriz == 2) {
                // Knight move
                // no need to check if vert or horiz is 1 and also no need to use xor as that was already covered previously
                if(pieceChar != 'N') {
                    System.out.printf("Tried to move something other than a %1$s, as if it was a %1$s. (pieceChar is '%2$s')%n",
                        PieceConstants.KNIGHT.getLocalizedName(), pieceChar);
                    return false;
                }
                // no need to check for piece obstructions cuz knight = based
            }
        
        // check what will be in check after the move 
        // TODO validate checkmate
        Board tmp0 = board.clone();
        tmp0.setPiece(targetCoord, tmp0.pieceAt(startCoord));
        tmp0.setPiece(startCoord, null);
        if(isCastle) { // if isCastle is true, the "Rook move" part has to be set too, but isKingInCheck was already checked earlier.
            tmp0.setPiece((directionHoriz+1)/2 *7, targetCoord[1],null);
            tmp0.setPiece(targetCoord[0]-directionHoriz, targetCoord[1], Piece.create(PieceConstants.ROOK));
        } else {
            boolean kingWouldBeInCheck = isKingInCheck(tmp0, colorToMove); // no need to test if the king is currently in check
            if(kingWouldBeInCheck) {
                System.out.println("Move would leave the King in check.");
                return false;
            }
        }
        boolean opponentKingWillBeInCheck = isKingInCheck(tmp0, invert(colorToMove));
        if(opponentKingWillBeInCheck != move.isCheck()) {
            System.out.print("The King would");
            if(!opponentKingWillBeInCheck) {
                System.out.print("n't ");
            }
            System.out.print("be in check but the move ");
            if(move.isCheck()) {
                System.out.print(" has ");
            } else {
                System.out.print(" is missing ");
            }
            System.out.println("a +");
            // return false;
        }
        // TODO test etc
        return true;
    }
public static boolean itison=false;
    public boolean isValidMove(String move) {
        return isValidMove(Move.fromString(move));
    }

    static boolean isKingInCheck(Board board, Color kingColor) {
        board = board.clone();
        // find the king
        int[] kingCoord = board.findPieceCoords(PieceConstants.KING, kingColor).get(0);
        // find pieces on the same column
        ArrayList<int[]> sameColCoords = board.findPieceCoords(new PieceConstants[]{PieceConstants.ROOK, PieceConstants.QUEEN}, new Color[]{invert(kingColor), invert(kingColor)}, kingCoord[0], kingCoord[1], true, false);
        // find pieces on the same row
        ArrayList<int[]> sameRowCoords = board.findPieceCoords(new PieceConstants[]{PieceConstants.ROOK, PieceConstants.QUEEN}, new Color[]{invert(kingColor), invert(kingColor)}, kingCoord[0], kingCoord[1], false, true);
        
        ArrayList<int[]> sameDiagCoords = new ArrayList<>();
        // TODO: Knights
        ArrayList<int[]> knightCoords = board.findPieceCoords(PieceConstants.KNIGHT, invert(kingColor));
        for (int[] knightCoord : knightCoords) {
            int distanceHoriz = Math.abs(knightCoord[0]-knightCoord[0]);
            int distanceVert = Math.abs(knightCoord[1]-knightCoord[1]);
            if((distanceHoriz == 1 && distanceVert == 2) 
            || (distanceHoriz == 2 && distanceVert == 1)) {
                return true;
            }
        }
        int loopcount = 0;
        // down left
        int[] tmpCoords = kingCoord.clone();
        while(tmpCoords[0] > 0 && tmpCoords[1] > 0) {
            loopcount++;
            if(loopcount > 255) {
                System.out.println("loopcount too high, exiting");
                System.exit(1);
            }

            Piece p = board.pieceAt(tmpCoords.clone());
            if(p != null && p.color == invert(kingColor) 
                && (
                    p.constants == PieceConstants.BISHOP || p.constants == PieceConstants.QUEEN || (
                        p.constants == PieceConstants.PAWN && Math.abs(tmpCoords[0]-kingCoord[0]) < 2
                    )
                )) {
                sameDiagCoords.add(tmpCoords.clone());
            }

            tmpCoords[0] -= 1;
            tmpCoords[1] -= 1;
        }
        loopcount = 0;
        // down right
        tmpCoords = kingCoord.clone();
        while(tmpCoords[0] > 0 && tmpCoords[1] < 8) {
            loopcount++;
            if(loopcount > 255) {
                System.out.println("loopcount too high, exiting");
                System.exit(1);
            }

            Piece p = board.pieceAt(tmpCoords.clone());
            if(p != null && p.color == invert(kingColor) 
                && (
                    p.constants == PieceConstants.BISHOP || p.constants == PieceConstants.QUEEN || (
                        p.constants == PieceConstants.PAWN && Math.abs(tmpCoords[0]-kingCoord[0]) < 2
                    )
                )) {
                sameDiagCoords.add(tmpCoords.clone());
            }

            tmpCoords[0] -= 1;
            tmpCoords[1] += 1;
        }
        loopcount = 0;
        // up left
        tmpCoords = kingCoord.clone();
        while(tmpCoords[0] < 8 && tmpCoords[1] > 0) {
            loopcount++;
            if(loopcount > 255) {
                System.out.println("loopcount too high, exiting");
                System.exit(1);
            }

            Piece p = board.pieceAt(tmpCoords.clone());
            if(p != null) {
                "meow".charAt(0);
            }
            if(p != null && p.color == invert(kingColor) 
                && (
                    p.constants == PieceConstants.BISHOP || p.constants == PieceConstants.QUEEN || (
                        p.constants == PieceConstants.PAWN && Math.abs(tmpCoords[0]-kingCoord[0]) < 2
                    )
                )) {
                sameDiagCoords.add(tmpCoords.clone());
            }

            tmpCoords[0] += 1;
            tmpCoords[1] -= 1;
        }
        loopcount = 0;
        // up right
        tmpCoords = kingCoord.clone();
        while(tmpCoords[0] < 8 && tmpCoords[1] < 8) {
            loopcount++;
            if(loopcount > 255) {
                System.out.println("loopcount too high, exiting");
                System.exit(1);
            }

            Piece p = board.pieceAt(tmpCoords.clone());
            if(p != null && p.color == invert(kingColor) 
                && (
                    p.constants == PieceConstants.BISHOP || p.constants == PieceConstants.QUEEN || (
                        p.constants == PieceConstants.PAWN && Math.abs(tmpCoords[0]-kingCoord[0]) < 2
                    )
                )) {
                sameDiagCoords.add(tmpCoords.clone());
            }

            tmpCoords[0] += 1;
            tmpCoords[1] += 1;
        }
        loopcount = 0;
        for (int i = 0; i < sameDiagCoords.size(); i++) {
            loopcount++;
            if(loopcount > 255) {
                System.out.println("loopcount too high, exiting");
                System.exit(1);
            }
            int[] coord = sameDiagCoords.get(i);
            int colDifference = Math.abs(kingCoord[0]-coord[0]), rowDifference = Math.abs(kingCoord[1]-coord[1]);
            if(colDifference == 1 && rowDifference == 1) {
                // piece is diagonally next to king
                return true;
            }
            // TODO: this loop does nothing but breaking if a specific condition is met?? gotta figure out what that is supposed to do
            // this is also quite terrible code
            for(int[] j = coord.clone(); j[0] != kingCoord[0] && j[1] != kingCoord[1]; j[0]+=((kingCoord[0]-j[0])/Math.abs(kingCoord[0]-j[0])), j[1]+=((kingCoord[1]-j[1])/Math.abs(kingCoord[1]-j[1]))) {
                if(board.pieceAt(j.clone()) != null) {
                    break;
                }
            }
        }
        loopcount = 0;
        for (int i = 0; i < sameColCoords.size(); i++) {
            loopcount++;
            if(loopcount > 255) {
                System.out.println("loopcount too high, exiting");
                System.exit(1);
            }
            int[] coord = sameColCoords.get(i);
            int colDifference = Math.abs(kingCoord[0]-coord[0]), rowDifference = Math.abs(kingCoord[1]-coord[1]);
            if(colDifference == 0 && rowDifference == 1) {
                return true;
            } else if(colDifference == 1) {
                System.out.println("colDifference == 1 for sameColCoords in isKingInCheck (ratio)");
                System.exit(1);
            }
            // TODO: this loop does nothing but breaking if a specific condition is met?? gotta figure out what that is supposed to do
            // this is also quite terrible code but not as bad as the other one
            for(int j = coord[1]; j != kingCoord[1]; j+=((kingCoord[1]-j)/Math.abs(kingCoord[1]-j))) {
                if(board.pieceAt(new int[]{coord[0],j}) != null) {
                    break;
                }
            }
        }
        loopcount = 0;
        for (int i = 0; i < sameRowCoords.size(); i++) {
            loopcount++;
            if(loopcount > 255) {
                System.out.println("loopcount too high, exiting");
                System.exit(1);
            }
            int[] coord = sameRowCoords.get(i);
            int colDifference = Math.abs(kingCoord[0]-coord[0]), rowDifference = Math.abs(kingCoord[1]-coord[1]);
            if(colDifference == 1 && rowDifference == 0) {
                return true;
            } else if(rowDifference == 1) {
                System.out.println("rowDifference == 1 for sameRowCoords in isKingInCheck (ratio)");
                System.exit(1);
            }
            // TODO: this loop does nothing but breaking if a specific condition is met?? gotta figure out what that is supposed to do
            // this is also quite terrible code but not as bad as the first one, about as bad as the second one
            for(int j = coord[0]; j != kingCoord[0]; j+=((kingCoord[0]-j)/Math.abs(kingCoord[0]-j))) {
                if(board.pieceAt(new int[]{j, coord[1]}) != null) {
                    break;
                }
            }
        }
        return false;
    }
    public void doMove(String move) {
        doMove(Move.fromString(move));
    }
    public void doMove(Move move) {
        if(!isValidMove(move)) {
            throw InvalidMoveException.fromMove(move);
        }
        if(move.pieceChar() == 'K') {
            allowedCastles[colorToMove.numValue()][0] = false;
            allowedCastles[colorToMove.numValue()][1] = false;
        }
        if(move.isCastle()) {
            // System.out.println("move is \"" + move.rawMoveString() + "\" (castle) in doMove:");
            // System.out.println(board);
            int row = colorToMove.numValue() * 7;

            int startCol = 4, targetCol = move.isCastleLong() ? 2 : 6;
            board.setPiece(targetCol, row, board.pieceAt(startCol, row));
            board.setPiece(startCol, row, null);

            int directionHoriz = (targetCol - startCol) / Math.abs(targetCol - startCol);
            int rookStartCol = (directionHoriz+1)/2 *7, rookTargetCol = targetCol - directionHoriz;

            board.setPiece(rookTargetCol, row, board.pieceAt(rookStartCol, row));
            board.setPiece(rookStartCol, row, null);
        } else {
            int[] startCoord = move.startCoord();
            int[] targetCoord = move.targetCoord();
            if(move.pieceChar() == 'R' && startCoord[1] == (colorToMove.numValue()*7)) {
                if(startCoord[0] == 7 && allowedCastles[colorToMove.numValue()][0]) {
                    allowedCastles[colorToMove.numValue()][0] = false;
                }
                if(startCoord[0] == 0 && allowedCastles[colorToMove.numValue()][1]) {
                    allowedCastles[colorToMove.numValue()][1] = false;
                }
            }
            if(move.takeTargetPiece()) {
                takenPieces[colorToMove.numValue()].add(board.pieceAt(targetCoord));
            }
            board.setPiece(targetCoord, board.pieceAt(startCoord));
            board.setPiece(startCoord, null);
        }
        if(move.isMate()) {
            gameOver = true;
            lastGameResult = colorToMove.numValue();
        }
        switchColorToMove();
    }
    void switchColorToMove() {
        colorToMove = invert(colorToMove);
    }

    public Color getColorToMove() {
        return colorToMove;
    }

    public Board getBoard() {
        return board.clone();
    }

}
