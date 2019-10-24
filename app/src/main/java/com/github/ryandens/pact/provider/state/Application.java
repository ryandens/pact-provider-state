package com.github.ryandens.pact.provider.state;

import com.sun.net.httpserver.HttpServer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Entry-point to the application */
public final class Application {

  /** Creates and starts an {@link HttpServer} */
  public static void main(final String[] args) {
    final var config = config();
    final var applicationPort = config.getInt("applicationPort");
    final Logger logger = Logger.getLogger("pact-provider-state");
    logger.setLevel(Level.INFO);
    logger.info("applicationPort:" + applicationPort);
    final InetSocketAddress address = new InetSocketAddress("0.0.0.0", applicationPort);
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

  /**
   * Shamelessly stolen from <a
   * href="https://github.com/gilday/how-to-microservice/blob/master/src/main/java/com/johnathangilday/App.java">gilday/how-to-microservice</a>
   */
  private static Config config() {
    final var defaultConfig = ConfigFactory.load();
    final var externalConfigFile = Optional.ofNullable(System.getProperty("config")).map(File::new);

    if (externalConfigFile.isPresent() && !externalConfigFile.get().exists()) {
      throw new RuntimeException(
          "external config file " + externalConfigFile.get().getAbsolutePath() + " not found");
    }

    return externalConfigFile
        .map(ConfigFactory::parseFile)
        .map(c -> c.withFallback(defaultConfig))
        .orElse(defaultConfig);
  }
}
