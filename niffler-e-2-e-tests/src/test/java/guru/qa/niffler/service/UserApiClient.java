package guru.qa.niffler.service;


import guru.qa.niffler.api.UserApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.util.RandomDataUtils;
import io.qameta.allure.Step;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserApiClient implements UserClient {

    private static final Config CFG = Config.getInstance();

    private final Retrofit retrofit = new Retrofit.Builder().baseUrl(CFG.userdataUrl()).addConverterFactory(JacksonConverterFactory.create()).build();
    private final UserApi userApi = retrofit.create(UserApi.class);
    AuthApiClient authApiClient = new AuthApiClient();

    @Nonnull
    @Override
    @Step("Создаём пользователя с логином '{username}'")
    public UserJson createUser(String username, String password) {
        Response<UserJson> response;
        try {
            Response<Void> authResponse = authApiClient.register(username, password);
            assertEquals(HTTP_OK, authResponse.code());
            response = userApi.currentUser(username).execute();
            assertEquals(HTTP_OK, response.code());
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return response.body();
    }

    @Nonnull
    @Override
    @Step("Отправляем {count} входящих запросов в друзья пользователю '{targetUser.username}'")
    public List<UserJson> addIncomeInvitation(UserJson targetUser, int count) {
        List<UserJson> result = new ArrayList<>();
        try {
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    UserJson newUser = createUser(RandomDataUtils.randomUsername(), "12345");
                    Response<UserJson> response = userApi.sendInvitation(targetUser.username(), newUser.username()).execute();
                    assertEquals(HTTP_OK, response.code());
                    result.add(newUser);
                }
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return result;
    }

    @Nonnull
    @Override
    @Step("Отправляем {count} исходящих запросов в друзья от пользователя '{targetUser.username}'")
    public List<UserJson> addOutcomeInvitation(UserJson targetUser, int count) {
        List<UserJson> result = new ArrayList<>();
        try {
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    UserJson newUser = createUser(RandomDataUtils.randomUsername(), "12345");
                    Response<UserJson> responseSendInvitation = userApi.sendInvitation(newUser.username(), targetUser.username()).execute();
                    assertEquals(HTTP_OK, responseSendInvitation.code());
                    result.add(targetUser);
                }
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return result;
    }

    @Nonnull
    @Override
    @Step("Добавляем {count} друзей пользователю '{targetUser.username}'")
    public List<UserJson> addFriend(UserJson targetUser, int count) {
        List<UserJson> result = new ArrayList<>();
        try {
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    UserJson newUser = createUser(RandomDataUtils.randomUsername(), "12345");
                    Response<UserJson> responseSendInvitation = userApi.sendInvitation(newUser.username(), targetUser.username()).execute();
                    assertEquals(HTTP_OK, responseSendInvitation.code());
                    Response<UserJson> responseAcceptInvitation = userApi.acceptInvitation(targetUser.username(), newUser.username()).execute();
                    assertEquals(HTTP_OK, responseAcceptInvitation.code());
                    result.add(newUser);
                }
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return result;
    }

}
