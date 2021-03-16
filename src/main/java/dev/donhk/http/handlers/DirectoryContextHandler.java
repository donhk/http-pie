package dev.donhk.http.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryContextHandler extends AbstractHandler {

    private final Path directory;
    private final Path webDirectory;
    private final String newLineHtml = "<br>";

    public DirectoryContextHandler(Path webDirectory, Path directory) {
        this.webDirectory = webDirectory;
        this.directory = directory;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Resolving request [dir]");
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<meta charset=\"UTF-8\">");
        sb.append("</head>");
        sb.append("<body>");
        Files.list(directory).forEach(element -> {
            final String name = element.toString().replace(webDirectory.toString(), "").replace("\\", "/");
            if (Files.isSymbolicLink(element)) {
                sb.append("[S]").append(wrap(name, exchange)).append(newLineHtml);
            }
            if (Files.isDirectory(element)) {
                sb.append("[D]").append(wrap(name, exchange)).append(newLineHtml);
            }
            if (Files.isRegularFile(element)) {
                sb.append("[F]").append(wrap(name, exchange)).append(newLineHtml);
            }
        });
        sb.append("</body>");
        sb.append("</html>");
        final String responseMessage = sb.toString() + "\n";
        sendHtmlResponse(responseMessage.getBytes(), exchange);
    }

    private String wrap(String name, HttpExchange exchange) {
        return "<a href=\"" + name + "\">" + name + "</a>";
    }
}
