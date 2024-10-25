package xyz.skyjumper409.chessengine.pgnutils;

import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import xyz.skyjumper409.chessengine.Game;
import xyz.skyjumper409.chessengine.Move;

import static xyz.skyjumper409.util.ArrayUtils.join;

public class PGNFileReader extends FileReader {
    private static final char TRIPLE_DOT = '…';
    private static final String QUOTATION_MARKS = "«»„“”\"`‚‘’‹›',";

    public PGNFileReader(File file) throws FileNotFoundException {
        super(file);
    }
    public PGNFileReader(String filepath) throws FileNotFoundException {
        super(filepath);
    }

    public Game game = null;
    // local readFile vars except they can be accessed by printDbg
    private char[] cbuf;
    private boolean inMovetext, inMove, inbetweenMovetextTokens;
    private byte previousMovetextTokenType;
    private boolean inTag, inTagName, inTagValue;
    private String[] tmpBufs;
    private int readCharCount;
    private static final byte moveIndex = -1, moveWhite = 0, moveBlack = 1;

    private boolean inResultString = false;

    public PGNFile readFile() throws IOException {
        PGNFile result = new PGNFile();game=new Game();
        cbuf = new char[1];
        inMovetext = false; inMove = false; inbetweenMovetextTokens = false;
        previousMovetextTokenType = -2;
        inTag = false; inTagName = false; inTagValue = false;
        tmpBufs = new String[]{"",""};
        readCharCount = 0;
        while((read(cbuf)) != -1) {
            char n = cbuf[0];
            if(inResultString) {
                tmpBufs[0] += n;
            } else if(inMovetext) {
                org.junit.Assert.assertFalse(inbetweenMovetextTokens && inMove);
                if(inbetweenMovetextTokens) {
                    if(n >= '0' && n <= '9') {
                        switch (previousMovetextTokenType) {
                            case moveIndex:
                                inbetweenMovetextTokens = false;
                                tmpBufs[0] = String.valueOf(n);
                                tmpBufs[1] = "";
                                tmpBufs[2] = ""; 
                                break;
                        
                            case moveWhite:
                            case moveBlack:
                            default:
                                printDbg("Encountered number as first character in a move, something's wrong here");
                                System.exit(1);
                                break;
                        }
                    } else if(n == 'O' || Move.isValidPieceChar(n) || xyz.skyjumper409.chessengine.CoordUtils.isValidCol(n)) {
                        inbetweenMovetextTokens = false;
                        inMove = true;
                        tmpBufs[((previousMovetextTokenType+1) % 2)+1] = "";
                        tmpBufs[previousMovetextTokenType+1] = String.valueOf(n);
                    } 
                } else if(" \n\t\r".indexOf(n) != -1) {
                    if(inMove) {
                        Move m = Move.fromString(Move.doublyDisambiguate(tmpBufs[previousMovetextTokenType+1], game), tmpBufs[previousMovetextTokenType+1]);
                        result.moves.add(m);
                        game.doMove(m);
                        inMove = false;
                        if(m.isMate()) {
                            inResultString = true;
                            inMovetext = false;
                            inbetweenMovetextTokens = false;
                            tmpBufs = new String[]{""};
                        }
                    }
                    // tmpBufs[previousMovetextTokenType+1] = ""; // uncomment if necessary, but keeping it till the space is needed might help with debugging
                    inbetweenMovetextTokens = true;
                    previousMovetextTokenType = (byte) (((previousMovetextTokenType+2) % 3) - 1);
                } else if(xyz.skyjumper409.chessengine.Move.isValidPieceChar(n) || xyz.skyjumper409.chessengine.CoordUtils.isValidCol(n) 
                    || xyz.skyjumper409.chessengine.CoordUtils.isValidRow(n) || ("+#=x.-O90".indexOf(n) != -1)) {
                    tmpBufs[(previousMovetextTokenType+1) % 3] += n;
                } else {
                    printDbg("generic message for hitting impossible else block #0");
                    System.out.println();
                }
            } else if(inTag) {
                if(" \n\t\r".indexOf(n) == -1 && inTag && !inTagName && !inTagValue) {
                    inTagName = true;
                }
                if(n == ']') {
                    if(tmpBufs[1].length() == 0) {
                        printDbg("missing value for tag");
                        System.out.println();
                    }
                    if(tmpBufs[0].indexOf(']') != -1) {
                        printDbg("Something went wrong while parsing, ] should not appear in tmpBufs[0]");
                        System.exit(1);
                    }
                    inTag = false;
                    inTagName = false;
                    inTagValue = false;
                    // TODO properly type metadata
                    result.metadata.stringData.put(tmpBufs[0].trim(), tmpBufs[1].substring(1).trim());
                    tmpBufs[0] = "";
                    tmpBufs[1] = "";
                } else if(inTagName) {
                    if(QUOTATION_MARKS.indexOf(n) != -1) {
                        inTagName = false;
                        inTagValue = true;
                        // System.out.println("Switched to tagValue (tmpBufs[0] is \"" + tmpBufs[0] + "\")");
                        tmpBufs[1] = String.valueOf(n);
                    } else {
                        tmpBufs[0] += n;
                    }
                } else if(inTagValue) {
                    if(n == tmpBufs[1].charAt(0)) {
                        inTagValue = false;
                    } else {
                        tmpBufs[1] += n;
                    }
                } else if(!Character.isWhitespace(n)){
                    printDbg("generic message for hitting impossible else block #1");
                    System.out.println();
                }
            } else if(n == '[') {
                inTag = true;
            } else if(n >= '0' && n <= '9') { // start of Move Text
                inMovetext = true;
                previousMovetextTokenType = -1;
                tmpBufs =  new String[]{String.valueOf(n),"",""};
            } else if(" \n\t\r".indexOf(n) == -1) {
                printDbg("generic message for hitting impossible else block #2");
                System.out.println();
            }
            readCharCount++;
        }
        // some additional parsing
        String resultString = tmpBufs[0].trim();
        result.rawResultString = resultString;
        boolean whiteScore = false, blackScore = false;
        if(resultString.matches("[0-1][ ]*?(\\:|\\-|to|–|—)[ ]*?[0-1]")) {
            whiteScore = resultString.startsWith("1");
            blackScore = resultString.endsWith("1");
        } else if(resultString.matches("(1/2|[0]?\\.5[ ]+1/2|[0]?\\.5)")) {
            whiteScore = true;
            blackScore = true;            
        } else if(!resultString.matches("\\*")) {
            printDbg("Odd result: " + resultString);
        }
        
        if(whiteScore == blackScore) {
            result.result.addDraw();
        } else if(whiteScore) {
            result.result.addWhiteWin();
        } else if(blackScore) {
            result.result.addBlackWin();
        }

        printDbg("generic post-loop message");
        System.out.println("Board:");
        System.out.println(game.getBoard());
        return result;
    }
    // method for printing debug stuffs
    public void printDbg(String msg) {
        System.out.printf("In %s.readFile(): %s (n: \'%s\', readCharCount: %d, tmpBufs: %s,%n\tinTag: %b, inTagName: %b, inTagValue: %b, inMovetext: %b,%n\tinbetweenMovetextTokens: %b, previousMovetextTokenType: %d, previousMovetextTokenType as String: %s)%n",
        getClass().getCanonicalName(), msg, cbuf[0] == '\n' ? "\\n" : String.valueOf(cbuf[0]), readCharCount, join(true, tmpBufs), inTag, inTagName, inTagValue, inMovetext, inbetweenMovetextTokens, previousMovetextTokenType, (previousMovetextTokenType < -1 || previousMovetextTokenType > 1) ? "invalid index" : new String[]{"moveIndex", "moveWhite", "moveBlack"}[previousMovetextTokenType+1]);
        System.out.println();
    }
    public static void main(String[] args) {
        try {
            File RESOURCE_DIR = new File("src/main/resources");
            PGNFileReader pgnIn = new PGNFileReader(new File(RESOURCE_DIR, "pgn/example_game.pgn"));
            PGNFile file = pgnIn.readFile();
            System.out.println(file.toString());
            pgnIn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
