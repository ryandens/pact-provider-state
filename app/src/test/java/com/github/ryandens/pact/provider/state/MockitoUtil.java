package com.github.ryandens.pact.provider.state;

import org.mockito.Mockito;

/** Utilities used for testing with {@link org.mockito} */
public final class MockitoUtil {

  private MockitoUtil() {}

  /** Static factory method for creating a stub of the provided {@link Class} */
  public static <T> T stub(final Class<T> classToStub) {
    return Mockito.mock(classToStub, Mockito.withSettings().stubOnly());
  }
}
