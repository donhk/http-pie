package dev.donhk;

import dev.donhk.fs.FSWatcher;
import dev.donhk.http.HttpContextHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application {

    private final int SERVER_PORT;
    private final Path WEB_CONTENT;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public Application(String[] args) {
        switch (args.length) {
            default:
            case 1:
                SERVER_PORT = 9001;
                WEB_CONTENT = Paths.get(args[0]);
                break;
            case 2:
                SERVER_PORT = Integer.parseInt(args[0]);
                WEB_CONTENT = Paths.get(args[1]);
                break;
        }
    }

    public void startTheParty() {
        if (!Files.exists(WEB_CONTENT)) {
            throw new IllegalStateException("Cannot read directory " + WEB_CONTENT);
        }
        final HttpContextHandler server = new HttpContextHandler(WEB_CONTENT, SERVER_PORT, executorService);
        final FSWatcher fsWatcher = new FSWatcher(WEB_CONTENT, server);
        executorService.submit(fsWatcher);
        executorService.submit(server);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("A folder to share data must be specified");
        }
        new Application(args).startTheParty();
    }
}
