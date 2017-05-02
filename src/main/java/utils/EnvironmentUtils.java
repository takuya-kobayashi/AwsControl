package utils;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentUtils {

    private static boolean isDebug = false;
    private static Map<String, String> debugVariables = new HashMap<>();

    public static String getEnvironmentVariable(String varName) {
        if (isDebug) {
            return trimSpace(debugVariables.get(varName));
        }
        return trimSpace(System.getenv(varName));
    }

    public static void setDebugMode() {
        isDebug = true;
    }

    public static void putDebugEnvironmentVariables(String key, String value) {
        debugVariables.put(key, value);
    }

    private static String trimSpace(String str) {
        if (str.isEmpty()) {
            return "";
        }
        return str.replaceAll("\\s|　", "");
    }

}
