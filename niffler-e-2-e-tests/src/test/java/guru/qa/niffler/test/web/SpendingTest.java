package guru.qa.niffler.test.web;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.component.Header;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.util.RandomDataUtils.*;

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
}
