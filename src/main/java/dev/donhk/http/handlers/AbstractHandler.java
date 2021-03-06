package dev.donhk.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

public abstract class AbstractHandler implements HttpHandler {

    public void sendHtmlResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        addCommonHeaders(exchange);
        exchange.getResponseHeaders().put("Content-Type", Collections.singletonList("text/html; charset=utf-8"));
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBytes);
            outputStream.flush();
        }
    }

    void addCommonHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().put("server", Collections.singletonList("HK-Server"));
    }

}
