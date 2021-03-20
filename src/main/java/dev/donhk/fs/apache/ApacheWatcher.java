package dev.donhk.fs.apache;

import java.io.File;
import java.nio.file.Path;

import dev.donhk.fs.FsScanner;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

public class ApacheWatcher implements Runnable {

    private final FsScanner fsScanner;
    private final File folder;

    public ApacheWatcher(Path webContent, FsScanner fsScanner) {
        folder = webContent.toFile();
        this.fsScanner = fsScanner;
    }

    @Override
    public void run() {
        final FileAlterationObserver observer = new FileAlterationObserver(folder);
        final long pollingInterval = 5 * 1000;
        final FileAlterationMonitor monitor = new FileAlterationMonitor(pollingInterval);
        fsScanner.update();
        observer.addListener(new FileListener(fsScanner));
        monitor.addObserver(observer);
        try {
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
