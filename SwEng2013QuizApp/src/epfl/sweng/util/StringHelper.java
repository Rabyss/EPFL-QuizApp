package epfl.sweng.util;

public class StringHelper {
    public static boolean containsNonWhitespaceCharacters(String str) {
        if (str == null) {
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }
}
