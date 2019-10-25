package com.github.ryandens.pact.provider.state;

/** Describes a class which puts uses a mechanism to put the provider in a given state */
interface ProviderStateHandler {

  /** Puts the Provider in the supplied {@link ProviderState} */
  ProviderStateHandlerResult handle(ProviderState providerState);
}
