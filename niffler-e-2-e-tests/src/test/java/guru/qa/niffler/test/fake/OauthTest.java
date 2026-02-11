package guru.qa.niffler.test.fake;

import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.service.AuthApiClient;
import guru.qa.niffler.utils.OauthUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebTest
public class OauthTest {

    private final AuthApiClient authApiClient = new AuthApiClient();

    @Test
    void oauth2TokenTest() throws IOException {
        String codeVerifier = OauthUtils.generateCodeVerifier();

        authApiClient.authorize(codeVerifier);
        String code = authApiClient.login("ginny.kertzmann", "12345");
        String idToken = authApiClient.token(code, codeVerifier);

        assertNotNull(idToken, "id_token не должен быть null");
    }
}
