package dev.donhk.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import dev.donhk.utils.LayoutManager;
import dev.donhk.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryContextHandler extends AbstractHandler {

    private final Path currentDirectory;
    private final Path webDirectory;
    private final String newLineHtml = "<br>";

    public DirectoryContextHandler(Path webDirectory, Path currentDirectory) {
        this.webDirectory = webDirectory;
        this.currentDirectory = currentDirectory;
        System.out.println("cd [" + currentDirectory.toString() + "]");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }
        final StringBuilder directories = new StringBuilder();
        final StringBuilder files = new StringBuilder();
        Files.list(currentDirectory).sorted().forEach(element -> {
            try {
                if (Files.isSymbolicLink(element)) {
                    if (Files.isDirectory(element)) {
                        directories.append(folderIcon()).append(wrap(element)).append("@ ").append(Files.size(element)).append(" bytes").append(newLineHtml);
                    }
                    if (Files.isRegularFile(element)) {
                        files.append(fileIcon()).append(wrap(element)).append("@ ").append(Files.size(element)).append(" bytes").append(newLineHtml);
                    }
                } else {
                    if (Files.isDirectory(element)) {
                        directories.append(folderIcon()).append(wrap(element)).append(" ").append(Files.size(element)).append(" bytes").append(newLineHtml);
                    }
                    if (Files.isRegularFile(element)) {
                        files.append(fileIcon()).append(wrap(element)).append(" ").append(Files.size(element)).append(" bytes").append(newLineHtml);
                    }
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        });
        directories.append(files);
        final String responseMessage = LayoutManager.getInstance().getLayout().replace("{{listOfFiles}}", directories.toString()) + "\n";
        sendHtmlResponse(responseMessage.getBytes(), exchange);
    }

    private String wrap(Path element) {
        final String href = Utils.urlEncode(element.toString().replace(webDirectory.toString(), "").replace("\\", "/"));
        return "<a href=\"" + href + "\">" + "../" + element.getFileName() + "</a>";
    }

    private String fileIcon() {
        return "<div class=\"icon fileIcon\"></div>";
    }

    private String folderIcon() {
        return "<div class=\"icon folderIcon\"></div>";
    }
}
