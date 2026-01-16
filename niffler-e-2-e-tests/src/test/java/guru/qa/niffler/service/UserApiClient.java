package guru.qa.niffler.service;


import com.google.common.base.Stopwatch;
import guru.qa.niffler.api.UserApi;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.util.RandomDataUtils;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public final class UserApiClient extends RestClient implements UserClient {

    private final UserApi userApi;
    AuthApiClient authApiClient = new AuthApiClient();

    public UserApiClient() {
        //Создаем клиента через которого выполняются запросы на URL
        super(CFG.userdataUrl());
        // Создаем объект ретрофита что бы им уже  работать в клиенте
        // (аналог private final UserApi userApi = retrofit.create(UserApi.class);)
        this.userApi = create(UserApi.class);  // Создаем объект ретрофита что бы им уже
        // работать в клиенте (аналог     private final UserApi userApi= retrofit.create(UserApi.class);)
    }

    @Nonnull
    @Override
    @Step("Создаём пользователя с логином '{username}'")
    public UserJson createUser(String username, String password) {
        Response<UserJson> response;
        try {
            Response<Void> authResponse = authApiClient.register(username, password);
            assertEquals(HTTP_OK, authResponse.code());

            Stopwatch sw = Stopwatch.createStarted();
            long maxWaitTime = 10_000;

            while (sw.elapsed(TimeUnit.MILLISECONDS) < maxWaitTime) {
                try {
                    UserJson userJson = userApi.currentUser(username).execute().body();
                    if (userJson != null && userJson.id() != null) {
                        return userJson;
                    } else {
                        Thread.sleep(100);
                    }
                } catch (IOException e) {
                    // just wait
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        throw new AssertionError("User was not created");
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

    @Nonnull
    public List<UserJson> getAll(String username) {
        List<UserJson> resultList = null;
        try {
            Response<List<UserJson>> response = userApi.allUsers(username, null).execute();
            assertEquals(HTTP_OK, response.code());
            resultList = response.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resultList;
    }
}
