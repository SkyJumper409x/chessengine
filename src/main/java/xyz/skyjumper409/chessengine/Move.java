package xyz.skyjumper409.chessengine;

import static xyz.skyjumper409.chessengine.CoordUtils.isValidCoord;

import static xyz.skyjumper409.chessengine.CoordUtils.isValidCol;
import static xyz.skyjumper409.chessengine.CoordUtils.isValidColZeroBased;
import static xyz.skyjumper409.chessengine.CoordUtils.isValidRow;
import static xyz.skyjumper409.chessengine.CoordUtils.isValidRowZeroBased;

import static xyz.skyjumper409.chessengine.CoordUtils.coordToPositionIndexes;

public record Move (char pieceChar, int[] startCoord, int[] targetCoord,
    boolean takeTargetPiece, boolean isPromotion, char promotionChar,
    boolean isCheck, boolean isMate, boolean isCastleShort, boolean isCastleLong, String rawMoveString, String origRawMoveString) {
    // public static Move fromString(String rawMoveString) {
    //     Move m = null;
    //     try { 
    //         m = secretfromString(rawMoveString);
    //     } catch(InvalidMoveException imex) {
    //         System.out.println("imEx, rawMoveString = " + rawMoveString + "\"");
    //         throw imex;
    //     } catch(RuntimeException rex) {
    //         System.out.println("rEx, rawMoveString = \"" + rawMoveString + "\"");
    //         throw rex;
    //     }
    //     return m;
    // }
    private static final String CASTLE_REGEX = "(0|o|O)-(0|o|O)(-(0|o|O))?(\\+|#)?";

    public static Move fromString(String rawMoveString) throws InvalidMoveException {
        return fromString(rawMoveString, rawMoveString, false);
    }
    public static Move fromString(String rawMoveString, String origMoveString) throws InvalidMoveException {
        return fromString(rawMoveString, origMoveString, false);
    }
    private static Move fromString(String rawMoveString, boolean validationOnly) throws InvalidMoveException {
        return fromString(rawMoveString, rawMoveString, validationOnly);
    }
    private static Move fromString(String rawMoveString, String origMoveString, boolean validationOnly) throws InvalidMoveException {
        Move m = null;
        if(validationOnly) {
            try {
                m = fromStringTrue(rawMoveString, origMoveString, validationOnly);
            } catch (Exception ex) { };
        } else {
            m = fromStringTrue(rawMoveString, origMoveString, validationOnly);
        }
        return m;
    }
    private static Move fromStringTrue(String rawMoveString, String origMoveString, boolean validationOnly) throws InvalidMoveException {
        rawMoveString = rawMoveString.trim();
        String origRawMoveString = rawMoveString;
        // castling
        boolean isCastleLong = false, isCastleShort = false, isCheck = false, isMate = false;
        if(rawMoveString.matches(CASTLE_REGEX)) {
            switch (rawMoveString.charAt(rawMoveString.length()-1)) {
                case '#':
                    isMate = true;
                case '+':
                    isCheck = true;
                    rawMoveString = rawMoveString.substring(0,rawMoveString.length()-1);
                    break;
                default:
                    break;
            }
            if(rawMoveString.length() == 5) {
                isCastleLong = true;
            } else if(rawMoveString.length() == 3) {
                isCastleShort = true;
            } else {
                System.out.println("Hit impossible else block in Move.java");
            }
            // System.out.println("Parsed castling move: \"" + rawMoveString + "\"");
            return new Move('K', null, null, false, false, ' ', isCheck, isMate, isCastleShort, isCastleLong, rawMoveString, origRawMoveString);
        }

        if(rawMoveString == null || rawMoveString.length() < 4) { // 4 is minimum, an example is e2e4. (except for castling, which was covered previously)
            System.out.println("rawMoveString == null || rawMoveString.length < 4");
            if(validationOnly) {
                return null;
            }
            throw InvalidMoveException.fromMove(rawMoveString);
        }
        // assume it's a piece move
        char pieceChar = Character.toUpperCase(rawMoveString.charAt(0));

        // if the number of the first coordinate is the third character, the first char designates the piece.
        if(isValidRow(rawMoveString.charAt(2))) {
            if(!isValidPieceChar(pieceChar)) {
                System.out.println("invalid piece char");
                if(validationOnly) {
                    return null;
                }
                throw InvalidMoveException.fromMove(rawMoveString);
            }
            rawMoveString = rawMoveString.substring(1); // since the first char has been processed
        } else if(isValidRow(rawMoveString.charAt(1))) {
            pieceChar = 'P';
            // no substring since there wasnt a piece character to begin with
        } else {
            System.out.println("Invalid Piece char (couldnt locate first coord)");
            if(validationOnly) {
                return null;
            }
            throw InvalidMoveException.fromMove(rawMoveString);
        }
        if(!isValidCoord(rawMoveString.substring(0,2))) {
            System.out.println("Invalid/faulty notation (make sure to always doubly disambiguate!)");
            if(validationOnly) {
                return null;
            }
            throw InvalidMoveException.fromMove(rawMoveString);
        }
        int[] startCoord = coordToPositionIndexes(rawMoveString.substring(0,2));
        rawMoveString = rawMoveString.substring(2);
        int[] targetCoord = new int[2];
        boolean takeTargetPiece = false;
        if(rawMoveString.charAt(0) == 'x') {
            // the move takes a piece
            takeTargetPiece = true;
            rawMoveString = rawMoveString.substring(1);
        }
        if(rawMoveString.length() < 2) {
            System.out.println();
        }
        if(!isValidCoord(rawMoveString.substring(0,2))) {
            System.out.println("Invalid/faulty notation (make sure to always doubly disambiguate!)");
            if(validationOnly) {
                return null;
            }
            throw InvalidMoveException.fromMove(rawMoveString);
        }
        targetCoord = coordToPositionIndexes(rawMoveString.substring(0, 2));
        char promotionChar = ' ';
        boolean isPromotion = false;
        if(rawMoveString.length() > 2) {
            // there is more to be parsed
            rawMoveString = rawMoveString.substring(2);
            while(rawMoveString.length() > 0) {
                switch (rawMoveString.charAt(0)) {
                    case '#':
                        isMate = true;
                    case '+':
                        isCheck = true;
                        rawMoveString = rawMoveString.substring(1);
                        if(rawMoveString.length() > 0) {
                            if(validationOnly) {
                                return null;
                            }
                            throw InvalidMoveException.fromMove(rawMoveString, " (the move has characters after the check ('+') or the checkmate ('#') symbol)");
                        }
                        break;
                    case '=': // the while loop is there because if something is a pawn promotion AND a check/mate, going through the switch() once isnt enough
                        isPromotion = true;
                        rawMoveString = rawMoveString.substring(1);
                        if(rawMoveString.length() == 0) {
                            if(validationOnly) {
                                return null;
                            }
                           throw InvalidMoveException.fromMove(rawMoveString, " (the move has a promotion ('=') symbol, " + 
                                "but no character following it to designate the piece that is being promoted to)");
                        }
                        promotionChar = rawMoveString.charAt(0);
                        if(!isValidPieceChar(promotionChar)) {
                            if(validationOnly) {
                                return null;
                            }
                            throw InvalidMoveException.fromMove(rawMoveString, " (the move has a promotion ('=') symbol, " + 
                                "but the character following it ('" + promotionChar + "') does not designate a piece that is valid to promote to)");
                        }
                        rawMoveString = rawMoveString.substring(1);
                        break;
                    default:
                        if(validationOnly) {
                            return null;
                        }
                        throw InvalidMoveException.fromMove(rawMoveString, " (the move has one or more invalid characters after the target square)");
                }
            }
        }
        //TODO finish adding castling
        return new Move(pieceChar, startCoord, targetCoord, takeTargetPiece, isPromotion, promotionChar, isCheck, isMate, false, false, origMoveString.trim(), origRawMoveString);
    }
    public static boolean isValidPieceChar(char pieceChar) {
        int pieceChar80;
        // teehee
        return (Math.abs(pieceChar80=pieceChar-80) == 2 || pieceChar80 == (pieceChar80 & 1) || pieceChar == 'B' || pieceChar == 'K');
    }
    @Override
    public String toString() {
        return rawMoveString;
    }
    public static String doublyDisambiguate(String move, Game game) {
        if(move == null) {
            return null;
        }
        move = move.trim();
        if(Move.fromString(move, true) != null && game.isValidMove(Move.fromString(move))) {
            return move;
        }
        Board board = game.getBoard().clone();
        Piece.Color colorToMove = game.getColorToMove();
        String origMove = move;
        // parsing move
        char pieceChar = 'P';
        if(move.length() < 2) {
            return null;
        } else if(move.length() == 2) {
            if(!isValidCoord(move)) {
                return null;
            }
        }
        if(move.matches("[a-h]x[a-h][1-8](\\+|#)?")) {
            // "pawn takes" move
            char startRow = Character.toChars(move.charAt(3) + ((2*game.getColorToMove().numValue()) - 1))[0];
            System.out.println(move.replace("x", startRow + "x"));
            return move.replace("x", startRow + "x");
        }
        if(Character.digit(move.charAt(1)-1, 8) == -1) {
            // not a pawn move that doesn't take
            pieceChar = move.charAt(0);
            move = move.substring(1);
        } else  {
            origMove = 'P' + origMove;
        }
        if(isValidCol(move.charAt(0)) && (
            (isValidRow(move.charAt(1)) && move.length() > 2 && (isValidCol(move.charAt(2)) || move.charAt(2) == 'x')) || move.charAt(1) == 'x')) {
            move = move.substring(1);
            origMove = origMove.substring(1);
        }
        if(isValidRow(move.charAt(0)) && (isValidCol(move.charAt(1)) || move.charAt(1) == 'x')) {
            move = move.substring(1);
            origMove = origMove.substring(1);
        }
        if(move.charAt(0) == 'x') {
            move = move.substring(1);
        }
        String targetCoordString = move.substring(0,2);
        if(!isValidCoord(targetCoordString)) {
            return null;
        }
        move = move.substring(2);

        if(move.length() > 0 && move.charAt(0) == '=') {
            move = move.substring(2);
        }
        String moveAttempt = null;
        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                Piece p = board.pieceAt(col, row);
                if(p != null && p.color == colorToMove && p.constants.getInitial() == pieceChar) {
                    moveAttempt = String.valueOf(pieceChar) + String.valueOf(Character.toChars(col+97)[0]) + String.valueOf(row+1) + origMove.substring(1);
                    if(game.isValidMove(moveAttempt)) {
                        break;
                    } else {
                        // System.out.println("Attempted, but returned false: " + moveAttempt);
                        moveAttempt = null;
                    }
                }
            }
            if(moveAttempt != null) {
                break;
            }
        }
        return moveAttempt;
    }
    @Override
    public int[] startCoord() {
        return startCoord == null ? null : startCoord.clone();
    }
    @Override
    public int[] targetCoord() {
        return targetCoord == null ? null : targetCoord.clone();
    }
    public boolean isCastle() {
        return isCastleShort || isCastleLong;
    }
}
