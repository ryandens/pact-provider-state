package com.github.ryandens.pact.provider.state;

/** POJO which holds data regarding a desired provided state */
final class SqlProviderStateQueries {
  private final String setupQuery;
  private final String teardownQuery;

  SqlProviderStateQueries(final String setupQuery, final String teardownQuery) {
    this.setupQuery = setupQuery;
    this.teardownQuery = teardownQuery;
  }

  /**
   * @return a {@link String} representing the SQL query to be run to put the provider in the
   *     desired state
   */
  String setupQuery() {
    return setupQuery;
  }

  /**
   * @return a {@link String} representing the SQL query to be return the provider to its original
   *     state
   */
  String teardownQuery() {
    return teardownQuery;
  }
}
