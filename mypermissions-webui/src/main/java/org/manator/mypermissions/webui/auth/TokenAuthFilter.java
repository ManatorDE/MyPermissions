package org.manator.mypermissions.webui.auth;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import org.manator.mypermissions.webui.config.WebConfig;
import org.manator.mypermissions.webui.util.HttpUtil;

import java.io.IOException;

public class TokenAuthFilter extends Filter {
    private final WebConfig config;

    public TokenAuthFilter() {
        config = new WebConfig();
    }

    public void doFilter(HttpExchange exchange, Filter.Chain chain) throws IOException {
        String header = exchange.getRequestHeaders().getFirst("Authorization");
        if (header == null || !header.equals("Bearer " + config.token())) {
            HttpUtil.sendError(exchange, 401, "Unauthorized");
            return;
        }
        chain.doFilter(exchange);
    }

    @Override
    public String description() {
        return "";
    }
}