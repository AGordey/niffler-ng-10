package guru.qa.niffler.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jknack.handlebars.internal.lang3.StringUtils;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.CodeInterceptor;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.utils.OAuthUtils;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import retrofit2.Response;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static guru.qa.niffler.utils.OAuthUtils.generateCodeChallenge;

@ParametersAreNonnullByDefault
public final class AuthApiClient extends RestClient {

    public static final String RESPONSE_TYPE = "code";
    public static final String CLIENT_ID = "client";
    public static final String SCOPE = "openid";
    public static final String REDIRECT_URI = "http://localhost:3000/authorized";
    public static final String CODE_CHALLENGE_METHOD = "S256";
    public static final String GRANT_TYPE = "authorization_code";
    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(), true, new CodeInterceptor() );
        this.authApi = create(AuthApi.class);
    }
    @SneakyThrows
    public String login(String username, String password) {
        final String codeVerifier = OAuthUtils.generateCodeVerifier();
        final String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);
        final String redirectUri = CFG.frontUrl() + "authorized";
        final String clientId = "client";

        authApi.authorize(
                "code",
                clientId,
                "openid",
                redirectUri,
                codeChallenge,
                "S256"
        ).execute();

        authApi.login(
                username,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        ).execute();
        System.out.println("ApiLoginExtension.getCode() " + ApiLoginExtension.getCode());
        Response<JsonNode> tokenResponse = authApi.token(
                ApiLoginExtension.getCode(),
                redirectUri,
                codeVerifier,
                "authorization_code",
                clientId
                ).execute();
        System.out.println(tokenResponse.body());
        return tokenResponse.body().get("id_token").asText();
    }

    @Step("Регистрируем пользователя с username {'username'} password {'password'} ")
    public Response<Void> register(String username, String password) throws IOException {
        authApi.requestRegisterForm().execute();
        return authApi.register(username, password, password, ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")).execute();
    }

    @Step("Выполнение запроса @GET(oauth2/authorize)")
    public void authorizeCall(String codeVerifier) throws IOException {
        authApi.authorize(RESPONSE_TYPE, CLIENT_ID, SCOPE, REDIRECT_URI, generateCodeChallenge(codeVerifier), CODE_CHALLENGE_METHOD).execute();
    }

    @Step("Выполнение запроса @POST(login)")
    public String loginCall(String username, String password) throws IOException {
        var response = authApi.login(username, password, ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")).execute();
        String codeVerifier = StringUtils.substringAfter(response.raw().request().url().toString(), "code=");
        return codeVerifier;
    }

    @Step("Выполнение запроса @POST(oauth2/token)")
    public String tokenCall(String code, String codeVerifier) throws IOException {
        var response = authApi.token(code, REDIRECT_URI, codeVerifier, GRANT_TYPE, CLIENT_ID).execute();
        if (response.body() != null) {
            return response.body().path("id_token").asText();
        }
        throw new NullPointerException("Body of token call is null or doesn't contain id_token field") ;
    }
}
