package guru.qa.niffler.test.fake;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.AuthApiClient;
import guru.qa.niffler.utils.OAuthUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebTest
public class OAuthTest {

    private final AuthApiClient authApiClient = new AuthApiClient();

    @Test
    void oauth2TokenTest() throws IOException {
        String codeVerifier = OAuthUtils.generateCodeVerifier();

        authApiClient.authorizeCall(codeVerifier);
        String code = authApiClient.loginCall("ginny.kertzmann", "12345");
        String idToken = authApiClient.tokenCall(code, codeVerifier);

        assertNotNull(idToken, "id_token не должен быть null");
    }

    @Test
    @ApiLogin(username="ginny.kertzmann",password = "12345")
    void oauthTest(@Token String token, UserJson user) {
        System.out.println(user);
        Assertions.assertNotNull(token);
    }

    @Test
    @User
    @ApiLogin
    void oauthTest2(@Token String token, UserJson user) {
        System.out.println(user);
        Assertions.assertNotNull(token);
    }

}
