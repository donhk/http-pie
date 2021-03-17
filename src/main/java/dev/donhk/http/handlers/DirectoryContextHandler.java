package dev.donhk.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import dev.donhk.utils.Utils;

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
        final StringBuilder directories = new StringBuilder();
        final StringBuilder files = new StringBuilder();

        final String fileIcon = Utils.resource2txt("file.svg");
        final String folderIcon = Utils.resource2txt("directory.svg");
        String layout = Utils.resource2txt("layout.html");

        Files.list(directory).sorted().forEach(element -> {
            try {
                if (Files.isSymbolicLink(element)) {
                    if (Files.isDirectory(element)) {
                        directories.append(folderIcon).append(wrap(element)).append("@ ").append(Files.size(element)).append(" bytes").append(newLineHtml);
                    }
                    if (Files.isRegularFile(element)) {
                        files.append(fileIcon).append(wrap(element)).append("@ ").append(Files.size(element)).append(" bytes").append(newLineHtml);
                    }
                } else {
                    if (Files.isDirectory(element)) {
                        directories.append(folderIcon).append(wrap(element)).append(" ").append(Files.size(element)).append(" bytes").append(newLineHtml);
                    }
                    if (Files.isRegularFile(element)) {
                        files.append(fileIcon).append(wrap(element)).append(" ").append(Files.size(element)).append(" bytes").append(newLineHtml);
                    }
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        });
        directories.append(files);
        final String responseMessage = layout.replace("{{listOfFiles}}", directories.toString()) + "\n";
        sendHtmlResponse(responseMessage.getBytes(), exchange);
    }

    private String wrap(Path element) {
        final String name = element.toString().replace(webDirectory.toString(), "").replace("\\", "/");
        return "<a href=\"" + name + "\">" + "../" + element.getFileName() + "</a>";
    }

    private String fileIcon() {
        return "<div class=\"fileIcon\"></div>";
    }

    private String folderIcon() {
        return "<div class=\"directoryIcon\"></div>";
    }
}
