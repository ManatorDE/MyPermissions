package org.manator.mypermissions.webui.router;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.manator.mypermissions.webui.util.HttpUtil;

import java.io.IOException;
import java.util.UUID;

public class RouterHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();   // GET, POST, PUT, DELETE
        String path   = exchange.getRequestURI().getPath(); // /api/users/550e84...

        if (method.equals("GET") && path.equals("/api/groups"))         handleListGroups(exchange);
        else if (method.equals("POST") && path.equals("/api/groups"))   handleCreateGroup(exchange);
        else if (method.equals("GET") && path.startsWith("/api/users/")) handleGetUser(exchange, extractUuid(path));
        else if(method.equals("GET") && path.equals("/")) HttpUtil.sendError(exchange, 200, "Hello World");
            // ...
        else HttpUtil.sendError(exchange, 404, "Not found");
    }

    private void handleListGroups(HttpExchange exchange) {}
    private void handleCreateGroup(HttpExchange exchange) {}
    private void handleGetUser(HttpExchange exchange, UUID uuid) {}


    private UUID extractUuid(String path) {
        return UUID.fromString(path);
    }
}
