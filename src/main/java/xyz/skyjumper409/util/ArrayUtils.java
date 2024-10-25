package xyz.skyjumper409.util;

public class ArrayUtils {
    private ArrayUtils() {}

    private static final boolean QUOTE_ELEMENTS_DEFAULT = false;
    private static final String DELIMITER_DEFAULT = ", ";
    private static final String QUOTE_SYMBOL = "\"";
    private static final String EMPTY_JOINED_LIST = "[]";

    public static String join(Object[] args) {
        return join(DELIMITER_DEFAULT, QUOTE_ELEMENTS_DEFAULT, args);
    }
    public static String join(char delimiter, Object[] args) {
        return join(String.valueOf(delimiter), QUOTE_ELEMENTS_DEFAULT, args);
    }
    public static String join(String delimiter, Object[] args) {
        return join(delimiter, QUOTE_ELEMENTS_DEFAULT, args);        
    }

    public static String join(boolean quoteElements, Object[] args) {
        return join(DELIMITER_DEFAULT, quoteElements, args);
    }
    public static String join(char delimiter, boolean quoteElements, Object[] args) {
        return join(String.valueOf(delimiter), quoteElements, args);
    }
    public static String join(String delimiter, boolean quoteElements, Object[] args) {
        return join(delimiter, quoteElements, true, args);
    }
    public static String join(String delimiter, boolean quoteElements, boolean addBrackets, Object[] args) {
        if(args.length <= 0) {
            return EMPTY_JOINED_LIST;
        }
        String quote = quoteElements ? QUOTE_SYMBOL : "";
        StringBuilder result = new StringBuilder("");
        if(addBrackets) {
            result = result.append("[ ");
        }
        for (Object object : args) {
            result = result.append(quote).append(object == null ? "null" : object.toString()).append(quote).append(delimiter);
        }
        String replacement = "";
        if(addBrackets) {
            replacement = " ]";
        }
        if(delimiter.length() > 0) {
            return result.replace(result.length()-delimiter.length(), result.length(), replacement).toString();
        }
        return result.append(replacement).toString();
    }

    public static Byte[] byteValuesOf(byte[] bytes) {
        Byte[] result = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = bytes[i];
        }
        return result;
    }
    public static Short[] shortValuesOf(short[] shorts) {
        Short[] result = new Short[shorts.length];
        for (int i = 0; i < shorts.length; i++) {
            result[i] = shorts[i];
        }
        return result;
    }
    public static Integer[] integerValuesOf(int[] ints) {
        Integer[] result = new Integer[ints.length];
        for (int i = 0; i < ints.length; i++) {
            result[i] = ints[i];
        }
        return result;
    }
    public static Long[] longValuesOf(long[] longs) {
        Long[] result = new Long[longs.length];
        for (int i = 0; i < longs.length; i++) {
            result[i] = longs[i];
        }
        return result;
    }
}
