package dev.donhk;

import dev.donhk.fs.apache.ApacheWatcher;
import dev.donhk.fs.core.FileVisitorWatcher;
import dev.donhk.fs.FsScanner;
import dev.donhk.fs.core.CoreJavaWatcher;
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
    private final boolean apache;

    public Application(String[] args) {
        switch (args.length) {
            default:
            case 1:
                serverPort = 9001;
                webContent = Paths.get(args[0]);
                apache = false;
                break;
            case 2:
                serverPort = Integer.parseInt(args[0]);
                webContent = Paths.get(args[1]);
                apache = false;
                break;
            case 3:
                serverPort = Integer.parseInt(args[0]);
                webContent = Paths.get(args[1]);
                apache = true;
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
        final Runnable watcher;
        if (apache) {
            System.out.println("Apache Watcher");
            watcher = new ApacheWatcher(webContent, fsScanner);
        } else {
            System.out.println("Directory Watcher");
            watcher = new CoreJavaWatcher(webContent, true, fsScanner);
        }
        executorService.submit(watcher);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("A folder to share data must be specified");
        }
        new Application(args).startTheParty();
    }
}
