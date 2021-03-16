package dev.donhk.http;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.donhk.http.handlers.DirectoryContextHandler;
import dev.donhk.http.handlers.FileContextHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;

public class HttpContextHandler implements Runnable, FileVisitor<Path> {

    private final Path webDirectory;
    private final int port;
    private final ExecutorService executorService;
    private HttpServer server;

    public HttpContextHandler(Path webDirectory, int port, ExecutorService executorService) {
        this.webDirectory = webDirectory;
        this.port = port;
        this.executorService = executorService;
    }

    @Override
    public void run() {
        try {
            System.out.println("Starting server");
            this.server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
        //creating web contexts
        updateContext();
        server.setExecutor(executorService);
        server.start();
    }

    public void updateContext() {
        try {
            Files.walkFileTree(webDirectory, new TreeSet<>(), 100, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        final Path xDir;
        if (Files.isSymbolicLink(dir)) {
            xDir = dir.toRealPath();
        } else {
            xDir = dir;
        }
        final String contextName = xDir.toString().replace(webDirectory.toString(), "").replace("\\", "/");
        final String context;
        if (contextName.length() == 0) {
            context = "/";
        } else {
            context = contextName;
        }
        System.out.println("adding dir context " + context);
        final HttpContext statusContext = server.createContext(context);
        HttpHandler httpHandler = new DirectoryContextHandler(webDirectory, dir);
        statusContext.setHandler(httpHandler);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        final Path xFile;
        if (Files.isSymbolicLink(file)) {
            xFile = file.toRealPath();
        } else {
            xFile = file;
        }
        final String contextName = xFile.toString().replace(webDirectory.toString(), "").replace("\\", "/");
        System.out.println("adding context " + contextName);
        final HttpContext statusContext = server.createContext(contextName);
        HttpHandler httpHandler = null;
        if (Files.isRegularFile(file)) {
            httpHandler = new FileContextHandler(file);
        }
        if (Files.isDirectory(file)) {
            httpHandler = new DirectoryContextHandler(webDirectory, file);
        }
        if (httpHandler == null) {
            server.removeContext(file.toString());
            return FileVisitResult.CONTINUE;
        }
        statusContext.setHandler(httpHandler);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
