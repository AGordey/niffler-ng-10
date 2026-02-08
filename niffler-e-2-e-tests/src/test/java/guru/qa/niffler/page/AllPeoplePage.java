package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;

@ParametersAreNonnullByDefault
public class AllPeoplePage extends BasePage<AllPeoplePage> {

    private static final String URL = CFG.frontUrl() + "people/all";

    private final SelenideElement tableWithAllPeople;
    private final SelenideElement searchField;

    public AllPeoplePage(SelenideDriver driver) {
        super(driver);
        this.tableWithAllPeople = driver.$("#all");
        this.searchField = driver.$("input[aria-label='search']");
    }

    public AllPeoplePage() {
        this.tableWithAllPeople = Selenide.$("#all");
        this.searchField = Selenide.$("input[aria-label='search']");
    }

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