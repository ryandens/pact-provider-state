package com.github.ryandens.pact.provider.state;

import static com.github.ryandens.pact.provider.state.ProviderStateHandlerResult.FAILURE;
import static com.github.ryandens.pact.provider.state.ProviderStateHandlerResult.SUCCESS;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Uses a SQL {@link Connection} to modify the state of the Provider, given supplied {@link
 * #setupQuery} and {@link #teardownQuery} provided at startup time.
 */
final class SqlProviderStateHandler implements ProviderStateHandler {

  private final Connection connection;
  private final String setupQuery;
  private final String teardownQuery;

  SqlProviderStateHandler(
      final Connection connection, final String setupQuery, final String teardownQuery) {
    this.connection = connection;
    this.setupQuery = setupQuery;
    this.teardownQuery = teardownQuery;
  }

  /**
   * Executes the {@link #setupQuery} if the {@link ProviderState#action()} is {@code null} or
   * {@code "setup"}. Executes the {@link #teardownQuery} if {@link ProviderState#action() is {@code "teardown"}
   *
   * @param providerState
   * @return {@link ProviderStateHandlerResult#SUCCESS} if we successfully altered the provider as expected, else returns {@link ProviderStateHandlerResult#FAILURE}
   */
  @Override
  public ProviderStateHandlerResult handle(final ProviderState providerState) {
    var query =
        providerState.action() != null && providerState.action().equals("teardown")
            ? teardownQuery
            : setupQuery;
    logger.info("executing query: " + query);
    final int result;
    try {
      result = connection.createStatement().executeUpdate(query);
    } catch (SQLException e) {
      logger.severe("Problem creating SQL Statement: " + e.getMessage());
      return FAILURE;
    }
    if (result == 1) {
      return SUCCESS;
    } else {
      return FAILURE;
    }
  }

  private static final Logger logger = Logger.getLogger("pact-provider-deserialization-handler");
}
