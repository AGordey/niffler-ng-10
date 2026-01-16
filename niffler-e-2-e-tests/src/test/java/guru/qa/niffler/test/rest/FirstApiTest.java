package guru.qa.niffler.test.rest;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserApiClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

@Order(1)
@Execution(ExecutionMode.SAME_THREAD)
@Disabled
class FirstApiTest {

    private final UserApiClient usersApiClient = new UserApiClient();

    @Test
    @DisplayName("Таблица должна быть пуста")
    void userListShouldBeEmpty() {
        List<UserJson> userList = usersApiClient.getAll("");
        Assertions.assertTrue(userList.isEmpty());
    }
}
