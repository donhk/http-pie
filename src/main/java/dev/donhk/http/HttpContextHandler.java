package dev.donhk.http;

import com.sun.net.httpserver.HttpServer;
import dev.donhk.http.handlers.StatusHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class HttpContextHandler implements Runnable {

    private final Path webDirectory;
    private final int port;
    private final ExecutorService executorService;
    private boolean alive = false;
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
            final String hostname = InetAddress.getLocalHost().getCanonicalHostName();
            System.out.println("Starting socket at  " + hostname + ":" + port);
            this.server = HttpServer.create(new InetSocketAddress(InetAddress.getLocalHost().getCanonicalHostName(), port), 0);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
        //add state handlers
        this.server.createContext("/ping", new StatusHandler());
        server.setExecutor(executorService);
        server.start();
        alive = true;
    }

    public void waitUntilStart() {
        while (!isAlive()) {
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException ie) {
                throw new IllegalStateException(ie.getMessage());
            }
        }
    }

    public HttpServer getServer() {
        return server;
    }

    public Path getWebDirectory() {
        return webDirectory;
    }

    public boolean isAlive() {
        return alive;
    }
}
