package dev.donhk;

import dev.donhk.fs.FsWatcher;
import dev.donhk.fs.FileVisitorWatcher;
import dev.donhk.fs.FsScanner;
import dev.donhk.http.HttpContextHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application {

    private final int serverPort;
    private final Path webContent;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public Application(String[] args) {
        switch (args.length) {
            default:
            case 1:
                serverPort = 9001;
                webContent = Paths.get(args[0]);
                break;
            case 2:
                serverPort = Integer.parseInt(args[0]);
                webContent = Paths.get(args[1]);
                break;
        }
    }

    public void startTheParty() {
        if (!Files.exists(webContent)) {
            throw new IllegalStateException("Cannot read directory " + webContent);
        }
        final HttpContextHandler server = new HttpContextHandler(webContent, serverPort, executorService);
        executorService.submit(server);
        server.waitUntilStart();
        final FileVisitorWatcher fileVisitor = new FileVisitorWatcher(server);
        final FsScanner fsScanner = new FsScanner(fileVisitor);
        final FsWatcher fsWatcher = new FsWatcher(webContent, fsScanner);
        executorService.submit(fsWatcher);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("A folder to share data must be specified");
        }
        new Application(args).startTheParty();
    }
}
