package dev.donhk.http.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StatusHandler extends AbstractHandler {

    private static final byte[] bytes = "pong".getBytes(StandardCharsets.UTF_8);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCommonHeaders(exchange);
        sendHtmlResponse(bytes, exchange);
    }
}
