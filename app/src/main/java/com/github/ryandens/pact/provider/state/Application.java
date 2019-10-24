package com.github.ryandens.pact.provider.state;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Entry-point to the application */
public final class Application {

  /** Creates and starts an {@link HttpServer} */
  public static void main(final String[] args) {
    final Logger logger = Logger.getLogger("pact-provider-state");
    logger.setLevel(Level.INFO);
    final InetSocketAddress address = new InetSocketAddress("0.0.0.0", 8080);
    final HttpServer server;
    try {
      server = HttpServer.create(address, 0);
    } catch (IOException e) {
      throw new RuntimeException("Problem initializing HttpServer", e);
    }

    server.createContext(
        "/ping",
        exchange -> {
          exchange.sendResponseHeaders(204, -1);
          exchange.close();
          logger.info(
              "request URI:"
                  + exchange.getRequestURI()
                  + ", response code:"
                  + exchange.getResponseCode());
        });

    logger.info("HttpServer started listening on" + address);

    server.start();
  }
}
