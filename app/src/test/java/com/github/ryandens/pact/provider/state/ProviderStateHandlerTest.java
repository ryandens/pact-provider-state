package com.github.ryandens.pact.provider.state;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProviderStateHandlerTest {

  private ProviderStateHandler providerStateHandler;
  private Connection connectionMock;

  @BeforeEach
  void beforeEach() {
    connectionMock = mock(Connection.class);
    providerStateHandler = new ProviderStateHandler(connectionMock);
  }

  @Test
  void it_deserializes_valid_body() {
    // stub the HttpExchange
    final var httpExchange = mock(HttpExchange.class);

    // create InputStream which will serve as the stubbed request body

    when(httpExchange.getRequestBody())
        .thenReturn(new ByteArrayInputStream("{\"state\": \"jane is the best\"}".getBytes()));

    try {
      providerStateHandler.handle(httpExchange);
    } catch (IOException e) {
      throw new AssertionError("Test failed", e);
    }
  }
}
