package xyz.skyjumper409.chessengine.pgnutils;

import java.util.ArrayList;
import xyz.skyjumper409.chessengine.Game;

public class PGNFile {
    ArrayList<xyz.skyjumper409.chessengine.Move> moves = new ArrayList<>();
    PGNMetadata metadata = new PGNMetadata();

    String rawResultString;
    Game.GameScore result = new Game.GameScore();;

    public ArrayList<xyz.skyjumper409.chessengine.Move> getMoves() {
        return moves;
    }
    public PGNMetadata getMetadata() {
        return metadata;
    }
}
