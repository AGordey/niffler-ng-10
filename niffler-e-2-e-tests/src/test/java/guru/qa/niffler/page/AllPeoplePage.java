package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class AllPeoplePage extends BasePage<AllPeoplePage> {
    private final SelenideElement tableWithAllPeople = $("#all");
    private final SelenideElement searchField = $("input[aria-label='search']");

    @Nonnull
    @Step("Проверяем что '{expectedUsernames}' имеет статус Waiting")
    public AllPeoplePage checkWaitingOfUserInvitations(String expectedUsernames) {
        tableWithAllPeople.$$("tr").find(text(expectedUsernames)).shouldHave(text("Waiting..."));
        return this;
    }

    @Nonnull
    @Step("Ищем '{expectedUsernames}' через поиск")
    public AllPeoplePage searchFriends(String username) {
        searchField.setValue(username).pressEnter();
        return this;
    }

}