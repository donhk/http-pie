package dev.donhk.fs;

import dev.donhk.http.HttpContextHandler;
import dev.donhk.utils.Utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileVisitorPruner implements FileVisitor<Path> {

    private final HttpContextHandler server;

    public FileVisitorPruner(HttpContextHandler server) {
        this.server = server;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        final String context = Utils.generateContextName(file, server.getWebDirectory().toString());
        server.getServer().removeContext(context);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        final String context = Utils.generateContextName(dir, server.getWebDirectory().toString());
        server.getServer().removeContext(context);
        return FileVisitResult.CONTINUE;
    }
}
