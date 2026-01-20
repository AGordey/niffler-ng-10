package guru.qa.niffler.test.rest;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.List;

@Isolated
class LastApiTest {

    private final UserApiClient usersApiClient = new UserApiClient();

    @Test
    @DisplayName("Таблица должна быть не пустой")
    void userListShouldNotBeEmpty() {
        List<UserJson> userList = usersApiClient.getAll("");
        Assertions.assertFalse(userList.isEmpty());
    }
}
