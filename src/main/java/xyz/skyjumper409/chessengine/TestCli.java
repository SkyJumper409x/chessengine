package xyz.skyjumper409.chessengine;

import xyz.skyjumper409.chessengine.pgnutils.PGNFile;
import xyz.skyjumper409.chessengine.pgnutils.PGNFileReader;

public class TestCli {
    public static void main(String[] args) {
        Game g = new Game();
        String userInput = "";
        java.util.Scanner sc = new java.util.Scanner(System.in);
        java.util.ArrayList<Move> presetMoves = null;
        try {
            PGNFileReader fileReader = new PGNFileReader("/home/lynn/Documents/coding/_chess_engine/chessengine/src/main/resources/pgn/tmp.pgn");
            PGNFile file = fileReader.readFile();
            presetMoves = file.getMoves();
            fileReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        while(presetMoves.size() > 0) {
            g.doMove(presetMoves.get(0));
            presetMoves.remove(0);
        }
        while (!userInput.equalsIgnoreCase("quit")) {
            System.out.println("waiting for input... ");
            if(sc.hasNext()) {
                
                userInput = sc.next().toLowerCase();
                String[] tokens = userInput.split(" ");
                switch (tokens[0]) {
                    case "info":
                        System.out.println("Board:");
                        System.out.println(g.getBoard());
                        System.out.println("It is player " + (g.getColorToMove().numValue()+1) + "'s turn.");
                        break;
                
                    case "move":
                        String move = ""; 
                        if(tokens.length < 2) {
                            System.out.print("Input Move: ");
                            move = sc.next();
                            System.out.println();
                        } else {
                            move = tokens[1];
                        }
                        move = Move.doublyDisambiguate(move, g);
                        if(!g.isValidMove(move)) {
                            System.out.print(move + " is not a valid move.\nproceed anyways? [yes/No]");
                            String answer = sc.next();
                            System.out.println();
                            if(!answer.equalsIgnoreCase("yes")) {
                                break;
                            }
                        }
                        g.doMove(move);
                        break;
                    case "reset":
                        g.reset();
                    default:
                        break;
                }
            }
        }
        sc.close();
    }
}
