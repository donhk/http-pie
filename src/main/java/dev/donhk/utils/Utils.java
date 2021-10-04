package dev.donhk.utils;

import java.io.*;
import java.nio.file.Path;

public class Utils {
    private Utils() {
    }

    public static String resource2txt(String resourceName) throws IOException {
        ClassLoader classLoader = Utils.class.getClassLoader();
        if (classLoader.getResource(resourceName) == null) {
            throw new IOException("Resource not found " + resourceName);
        }
        try (InputStream in = classLoader.getResourceAsStream(resourceName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            final StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
            return sb.toString();
        }
    }

    public static String generateContextName(Path path, String webBase) {
        final String string = path.toString()
                .replace(webBase, "")
                .replace("\\", "/");
        System.out.println("before url encode [" + path.toString() + "]");
        final String encoded = Utils.urlEncode(string);
        System.out.println("after url encode [" + path.toString() + "]");
        return encoded;
    }

    public static String urlEncode(String originalString) {
        return percentEncode(originalString);
    }

    private static String percentEncode(String encodeMe) {
        if (encodeMe == null) {
            return "";
        }
        String encoded = encodeMe.replace("%", "-");
        encoded = encoded.replace(" ", "-");
        encoded = encoded.replace("!", "-");
        encoded = encoded.replace("#", "-");
        encoded = encoded.replace("$", "-");
        encoded = encoded.replace("&", "-");
        encoded = encoded.replace("'", "-");
        encoded = encoded.replace("(", "-");
        encoded = encoded.replace(")", "-");
        encoded = encoded.replace("*", "-");
        encoded = encoded.replace("+", "-");
        encoded = encoded.replace(",", "-");
        //encoded = encoded.replace("/", "%2F");
        encoded = encoded.replace(":", "-");
        encoded = encoded.replace(";", "-");
        encoded = encoded.replace("=", "-");
        encoded = encoded.replace("?", "-");
        encoded = encoded.replace("@", "-");
        encoded = encoded.replace("[", "-");
        encoded = encoded.replace("]", "-");
        return encoded;
    }
}
