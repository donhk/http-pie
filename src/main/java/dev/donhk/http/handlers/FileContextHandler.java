package dev.donhk.http.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class FileContextHandler extends AbstractHandler {

    private final Path file;

    public FileContextHandler(Path file) {
        this.file = file;
    }

    @Override
    public void handle(HttpExchange exchange) {
        System.out.println("Downloading " + file.toString());
        try {
            Path realPath;
            if (Files.isSymbolicLink(file)) {
                realPath = file.toRealPath();
            } else {
                realPath = file;
            }
            downloadFile(exchange, realPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadFile(HttpExchange exchange, Path fileToDownload) throws IOException {
        exchange.getResponseHeaders().put("Content-Transfer-Encoding", Collections.singletonList("binary"));
        addCommonHeaders(exchange);
        final long totalBytes = Files.size(fileToDownload);
        System.out.println("file: " + fileToDownload.toString() + " " + totalBytes);
        if (totalBytes == 0) {
            emptyResponse(exchange);
        } else {
            downloadData(totalBytes, fileToDownload, exchange);
        }
    }

    /**
     * Download file contents
     *
     * @param totalBytes     bytes that will be send
     * @param fileToDownload file to download
     * @param exchange       HttpExchange object
     * @throws IOException on error
     */
    private void downloadData(long totalBytes, Path fileToDownload, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, totalBytes);
        try (OutputStream outputStream = exchange.getResponseBody();
             InputStream is = new FileInputStream(fileToDownload.toFile())) {
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

    /**
     * The file to download is empty
     *
     * @param exchange HttpExchange object
     * @throws IOException on error
     */
    private void emptyResponse(HttpExchange exchange) throws IOException {
        final byte[] empty = "\n".getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, empty.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(empty);
            outputStream.flush();
        }
    }
}
