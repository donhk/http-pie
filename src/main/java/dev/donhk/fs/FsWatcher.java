package dev.donhk.fs;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class FsWatcher implements Runnable {

    private final Path hotDir;
    private final FsScanner scanner;

    public FsWatcher(Path hotDir, FsScanner scanner) {
        this.hotDir = hotDir;
        this.scanner = scanner;
    }

    @Override
    public void run() {
        scanner.update();
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
                    final Path path = (Path) event.context();
                    System.out.println("Event kind : " + event.kind() + " - File : " + path);
                    switch (event.kind().name()) {
                        case "ENTRY_DELETE":
                            scanner.delete(path);
                            break;
                        case "ENTRY_CREATE":
                            scanner.create(path);
                            break;
                        case "ENTRY_MODIFY":
                            //scanner.folderChange(path);
                            break;
                    }
                }
                poll = key.reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
