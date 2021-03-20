package dev.donhk.fs.apache;

import dev.donhk.fs.FsScanner;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import java.io.File;

public class FileListener extends FileAlterationListenerAdaptor {

    private final FsScanner fsScanner;

    public FileListener(FsScanner fsScanner) {
        this.fsScanner = fsScanner;
    }

    @Override
    public void onFileCreate(File file) {
        fsScanner.create(file.toPath());
    }

    @Override
    public void onFileDelete(File file) {
        fsScanner.delete(file.toPath());
    }

    @Override
    public void onDirectoryDelete(final File directory) {
        fsScanner.delete(directory.toPath());
    }

    @Override
    public void onDirectoryCreate(final File directory) {
        fsScanner.create(directory.toPath());
    }
}