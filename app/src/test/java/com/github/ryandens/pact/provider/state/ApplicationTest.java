package com.github.ryandens.pact.provider.state;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Tests for {@link Application} */
final class ApplicationTest {

  private static HttpClient httpClient;

  @BeforeAll
  static void beforeAll() {
    Application.main(new String[] {});
    httpClient = HttpClient.newHttpClient();
  }

  /** makes sure the ping endpoint responds to http requests */
  @Test
  void it_responds_to_ping() {
    final HttpResponse<String> response =
        sendHttpRequest(
            HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/ping")).build());
    assert response.statusCode() == 204;
  }

  /**
   * Sends the provided {@link HttpRequest}
   *
   * @return the {@link HttpResponse}
   */
  private HttpResponse<String> sendHttpRequest(final HttpRequest request) {
    final HttpResponse<String> response;
    try {
      response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException("Problem sending HTTP request");
    }
    return response;
  }
}
