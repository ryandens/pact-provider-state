package com.github.ryandens.pact.provider.state;

import static com.github.ryandens.pact.provider.state.ProviderStateHandlerResult.FAILURE;
import static com.github.ryandens.pact.provider.state.ProviderStateHandlerResult.SUCCESS;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Logger;

/** Uses a SQL {@link Connection} to modify the state of the Provider */
final class SqlProviderStateHandler implements ProviderStateHandler {

  private final Connection connection;
  private final Map<String, SqlProviderStateQueries> sqlProviderStateQueriesMap;

  /**
   * @param sqlProviderStateQueriesMap is a map which maps {@link String}s representing the desired
   *     state of the Provider to a {@link SqlProviderStateQueries}, which holds the information
   *     about how to get the provider in that state. This is read in at startup time and determines
   *     the actions this service is capable of.
   */
  SqlProviderStateHandler(
      final Connection connection,
      final Map<String, SqlProviderStateQueries> sqlProviderStateQueriesMap) {
    this.connection = connection;
    this.sqlProviderStateQueriesMap = sqlProviderStateQueriesMap;
  }

  /**
   * Executes the {@link SqlProviderStateQueries} corresponding to the parameterized {@link
   * ProviderState}, which tells the service which {@link SqlProviderStateQueries} it should execute
   * now.
   *
   * @return {@link ProviderStateHandlerResult#SUCCESS} if we successfully altered the provider as
   *     expected, else returns {@link ProviderStateHandlerResult#FAILURE}
   */
  @Override
  public ProviderStateHandlerResult handle(final ProviderState providerState) {
    final var sqlProviderStateQueries = sqlProviderStateQueriesMap.get(providerState.state());
    var query =
        providerState.action() != null && providerState.action().equals("teardown")
            ? sqlProviderStateQueries.teardownQuery()
            : sqlProviderStateQueries.setupQuery();
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
