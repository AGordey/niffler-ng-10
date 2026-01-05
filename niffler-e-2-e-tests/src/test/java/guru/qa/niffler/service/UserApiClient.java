package guru.qa.niffler.service;


import guru.qa.niffler.api.UserApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.util.RandomDataUtils;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserApiClient implements UserClient {

    private static final Config CFG = Config.getInstance();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.userdataUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private final UserApi userApi = retrofit.create(UserApi.class);
    AuthApiClient authApiClient = new AuthApiClient();

    @SneakyThrows
    @Override
    public UserJson createUser(String username, String password) {
        Response<UserJson> response;
        Response<Void> authResponse = authApiClient.register(username, password);
        response = userApi.currentUser(username).execute();
        assertEquals(200, response.code());
        return response.body();
    }

    @SneakyThrows
    @Override
    public List<UserJson> addIncomeInvitation(UserJson targetUser, int count) {
        List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                UserJson newUser = createUser(RandomDataUtils.randomUsername(), "12345");
                userApi.sendInvitation(targetUser.username(), newUser.username()).execute();
                result.add(newUser);
            }
        }
        return result;
    }

    @SneakyThrows
    @Override
    public List<UserJson> addOutcomeInvitation(UserJson targetUser, int count) {
        List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                UserJson newUser = createUser(RandomDataUtils.randomUsername(), "12345");
                userApi.sendInvitation(newUser.username(), targetUser.username()).execute();
                result.add(targetUser);
            }
        }
        return result;
    }

    @SneakyThrows
    @Override
    public List<UserJson> addFriend(UserJson targetUser, int count) {
        List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                UserJson newUser = createUser(RandomDataUtils.randomUsername(), "12345");
                userApi.sendInvitation(newUser.username(), targetUser.username()).execute();
                userApi.acceptInvitation(targetUser.username(), newUser.username()).execute();
                result.add(newUser);
            }
        }
        return result;
    }

}
