package guru.qa.niffler.service;

import com.github.jknack.handlebars.internal.lang3.StringUtils;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static guru.qa.niffler.utils.OauthUtils.generateCodeChallenge;

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
        super(CFG.authUrl(), true);
        this.authApi = create(AuthApi.class);
    }

    @Step("Регистрируем пользователя с username {'username'} password {'password'} ")
    public Response<Void> register(String username, String password) throws IOException {
        authApi.requestRegisterForm().execute();
        return authApi.register(username, password, password, ThreadSafeCookieStore.INSTANCE.xsrfCookie()).execute();
    }

    @Step("Выполнение запроса @GET(oauth2/authorize)")
    public void authorize(String codeVerifier) throws IOException {
        authApi.authorize(RESPONSE_TYPE, CLIENT_ID, SCOPE, REDIRECT_URI, generateCodeChallenge(codeVerifier), CODE_CHALLENGE_METHOD).execute();
    }

    @Step("Выполнение запроса @POST(login)")
    public String login(String username, String password) throws IOException {
        var response = authApi.login(username, password, ThreadSafeCookieStore.INSTANCE.xsrfCookie()).execute();
        String codeVerifier = StringUtils.substringAfter(response.raw().request().url().toString(), "code=");
        return codeVerifier;
    }

    @Step("Выполнение запроса @POST(oauth2/token)")
    public String token(String code, String codeVerifier) throws IOException {
        var response = authApi.token(code, REDIRECT_URI, codeVerifier, GRANT_TYPE, CLIENT_ID).execute();
        if (response.body() != null) {
            return response.body().path("id_token").asText();
        }
        throw new NullPointerException("Body of token call is null or doesn't contain id_token field") ;
    }
}
