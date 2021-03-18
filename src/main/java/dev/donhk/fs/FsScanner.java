package dev.donhk.fs;

import com.sun.net.httpserver.HttpServer;
import dev.donhk.http.handlers.FileContextHandler;
import dev.donhk.utils.Utils;

import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeSet;

public class FsScanner {

    private final FileVisitorWatcher visitorWatcher;
    private final FileVisitorPruner pruner;
    private final HttpServer server;
    private final Path webRoot;

    public FsScanner(FileVisitorWatcher visitorWatcher) {
        this.visitorWatcher = visitorWatcher;
        this.pruner = new FileVisitorPruner(visitorWatcher.getServer());
        this.server = visitorWatcher.getServer().getServer();
        this.webRoot = visitorWatcher.getServer().getWebDirectory();
    }

    public void update() {
        scanFolder(visitorWatcher.getServer().getWebDirectory(), visitorWatcher);
    }

    private void scanFolder(Path folder, FileVisitor<Path> visitor) {
        System.out.println("scanning folder " + folder.toString());
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
            System.out.println("removing subtree " + actualPath.toString());
            scanFolder(actualPath, pruner);
        } else {
            server.removeContext(contextName);
        }
    }

    public void folderChange(Path directory) {
        //System.out.println("removing subtree " + directory.toString());
        //scanFolder(directory, pruner);
        //System.out.println("prune done");
        System.out.println("reindexing subtree " + directory.toString());
        scanFolder(directory, visitorWatcher);
        System.out.println("reindex done");
    }

    public Path getWebRoot() {
        return webRoot;
    }
}
