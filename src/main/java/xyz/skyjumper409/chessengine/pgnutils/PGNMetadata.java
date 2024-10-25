package xyz.skyjumper409.chessengine.pgnutils;

import java.util.HashMap;

public class PGNMetadata {

    HashMap<String, String> stringData = new HashMap<>();

// https://en.wikipedia.org/wiki/Portable_Game_Notation#Usage
    public static enum KEYS {
        /* not optional */
        event("Event"), 
        site("Site"),
        date("Date", PGNValueType.DATE), 
        round("Round", PGNValueType.INT), 
        playerNameWhite("White", PGNValueType.NAME), 
        playerNameBlack("Black", PGNValueType.NAME), 
        score("Result", PGNValueType.SCORE),
        /* optional */
        annotator("Annotator", PGNValueType.NAME, true),
        moveCount("PlyCount", PGNValueType.INT, true),
        timeControl("TimeControl", true),
        timeOfGameStart("Time", PGNValueType.TIME, true),
        termination("Termination", true),
        mode("Mode", true),
        fen("FEN", true);
        
        private KEYS(String keyString) {
            this(keyString, PGNValueType.TEXT);
        }
        private KEYS(String keyString, PGNValueType type) {
            this(keyString, type, false);
        }
        private KEYS(String keyString, boolean optional) {
            this(keyString, PGNValueType.TEXT, optional);
        }
        private KEYS(String keyString, PGNValueType type, boolean optional) {
            this.keyString = keyString;
            this.type = type;
            this.optional = optional;
        }
        private String keyString;
        private PGNValueType type;
        private boolean optional;
        String key() {
            return keyString;
        }
        public String getKeyString() {
            return keyString;
        }
        public PGNValueType getType() {
            return type;
        }
        public boolean isOptional() {
            return optional;
        }
    }
    public HashMap<String,String> getMap() {
        return stringData;
    }
}
