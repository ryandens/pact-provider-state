package com.github.ryandens.pact.provider.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

/** Unit tests for {@link ProviderStateDeserializationHttpHandler} */
final class ProviderStateDeserializationHttpHandlerTest {

  static Stream<Arguments> providerStateAndJson() {
    return Stream.of(
        Arguments.of(
            new ProviderState("jane is the best", "setup"),
            "{\"state\": \"jane is the best\", \"action\": \"setup\"}",
            ProviderStateHandlerResult.SUCCESS,
            204),
        Arguments.of(
            new ProviderState("jane is the best", "teardown"),
            "{\"state\": \"jane is the best\", \"action\": \"teardown\"}",
            ProviderStateHandlerResult.SUCCESS,
            204),
        Arguments.of(
            new ProviderState("jane is the best", "setup"),
            "{\"state\": \"jane is the best\", \"action\": \"setup\"}",
            ProviderStateHandlerResult.FAILURE,
            500));
  }

  @ParameterizedTest
  @MethodSource("providerStateAndJson")
  void it_deserializes_valid_request_bodies(
      final ProviderState expectedProviderState,
      final String requestBody,
      final ProviderStateHandlerResult providerStateResult,
      final int expectedResponseCode) {
    // GIVEN a ProviderStateHandler which always returns SUCCESS
    final var providerStateHandler = mock(ProviderStateHandler.class);
    when(providerStateHandler.handle(notNull())).thenReturn(providerStateResult);

    // AND GIVEN ProviderStateDeserializationHttpHandler with the default ObjectMapper
    final var providerStateDeserializationHttpHandler =
        new ProviderStateDeserializationHttpHandler(providerStateHandler, new ObjectMapper());

    // AND GIVEN an HttpExchange with a valid ProviderInfo serialized in the request body
    final var httpExchange = mock(HttpExchange.class);
    when(httpExchange.getRequestBody())
        .thenReturn(new ByteArrayInputStream(requestBody.getBytes()));

    // AND GIVEN we capture ProviderState arguments
    final var providerStateArgumentCaptor = ArgumentCaptor.forClass(ProviderState.class);

    // WHEN we handle the HttpExchange
    try {
      providerStateDeserializationHttpHandler.handle(httpExchange);
    } catch (IOException e) {
      throw new AssertionError("Problem with handling stubbed HttpExchange", e);
    }

    // VERIFY the ProviderStateHandler was called with the appropriate ProviderState
    verify(providerStateHandler).handle(providerStateArgumentCaptor.capture());
    assertEquals(expectedProviderState, providerStateArgumentCaptor.getValue());

    // VERIFY the HttpExchange had the correct response headers set
    try {
      verify(httpExchange).sendResponseHeaders(expectedResponseCode, -1);
    } catch (IOException ignored) {
      // No IOException when verifying mock
    }
  }
}
