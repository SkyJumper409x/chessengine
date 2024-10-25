package xyz.skyjumper409.chessengine.pgnutils;

import xyz.skyjumper409.chessengine.Game.GameScore;

public enum PGNValueType {
    TEXT(String.class) {
        @Override
        public String parse(String s) {
            return s;
        }
    }, 
    NAME(String[].class) {
        @Override
        public String[] parse(String s) {
            String[] tokens = s.split(", ", 2);
            switch (tokens.length) {
                case 1:
                    return new String[]{tokens[0], null};
                case 2:
                    return tokens;
                default:
                    if(tokens.length < 1) {
                        System.out.print("tokens.length < 1");
                    } else {
                        System.out.print("Something really odd and unexpected happened");
                    }
                    System.out.printf(" in %s.parse(String s), this shouldn't happen (s=\"%s\")%n", getClass().getCanonicalName(), s);
                    return null;
            }
        }
    }, 
    DATE(PGNDate.class) {
        public PGNDate parse(String s) {
            return PGNDate.parse(s);
        }
    }, 
    INT(int.class) {
        @Override
        public Integer parse(String s) {
            return Integer.parseInt(s);
        }
    }, 
    SCORE(GameScore.class) {
        @Override
        public GameScore parse(String s) {
            GameScore g = new GameScore();
            String[] tokens = s.split("-");
            if(tokens.length == 2) {
                tokens[0] = tokens[0].trim();
                tokens[1] = tokens[1].trim();
                if(tokens[0].equals("1/2") && tokens[1].equals("1/2")) {
                    g.addDraw();
                } else {
                    try {
                        for (int i = 0; i < Integer.parseInt(tokens[0]); i++) {
                            g.addWhiteWin();
                        }
                    } catch (NumberFormatException nfex) {
                        nfex.printStackTrace();
                        System.out.printf("error while parsing white's score in %s.parse(String s), (s=\"%s\")%n", getClass().getCanonicalName(), s);
                    }
                    try {
                        for (int i = 0; i < Integer.parseInt(tokens[1]); i++) {
                            g.addBlackWin();
                        }
                    } catch (NumberFormatException nfex) {
                        nfex.printStackTrace();
                        System.out.printf("error while parsing black's score in %s.parse(String s), (s=\"%s\")%n", getClass().getCanonicalName(), s);
                    }
                }
            } else {
                System.out.printf("tokens.length != 2 in %s.parse(String s), (s=\"%s\")%n", getClass().getCanonicalName(), s);
            }
            return g;
        }
    }, 
    TIME(java.time.LocalTime.class) {
        @Override
        public java.time.LocalTime parse(String s) {
            String[] tokens = s.split(":");
            if(tokens.length < 2 || tokens.length > 3) {
                System.out.printf("tokens.length < 2 || tokens.length > 3 in %s.parse(String s), (s=\"%s\")%n", getClass().getCanonicalName(), s);
                return null;
            }
            int hours = 0, minutes = 0;        
            try {
                hours = Integer.parseInt(tokens[0].trim());
                minutes = Integer.parseInt(tokens[1].trim());
            }  catch (NumberFormatException nfex) {
                nfex.printStackTrace();
                System.out.printf("error while parsing hours and minutes in %s.parse(String s), (s=\"%s\")%n", getClass().getCanonicalName(), s);
                return null;
            }
            if(tokens.length == 3 && tokens[2].trim().length() > 0) {
                try {
                    int seconds = Integer.parseInt(tokens[2].trim());
                    return java.time.LocalTime.of(hours, minutes, seconds);
                }  catch (NumberFormatException nfex) {
                    nfex.printStackTrace();
                    System.out.printf("error while parsing seconds in %s.parse(String s), (s=\"%s\")%n", getClass().getCanonicalName(), s);
                }
            }
            return java.time.LocalTime.of(hours, minutes);
        }
    };
    Class<?> typeClass;
    Class<?> secondaryTypeClass;
    private PGNValueType(Class<?> typeClass) {
        this.typeClass = typeClass;
    }
    private PGNValueType(Class<?> typeClass, Class<?> secondaryTypeClass) {
        this(typeClass);
        this.secondaryTypeClass = secondaryTypeClass;
    }
    public Class<?> getTypeClass() {
        return typeClass;
    }
    public Class<?> getSecondaryTypeClass() {
        return secondaryTypeClass;
    }
    public boolean hasSecondaryTypeClass() {
        return getSecondaryTypeClass() != null;
    }
    public Object parse(String s) {
        return s;
    }
}
