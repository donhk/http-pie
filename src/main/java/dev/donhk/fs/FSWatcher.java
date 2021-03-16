package dev.donhk.fs;

import dev.donhk.http.HttpContextHandler;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class FSWatcher implements Runnable {

    private final Path hotDir;
    private final HttpContextHandler server;

    public FSWatcher(Path hotDir, HttpContextHandler server) {
        this.hotDir = hotDir;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            final WatchService watchService = FileSystems.getDefault().newWatchService();
            hotDir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            boolean poll = true;
            while (poll) {
                final WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    break;
                }
                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.println("Event kind : " + event.kind() + " - File : " + event.context());
                    server.updateContext();
                }
                poll = key.reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
