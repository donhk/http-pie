package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
}
