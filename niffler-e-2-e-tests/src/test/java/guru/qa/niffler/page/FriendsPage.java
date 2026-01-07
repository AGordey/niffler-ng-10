package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Calendar;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class FriendsPage {
    private final SelenideElement tableWithAllFriends = $("#friends");
    private final SelenideElement requestsTable = $("#requests");
    private final SelenideElement allPeoplePage = $(byText("All people"));
    private final SelenideElement searchField = $("input[aria-label='search']");
    private final Calendar calendar = new Calendar($(".SpendingCalendar"));

    @Nonnull
    @Step("Check that person is friend")
    public FriendsPage checkExistingFriends(String expectedUsername) {
        tableWithAllFriends.shouldHave(text(expectedUsername));
        return this;
    }

    @Nonnull
    public FriendsPage searchFriends(String username) {
        searchField.setValue(username).pressEnter();
        return this;
    }

    @Nonnull
    public FriendsPage checkNoExistingFriends() {
        tableWithAllFriends.$$("tr").shouldHave(size(0));
        return this;
    }

    @Nonnull
    public FriendsPage checkExistingInvitations(String expectedUsernames) {
        requestsTable.$$("tr").shouldHave(textsInAnyOrder(expectedUsernames));
        return this;
    }

    @Nonnull
    public AllPeoplePage goToPeoplePage() {
        allPeoplePage.click();
        return new AllPeoplePage();
    }
}
