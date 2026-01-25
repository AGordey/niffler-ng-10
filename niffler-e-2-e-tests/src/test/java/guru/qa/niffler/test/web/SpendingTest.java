package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.util.ScreenDiffResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.util.RandomDataUtils.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@WebTest
public class SpendingTest {

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

        Selenide.open(LoginPage.URL, LoginPage.class)
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
        Selenide.open(LoginPage.URL, LoginPage.class)
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


    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ScreenShotTest("img/spendings/expected-stat.png")
    void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password());

        BufferedImage actual = ImageIO.read($("canvas[role='img']").screenshot());
        assertFalse(new ScreenDiffResult(
                expected,
                actual
        ));
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
    @ScreenShotTest("img/spendings/expected-stat.png")
    void checkStatComponentAfterDeleteSpendingTest(UserJson user, BufferedImage expected) {
        Selenide.open(LoginPage.URL, LoginPage.class)
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
    @ScreenShotTest("img/spendings/expected-stat.png")
    void checkStatComponentAfterEditSpendingTest(UserJson user, BufferedImage expected) {
        Selenide.open(LoginPage.URL, LoginPage.class)
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
                    ), @Spending(
                    category = "Обучение углубленное",
                    description = "Тест 2",
                    amount = 99999
            )}

    )
    @ScreenShotTest("img/spendings/expected-spending-archive.png")
    void checkStatComponentWithArchivedSpendingTest(UserJson user, BufferedImage expected) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToProfilePage()
                .makeCategoryArchive("Обучение");
        Selenide.open(MainPage.URL, MainPage.class)
                .assertCategoriesMatchBySize()
                .checkChartImage(expected);
    }

}
