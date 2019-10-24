package com.github.ryandens.pact.provider.state;

import com.sun.net.httpserver.HttpServer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Entry-point to the application */
public final class Application {

  /** Creates and starts an {@link HttpServer} */
  public static void main(final String[] args) {
    // read in config
    final var config = config();
    // set log level from config
    logger.setLevel(Level.parse(config.getString("logLevel")));

    // create a server which will listen on localhost with the application port read from config
    final InetSocketAddress address =
        new InetSocketAddress("0.0.0.0", config.getInt("applicationPort"));
    final HttpServer server;
    try {
      server = HttpServer.create(address, 0);
    } catch (IOException e) {
      throw new RuntimeException("Problem initializing HttpServer", e);
    }

    // create a connection to the database
    final var connection =
        getConnection(
            "jdbc:mysql://"
                + config.getString("mysqlHost")
                + ":"
                + config.getString("mysqlPort")
                + "/"
                + config.getString("mysqlPath"),
            config.getString("mysqlUser"),
            config.getString("mysqlPassword"));

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

    server.createContext("/provider-state", new ProviderStateHandler(connection));

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

  /**
   * Creates a connection to the database
   *
   * @param url connection url of the format "jdbc:subprotocol:subname"
   */
  private static Connection getConnection(
      final String url, final String user, final String password) {
    try {
      return DriverManager.getConnection(url, user, password);
    } catch (SQLException e) {
      logger.info("Unable to create Connection, continuing without connection to the database");
      return null;
    }
  }

  private static final Logger logger = Logger.getLogger("pact-provider-state");
}
