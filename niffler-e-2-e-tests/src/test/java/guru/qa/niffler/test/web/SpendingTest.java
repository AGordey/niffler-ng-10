package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.StatComponent;
import guru.qa.niffler.utils.SelenideUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static guru.qa.niffler.utils.RandomDataUtils.*;

@WebTest
public class SpendingTest {

    private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);

    Header header = new Header();

    @User(
            spendings = {@Spending(
                    category = "Учеба",
                    amount = 89900,
                    currency = CurrencyValues.RUB,
                    description = "Обучение Niffler 2.0 юбилейный поток")}
    )
    @Test
    @DisplayName("Редактирование затраты")
    void spendingDescriptionShouldBeEditedByTableAction(UserJson user) {
        final String newDescription = "Обучение Niffler Next Generation";
        final String spendingDescription = user.testData().spendings().getFirst().description();

        driver.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .searchSpending(spendingDescription)
                .editSpending(spendingDescription)
                .setNewSpendingDescription(newDescription)
                .save()
                .checkSnackbarText("Spending is edited successfully")
                .checkThatTableContains(newDescription);
    }

    @User()
    @Test
    @DisplayName("Добавление новой затраты")
    void addNewSpending(UserJson user) {
        String description = randomSentence(1);
        driver.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password());
        header.addSpendingPage()
                .setAmount(String.valueOf(randomNumber()))
                .setCurrency(CurrencyValues.RUB)
                .setCategory(randomCategoryName())
                .setDate(randomDate())
                .setDescription(description)
                .save()
                .checkSnackbarText("New spending is successfully created")
                .checkThatTableContains(description);
    }


    @SneakyThrows
    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ScreenShotTest(value = "img/spendings/expected-stat.png")
    void checkStatComponentTest(UserJson user, BufferedImage expected) {
        driver.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .checkChartImage(expected);

    }

    @User(
            spendings = {
                    @Spending(
                            category = "Автомобиль",
                            description = "Ремонт машины",
                            amount = 105000
                    ),
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990
                    )
            }
    )
    @ScreenShotTest(value = "img/spendings/expected-stat.png")
    void checkStatComponentAfterDeleteSpendingTest(UserJson user, BufferedImage expected) {
        driver.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .assertCategoriesMatchByName()
                .deleteSpending("Ремонт машины")
                .assertCategoriesMatchByName()
                .checkChartImage(expected);
    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 105000
            )

    )
    @ScreenShotTest(value = "img/spendings/expected-stat.png")
    void checkStatComponentAfterEditSpendingTest(UserJson user, BufferedImage expected) {
        driver.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .assertCategoriesMatchByName()
                .editSpending("Обучение Advanced 2.0")
                .setAmount("79990")
                .save()
                .assertCategoriesMatchByName()
                .checkChartImage(expected);
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Тест 1",
                            amount = 77777
                    ),
                    @Spending(
                            category = "Обучение углубленное",
                            description = "Тест 2",
                            amount = 99999
                    )}

    )
    @ScreenShotTest(value = "img/spendings/expected-spending-archive.png")
    void checkStatComponentWithArchivedSpendingTest(UserJson user, BufferedImage expected) {
        driver.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToProfilePage()
                .makeCategoryArchive("Обучение");
        driver.open(MainPage.URL, MainPage.class)
                .assertCategoriesMatchBySize()
                .checkChartImage(expected);
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Тест 1",
                            amount = 77777
                    ),
                    @Spending(
                            category = "Обучение углубленное",
                            description = "Тест 2",
                            amount = 55555
                    ),
                    @Spending(
                            category = "Вождение",
                            description = "Тест 2",
                            amount = 45630
                    )}

    )
    @Test
    void checkBubblesInOrder(UserJson user) {
        Bubble[] bubbles = new Bubble[]{
                new Bubble(Color.YELLOW, "Обучение 77777 " + CurrencyValues.RUB.currencySign),
                new Bubble(Color.GREEN, "Обучение углубленное 55555 " + CurrencyValues.RUB.currencySign),
                new Bubble(Color.BLUE100, "Вождение 45630 " + CurrencyValues.RUB.currencySign)
        };

        StatComponent statComponent = new StatComponent();
        driver.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password());

        statComponent.checkBubblesInOrder(bubbles);

    }

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Тест 1",
                            amount = 77777
                    ),
                    @Spending(
                            category = "Обучение углубленное",
                            description = "Тест 2",
                            amount = 55555
                    ),
                    @Spending(
                            category = "Вождение",
                            description = "Тест 2",
                            amount = 45630
                    )}

    )
    @Test
    void checkFirstBubbleInAnyOrder(UserJson user) {
        Bubble[] bubbles = new Bubble[]{
                new Bubble(Color.YELLOW, "Обучение 77777 " + CurrencyValues.RUB.currencySign),
                new Bubble(Color.GREEN, "Обучение углубленное 55555 " + CurrencyValues.RUB.currencySign),
                new Bubble(Color.BLUE100, "Вождение 45630 " + CurrencyValues.RUB.currencySign)
        };

        StatComponent statComponent = new StatComponent();
        driver.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password());
        statComponent.checkBubblesInAnyOrder(bubbles);

    }

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Тест 1",
                            amount = 77777
                    ),
                    @Spending(
                            category = "Обучение углубленное",
                            description = "Тест 2",
                            amount = 55555
                    ),
                    @Spending(
                            category = "Вождение",
                            description = "Тест 2",
                            amount = 45630
                    )}

    )
    @Test
    void checkBubbleContainSomeBubbles(UserJson user) {
        Bubble[] bubbles = new Bubble[]{
                new Bubble(Color.GREEN, "Обучение углубленное 55555 " + CurrencyValues.RUB.currencySign),
                new Bubble(Color.BLUE100, "Вождение 45630 " + CurrencyValues.RUB.currencySign)
        };

        StatComponent statComponent = new StatComponent();
        driver.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password());
        statComponent.checkBubblesContainSomeBubbles(bubbles);

    }

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Тест 1",
                            amount = 77777
                    ),
                    @Spending(
                            category = "Обучение углубленное",
                            description = "Тест 2",
                            amount = 55555
                    ),
                    @Spending(
                            category = "Вождение",
                            description = "Тест 3",
                            amount = 45630
                    )}

    )
    @Test
    void checkSpendingsInTable(UserJson user) {

        SpendingTable spendingTable = new SpendingTable();
        driver.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password());
        spendingTable.checkSpendingsInOrder(user.testData().spendings());

    }

}
