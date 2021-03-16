package dev.donhk.http.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileContextHandler extends AbstractHandler {

    private final Path file;

    public FileContextHandler(Path file) {
        this.file = file;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Downloading " + file.toString());
        byte[] fileData = Files.readAllBytes(file);
        sendBinaryResponse(fileData, exchange);
    }
}
