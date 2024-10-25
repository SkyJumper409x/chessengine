package xyz.skyjumper409.util;

public class StringUtils {
    private StringUtils() {}

    public static int count(String stringToSearch, char charToCount) {
        return count(stringToSearch, String.valueOf(charToCount));
    }
    public static int count(String stringToSearch, String stringToCount) {
        int result = 0;
        int previousIndex = 0;
        while((previousIndex = stringToSearch.indexOf(stringToCount, previousIndex)) != -1) {
            result++;
        }
        return result;
    }
}
