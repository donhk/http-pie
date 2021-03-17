package dev.donhk.fs;

import java.io.IOException;
import java.nio.file.Files;
import java.util.TreeSet;

public class FsScanner {

    private final FileVisitorWatcher visitorWatcher;

    public FsScanner(FileVisitorWatcher visitorWatcher) {
        this.visitorWatcher = visitorWatcher;
    }

    public void update() {
        try {
            Files.walkFileTree(visitorWatcher.getServer().getWebDirectory(), new TreeSet<>(), 100, visitorWatcher);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
