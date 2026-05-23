package org.manator.mypermissions.webui;

import com.sun.net.httpserver.HttpServer;
import org.manator.mypermissions.webui.config.WebConfig;
import org.manator.mypermissions.webui.router.RouterHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebServer {
    private final HttpServer server;

    public WebServer() throws IOException {
        WebConfig config = new WebConfig();
        String hostname = config.onlyLocal() ? "127.0.0.1" : "0.0.0.0";
        int port = config.port();
        server = HttpServer.create(new InetSocketAddress(hostname, port), 0);
        server.createContext("/", new RouterHandler());
        server.setExecutor(null);
    }

    public void start() {
        if(server != null) server.start();
    }

    public void stop() {
        if(server != null) server.stop(0);
    }

    public static void main(String[] args) {
        WebServer serve = null;
        try {
            serve = new WebServer();
            serve.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
