package com.github.ryandens.pact.provider.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Value object to hold information about a <a
 * href="https://docs.pact.io/getting_started/provider_states">Provider State</a>
 */
final class ProviderState {

  @JsonProperty("state")
  private String state;

  @JsonProperty("action")
  private String action;

  public ProviderState(final String state, final String action) {
    this.state = state;
    this.action = action;
  }

  public ProviderState() {}

  String state() {
    return state;
  }

  String action() {
    return action;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final ProviderState that = (ProviderState) o;
    return Objects.equals(state, that.state) && Objects.equals(action, that.action);
  }

  @Override
  public int hashCode() {
    return Objects.hash(state, action);
  }
}
