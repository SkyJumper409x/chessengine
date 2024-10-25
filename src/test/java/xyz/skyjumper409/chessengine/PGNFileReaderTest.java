package xyz.skyjumper409.chessengine;
import static org.junit.Assert.fail;

import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.io.File;

import xyz.skyjumper409.chessengine.pgnutils.PGNFile;
import xyz.skyjumper409.chessengine.pgnutils.PGNFileReader;

public class PGNFileReaderTest {
    private static final File RESOURCE_DIR = new File("src/main/resources");
    @Test
    public void testFileIn() {
        PGNFileReader fileReader = null;
        try {
            fileReader = new PGNFileReader(new File(RESOURCE_DIR, "pgn/example_game.pgn"));
            PGNFile file = fileReader.readFile();
            fileReader.close();
            java.util.HashMap<String,String> m = file.getMetadata().getMap();
            System.out.println("metadata.size(): " + m.size());
            System.out.print("metadata: {");
            for (String key : m.keySet()) {
                System.out.print("\n\t\"" + key + "\": \"" + m.get(key) + "\",");
            }
            if(m.keySet().size() > 0) { System.out.println(); } else { System.out.print(' '); }
            System.out.println("}");
            java.util.ArrayList<Move> moves = file.getMoves();
            System.out.println("moves.size(): " + moves.size());
            System.out.print("moves: {");
            for(Move move : moves) {
                System.out.print("\"" + move.toString() + "\" ");
            }
            if(moves.size() > 0) { System.out.println(); } else { System.out.print(' '); }
            System.out.println("}");
        } catch (Exception ex) {
            String data = "";
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final String utf8 = StandardCharsets.UTF_8.name();
            try (PrintStream ps = new PrintStream(baos, true, utf8)) {
                ex.printStackTrace(ps);
                data = baos.toString(utf8);
            } catch(java.io.UnsupportedEncodingException ueex) {
                ueex.printStackTrace();
            }
            System.out.println("Stacktrace:\n" + data.replaceFirst(MONSTER_REGEX_STRING, "[...]"));
            fileReader.printDbg("");
            System.out.println("Board:\n" + fileReader.game.getBoard().toString());
            fail("Got an exception\n");
        }
    }
    private static final String MONSTER_REGEX_STRING = "[ \\t]*at java\\.base/jdk\\.internal\\.reflect\\.NativeMethodAccessorImpl\\.invoke0\\(Native Method\\)\n[ \\t]*at java\\.base/jdk\\.internal\\.reflect\\.NativeMethodAccessorImpl\\.invoke\\(NativeMethodAccessorImpl\\.java:[0-9]*\\)\n[ \\t]*at java\\.base/jdk\\.internal\\.reflect\\.DelegatingMethodAccessorImpl\\.invoke\\(DelegatingMethodAccessorImpl\\.java:[0-9]*\\)\n[ \\t]*at java\\.base/java\\.lang\\.reflect\\.Method\\.invoke\\(Method\\.java:[0-9]*\\)\n[ \\t]*at org\\.junit\\.runners\\.model\\.FrameworkMethod\\$1\\.runReflectiveCall\\(FrameworkMethod\\.java:[0-9]*\\)\n[ \\t]*at org\\.junit\\.internal\\.runners\\.model\\.ReflectiveCallable\\.run\\(ReflectiveCallable\\.java:[0-9]*\\)\n[ \\t]*at org\\.junit\\.runners\\.model\\.FrameworkMethod\\.invokeExplosively\\(FrameworkMethod\\.java:[0-9]*\\)\n[ \\t]*at org\\.junit\\.internal\\.runners\\.statements\\.InvokeMethod\\.evaluate\\(InvokeMethod\\.java:[0-9]*\\)\n[ \\t]*at org\\.junit\\.runners\\.ParentRunner\\$3\\.evaluate\\(ParentRunner\\.java:[0-9]*\\)\n[ \\t]*at org\\.junit\\.runners\\.BlockJUnit4ClassRunner\\$1\\.evaluate\\(BlockJUnit4ClassRunner\\.java:[0-9]*\\)\n[ \\t]*at org\\.junit\\.runners\\.ParentRunner\\.runLeaf\\(ParentRunner\\.java:[0-9]*\\)\n[ \\t]*at org\\.junit\\.runners\\.BlockJUnit4ClassRunner\\.runChild\\(BlockJUnit4ClassRunner\\.java:[0-9]*\\)\n[ \\t]*at org\\.junit\\.runners\\.BlockJUnit4ClassRunner\\.runChild\\(BlockJUnit4ClassRunner\\.java:[0-9]*\\)\n[ \\t]*at org\\.junit\\.runners\\.ParentRunner\\$4\\.run\\(ParentRunner\\.java:[0-9]*\\)\n[ \\t]*at org\\.junit\\.runners\\.ParentRunner\\$1\\.schedule\\(ParentRunner\\.java:[0-9]*\\)\n[ \\t]*at org\\.junit\\.runners\\.ParentRunner\\.runChildren\\(ParentRunner\\.java:[0-9]*\\)\n[ \\t]*at org\\.junit\\.runners\\.ParentRunner\\.access\\$100\\(ParentRunner\\.java:[0-9]*\\)\n[ \\t]*at org\\.junit\\.runners\\.ParentRunner\\$2\\.evaluate\\(ParentRunner\\.java:[0-9]*\\)\n[ \\t]*at org\\.junit\\.runners\\.ParentRunner\\$3\\.evaluate\\(ParentRunner\\.java:[0-9]*\\)\n[ \\t]*at org\\.junit\\.runners\\.ParentRunner\\.run\\(ParentRunner\\.java:[0-9]*\\)\n[ \\t]*at org\\.eclipse\\.jdt\\.internal\\.junit4\\.runner\\.JUnit4TestReference\\.run\\(JUnit4TestReference\\.java:[0-9]*\\)\n[ \\t]*at org\\.eclipse\\.jdt\\.internal\\.junit\\.runner\\.TestExecution\\.run\\(TestExecution\\.java:[0-9]*\\)\n[ \\t]*at org\\.eclipse\\.jdt\\.internal\\.junit\\.runner\\.RemoteTestRunner\\.runTests\\(RemoteTestRunner\\.java:[0-9]*\\)\n[ \\t]*at org\\.eclipse\\.jdt\\.internal\\.junit\\.runner\\.RemoteTestRunner\\.runTests\\(RemoteTestRunner\\.java:[0-9]*\\)\n[ \\t]*at org\\.eclipse\\.jdt\\.internal\\.junit\\.runner\\.RemoteTestRunner\\.run\\(RemoteTestRunner\\.java:[0-9]*\\)\n[ \\t]*at org\\.eclipse\\.jdt\\.internal\\.junit\\.runner\\.RemoteTestRunner\\.main\\(RemoteTestRunner\\.java:[0-9]*\\)\n";
}
