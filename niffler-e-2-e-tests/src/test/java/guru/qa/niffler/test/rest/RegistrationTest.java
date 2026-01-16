package guru.qa.niffler.test.rest;

import guru.qa.niffler.service.AuthApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;

import static guru.qa.niffler.util.RandomDataUtils.randomUsername;

public class RegistrationTest {

  private final AuthApiClient authApiClient = new AuthApiClient();

  @Test
  void newUserShouldRegisteredByApiCall() throws IOException {
    final Response<Void> response = authApiClient.register(randomUsername(), "12345");
    Assertions.assertEquals(201, response.code());
  }
}
