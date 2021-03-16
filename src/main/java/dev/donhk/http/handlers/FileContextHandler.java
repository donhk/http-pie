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
        exchange.sendResponseHeaders(200, Files.size(file));
        try (OutputStream outputStream = exchange.getResponseBody();
             FileInputStream fis = new FileInputStream(file.toFile())) {
            //10MB
            final int bufferSize = 10_485_760;
            final byte[] buffer = new byte[bufferSize];
            while ((fis.read(buffer)) != -1) {
                outputStream.write(buffer);
                outputStream.flush();
            }
        }
    }
}
