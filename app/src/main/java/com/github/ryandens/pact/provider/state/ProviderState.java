package com.github.ryandens.pact.provider.state;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Value object to hold information about a <a
 * href="https://docs.pact.io/getting_started/provider_states">Provider State</a>
 */
final class ProviderState {

  @JsonProperty("state")
  private String state;

  String state() {
    return state;
  }
}
