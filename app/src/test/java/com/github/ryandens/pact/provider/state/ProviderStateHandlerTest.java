package com.github.ryandens.pact.provider.state;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for the {@link ProviderStateHandler} */
class ProviderStateHandlerTest {

  private ProviderStateHandler providerStateHandler;
  private Connection connectionStub;
  private Statement statementStub;
  private String setupQuery;
  private String teardownQuery;

  /**
   * Sets up the stubs for and the instance of {@link ProviderStateHandler}
   *
   * @throws SQLException, but not really because this is only thrown while stubbing {@link}
   */
  @BeforeEach
  void beforeEach() throws SQLException {
    // stub SQL Connection which returns a statement
    connectionStub = stub(Connection.class);
    // Statement mock used to verify
    statementStub = stub(Statement.class);
    when(connectionStub.createStatement()).thenReturn(statementStub);
    setupQuery = "INSERT into users(name,age) VALUES('Ryan', 23);";
    teardownQuery = "DELETE from users WHERE name = 'Ryan';";
    providerStateHandler = new ProviderStateHandler(connectionStub, setupQuery, teardownQuery);
  }

  private <T> T stub(final Class<T> classToStub) {
    return mock(classToStub, withSettings().stubOnly());
  }

  /** Verifies that the {@link ProviderStateHandler} properly handles a setup message */
  @Test
  void it_deserializes_valid_body_and_sends_setupQuery() {
    // GIVEN a mocked HttpExchange
    final var httpExchange = mock(HttpExchange.class);

    // AND GIVEN the HttpExchange has a stubbed valid request body
    when(httpExchange.getRequestBody())
        .thenReturn(
            new ByteArrayInputStream(
                "{\"state\": \"jane is the best\", \"action\": \"setup\"}".getBytes()));

    // AND GIVEN the Statement returns one when the update  is executed with the setupQuery
    try {
      // simple stub to make the
      when(statementStub.executeUpdate(setupQuery)).thenReturn(1);
    } catch (SQLException ignored) {
    }

    // WHEN the ProviderStateHandler handles the exchange
    try {
      providerStateHandler.handle(httpExchange);
    } catch (IOException e) {
      throw new AssertionError("Test failed", e);
    }

    // AND VERIFY the HttpExchange response headers are set correctly
    try {
      verify(httpExchange).sendResponseHeaders(204, -1L);
    } catch (IOException ignored) {
    }
  }
}
