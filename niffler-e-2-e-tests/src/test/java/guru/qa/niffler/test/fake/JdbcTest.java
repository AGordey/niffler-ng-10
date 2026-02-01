package guru.qa.niffler.test.fake;

import guru.qa.niffler.jupiter.extension.SpendClientInjector;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.UserDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;

@ExtendWith(SpendClientInjector.class)
public class JdbcTest {

    private SpendClient spendCLient;
    @Test
    void txTest() {
        String randomUsername = RandomDataUtils.randomUsername();
        SpendJson spend = spendCLient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "Кошкин дом",
                                randomUsername,
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "Кошкин дом ноябрь",
                        randomUsername
                )
        );

        System.out.println(spend);
    }

    static UserDbClient usersDbClient = new UserDbClient();

    @ValueSource(strings = {
            "sanek-"
    })
    @ParameterizedTest
    void springJdbcTest(String uname) {
        UserJson user = usersDbClient.createUser(
                uname.concat(RandomDataUtils.randomName()),
                "12345"
        );

        usersDbClient.addIncomeInvitation(user, 1);
        usersDbClient.addOutcomeInvitation(user, 1);
        usersDbClient.addFriend(user, 3);
    }
}
