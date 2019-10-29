package com.github.ryandens.pact.provider.state;

import static com.github.ryandens.pact.provider.state.MockitoUtil.stub;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Unit tests for {@link SqlProviderStateHandler} */
final class SqlProviderStateHandlerTest {

  private Statement statementStub;
  private static final String setupQuery = "INSERT into users(name,age) VALUES('Ryan', 23);";
  private static final String teardownQuery = "DELETE from users WHERE name = 'Ryan';";
  private static final String state = "Ryan is in the DB";
  private SqlProviderStateHandler sqlProviderStateHandler;

  @BeforeEach
  void beforeEach() throws SQLException {
    // stub SQL Connection which returns a statement
    final var connectionStub = stub(Connection.class);
    // Statement mock used to verify
    statementStub = stub(Statement.class);
    when(connectionStub.createStatement()).thenReturn(statementStub);
    sqlProviderStateHandler =
        new SqlProviderStateHandler(
            connectionStub,
            Collections.singletonMap(
                state, new SqlProviderStateQueries(setupQuery, teardownQuery)));
  }

  static Stream<Arguments> providerStates() {
    return Stream.of(
        Arguments.of(new ProviderState(state, "setup"), setupQuery),
        Arguments.of(new ProviderState(state, "teardown"), teardownQuery));
  }

  @ParameterizedTest
  @MethodSource("providerStates")
  void it_sends_correct_query_for_provider_state(
      final ProviderState providerState, final String expectedQuery) {
    // GIVEN statement has been stubbed to return 1 for the expectedQuery
    try {
      when(statementStub.executeUpdate(expectedQuery)).thenReturn(1);
    } catch (SQLException ignored) {
      // no SQLException when stubbing
    }

    // WHEN we call the handler with the given providerState
    final var result = sqlProviderStateHandler.handle(providerState);

    // VERIFY result is successful
    assertEquals(ProviderStateHandlerResult.SUCCESS, result);
  }

  @Test
  void it_correctly_handles_invalid_query_return() {
    // GIVEN statement has been stubbed to return 0 for the expectedQuery
    try {
      when(statementStub.executeUpdate(setupQuery)).thenReturn(0);
    } catch (SQLException ignored) {
      // no SQLException when stubbing
    }

    // WHEN we call the handler with the given providerState
    final var result = sqlProviderStateHandler.handle(new ProviderState(state, "setup"));

    // VERIFY result is failure
    assertEquals(ProviderStateHandlerResult.FAILURE, result);
  }

  @Test
  void it_handles_sql_exception_correctly() {
    // GIVEN statement has been stubbed to throw a SQL exception when executeUpdate
    try {
      doThrow(new SQLException()).when(statementStub).executeUpdate(setupQuery);
    } catch (SQLException ignored) {
      // no exception thrown when configuring stub
    }

    // WHEN we call the handler with the given providerState
    final var result = sqlProviderStateHandler.handle(new ProviderState(state, "setup"));

    // VERIFY result is failure
    assertEquals(ProviderStateHandlerResult.FAILURE, result);
  }
}
