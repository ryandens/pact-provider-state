package com.github.ryandens.pact.provider.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Designed to compose a {@link ProviderStateHandler} and an {@link ObjectMapper} which can
 * deserialize json into {@link ProviderState}s
 */
final class ProviderStateDeserializationHttpHandler implements HttpHandler {

  private final ObjectMapper objectMapper;
  private final ProviderStateHandler providerStateHandler;

  ProviderStateDeserializationHttpHandler(
      final ProviderStateHandler providerStateHandler, final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.providerStateHandler = providerStateHandler;
  }

  /**
   * Deserializes the {@link HttpExchange#getRequestBody()} into a {@link ProviderState} and then
   * forwards it on to the {@link ProviderStateHandler} for processing*
   *
   * @param exchange is has its response code set to 204 if {@link
   *     ProviderStateHandler#handle(ProviderState)} returns {@link
   *     ProviderStateHandlerResult#SUCCESS}, else set response code to 500
   * @throws IOException when reading request body, deserializing {@link ProviderState}, or setting
   *     response headers
   */
  @Override
  public void handle(final HttpExchange exchange) throws IOException {
    // read the request body into the byte array
    final var body = exchange.getRequestBody().readAllBytes();
    logger.info(new String(body));

    // deserialize into a ProviderState object, log the name
    final var providerState = objectMapper.readValue(body, ProviderState.class);
    // handle the ProviderState using the supplied ProviderState Handler
    final var result = providerStateHandler.handle(providerState);

    if (result.equals(ProviderStateHandlerResult.SUCCESS)) {
      // send 204 if we handled everything right
      logger.info("successfully handled ProviderInfo");
      exchange.sendResponseHeaders(204, -1);
    } else {
      // else send 500
      logger.info("");
      exchange.sendResponseHeaders(500, -1);
    }
  }

  private static final Logger logger = Logger.getLogger("pact-provider-deserialization-handler");
}
