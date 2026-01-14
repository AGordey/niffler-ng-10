package guru.qa.niffler.test.web;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.util.RandomDataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.util.RandomDataUtils.*;

@WebTest
public class SpendingTest {

    private static final Config CFG = Config.getInstance();
    Header header = new Header();
    EditSpendingPage editSpendingPage = new EditSpendingPage();
    MainPage mainPage = new MainPage();

    @User(
            spendings = {@Spending(
                    category = "Учеба",
                    amount = 89900,
                    currency = CurrencyValues.RUB,
                    description = "Обучение Niffler 2.0 юбилейный поток")}
    )
    @Test
    void spendingDescriptionShouldBeEditedByTableAction(UserJson user) {
        final String newDescription = "Обучение Niffler Next Generation";
        final String spendingDescription = user.testData().spendings().getFirst().description();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .searchSpending(spendingDescription)
                .editSpending(spendingDescription)
                .setNewSpendingDescription(newDescription)
                .save()
                .checkThatTableContains(newDescription);
    }

    @User
    @Test
    void addNewSpending(UserJson user) {
        String description = randomSentence(1);
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password());
        header.addSpendingPage();
        editSpendingPage.setAmount(String.valueOf(randomNumber()))
                .setCurrency(CurrencyValues.USD)
                .setCategory(randomCategoryName())
                .setDate(randomDate())
                .setDescription(description)
                .save();
        mainPage.checkThatTableContains(description);
    }
}
