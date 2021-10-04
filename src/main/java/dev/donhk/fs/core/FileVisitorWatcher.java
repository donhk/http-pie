package dev.donhk.fs.core;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import dev.donhk.http.HttpContextHandler;
import dev.donhk.http.handlers.DirectoryContextHandler;
import dev.donhk.http.handlers.FileContextHandler;
import dev.donhk.utils.Utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileVisitorWatcher implements FileVisitor<Path> {

    private final HttpContextHandler server;

    public FileVisitorWatcher(HttpContextHandler server) {
        this.server = server;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path currentDirectory, BasicFileAttributes attrs) {
        final String contextName = Utils.generateContextName(currentDirectory, server.getWebDirectory().toString());
        final String context;
        if (contextName.length() == 0) {
            context = "/";
        } else {
            context = contextName;
        }
        System.out.println("adding dir context [" + context + "] dir [" + currentDirectory.toString() + "]");
        final HttpContext statusContext = server.getServer().createContext(context);
        final HttpHandler httpHandler = new DirectoryContextHandler(server.getWebDirectory(), currentDirectory);
        statusContext.setHandler(httpHandler);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
        try {
            final String resourceName = server.getWebDirectory().toString();
            final String contextName = Utils.generateContextName(path, resourceName);
            System.out.println("creating context [" + contextName + "]");
            final HttpContext statusContext = server.getServer().createContext(contextName);
            System.out.println("context created");
            final boolean regularFile = Files.isRegularFile(path);
            final boolean directory = Files.isDirectory(path);

            HttpHandler httpHandler = null;
            System.out.println("regularFile [" + regularFile + "] directory [" + directory + "]");
            if (regularFile) {
                httpHandler = new FileContextHandler(path);
            }
            if (directory) {
                httpHandler = new DirectoryContextHandler(server.getWebDirectory(), path);
            }
            if (httpHandler == null) {
                server.getServer().removeContext(path.toString());
                return FileVisitResult.CONTINUE;
            }
            statusContext.setHandler(httpHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        return FileVisitResult.CONTINUE;
    }

    public HttpContextHandler getContextHandler() {
        return server;
    }
}
