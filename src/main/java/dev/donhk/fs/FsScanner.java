package dev.donhk.fs;

import com.sun.net.httpserver.HttpServer;
import dev.donhk.http.handlers.FileContextHandler;
import dev.donhk.utils.Utils;

import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;

public class FsScanner {

    private final FileVisitorWatcher visitorWatcher;
    private final HttpServer server;
    private final Path webRoot;

    public FsScanner(FileVisitorWatcher visitorWatcher) {
        this.visitorWatcher = visitorWatcher;
        this.server = visitorWatcher.getContextHandler().getServer();
        this.webRoot = visitorWatcher.getContextHandler().getWebDirectory();
    }

    public void update() {
        scanFolder(visitorWatcher.getContextHandler().getWebDirectory(), visitorWatcher);
    }

    private void scanFolder(Path folder, FileVisitor<Path> visitor) {
        try {
            Files.walkFileTree(folder, new TreeSet<>(), 100, visitor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void create(Path actualPath) {
        final String contextName = Utils.generateContextName(actualPath, webRoot.toString());
        if (Files.isDirectory(actualPath)) {
            System.out.println("creating dir context " + contextName);
            scanFolder(actualPath, visitorWatcher);
        } else {
            System.out.println("creating file context " + contextName);
            server.createContext(contextName, new FileContextHandler(actualPath));
        }
    }

    public void delete(Path actualPath) {
        final String contextName = Utils.generateContextName(actualPath, webRoot.toString());
        System.out.println("removing " + contextName);
        if (Files.isDirectory(actualPath)) {
            System.out.println("removing " + actualPath.toString());
            folderChange(actualPath);
        } else {
            server.removeContext(contextName);
        }
    }

    public void folderChange(Path directory) {
        //TODO we need to keep track of the context that we want to remove outside
        // outside of the code because the old context causes the code hang
        System.out.println("removing directories context" + directory.toString());
        System.out.println("reindexing subtree " + directory.toString());
        scanFolder(directory, visitorWatcher);
        System.out.println("reindex done");
    }

    public Path getWebRoot() {
        return webRoot;
    }
}
