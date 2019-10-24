package com.github.ryandens.pact.provider.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Logger;

/** Receives HTTP requests with a serialized {@link ProviderState} in the request body */
final class ProviderStateHandler implements HttpHandler {

  private final Connection connection;
  private final ObjectMapper objectMapper;

  ProviderStateHandler(final Connection connection) {
    this.connection = connection;
    objectMapper = new ObjectMapper();
  }

  /**
   * Deserialize the body into a {@link ProviderState}, log the {@link ProviderState#state()}
   *
   * @throws IOException if there is an issue reading the body, deserializing the object, or sending
   *     the response
   */
  @Override
  public void handle(final HttpExchange exchange) throws IOException {
    // read the request body into the byte array
    final var body = exchange.getRequestBody().readAllBytes();
    logger.info(new String(body));

    // deserialize into a ProviderState object, log the name
    final var providerState = objectMapper.readValue(body, ProviderState.class);
    logger.info(providerState.state());

    // send the response headers
    exchange.sendResponseHeaders(204, -1);
  }

  private static final Logger logger = Logger.getLogger("pact-provider-state-handler");
}