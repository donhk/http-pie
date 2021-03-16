package dev.donhk.http.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class FileContextHandler extends AbstractHandler {

    private final Path file;

    public FileContextHandler(Path file) {
        this.file = file;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Downloading " + file.toString());
        addCommonHeaders(exchange);
        exchange.getResponseHeaders().put("Content-Transfer-Encoding", Collections.singletonList("binary"));
        final long totalBytes = Files.size(file);
        exchange.sendResponseHeaders(200, totalBytes);
        try (OutputStream outputStream = exchange.getResponseBody();
             InputStream is = new FileInputStream(file.toFile())) {
            //10MB
            long bytesLeft = totalBytes;
            final int bufferSize = 8192;
            final byte[] buffer = new byte[bufferSize];
            while (bytesLeft > 0) {
                final int min = (int) Math.min(buffer.length, bytesLeft);
                final int bytesRead = is.read(buffer, 0, min);
                if (bytesRead < 0) {
                    throw new EOFException("Expected " + bytesLeft + " more bytes to read");
                }
                outputStream.write(buffer, 0, bytesRead);
                outputStream.flush();
                bytesLeft -= bytesRead;
            }
        }
    }

}
