package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class AllPeoplePage {
    private final SelenideElement tableWithAllPeople = $("#all");
    private final SelenideElement searchField = $("input[aria-label='search']");

    @Nonnull
    public AllPeoplePage checkWaitingOfUserInvitations(String expectedUsernames) {
        tableWithAllPeople.$$("tr").find(text(expectedUsernames)).shouldHave(text("Waiting..."));
        return this;
    }

    @Nonnull
    public AllPeoplePage searchFriends(String username) {
        searchField.setValue(username).pressEnter();
        return this;
    }

}