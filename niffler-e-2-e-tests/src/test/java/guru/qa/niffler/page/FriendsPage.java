package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class FriendsPage extends BasePage<FriendsPage> {

    private static final String URL = CFG.frontUrl() + "people/friends";

    private final SelenideElement tableWithAllFriends = $("#simple-tabpanel-friends");
    private final SelenideElement alertWindow = $("[aria-describedby='alert-dialog-slide-description']");

    private final SelenideElement requestsTable = $("#requests");
    private final ElementsCollection friendsRequests = $$("#requests");
    private final SelenideElement allPeoplePage = $(byText("All people"));
    private final SelenideElement searchField = $("input[aria-label='search']");


    @Step("Проверяем, что пользователь '{expectedUsername}' находится в списке друзей")
    @Nonnull
    public FriendsPage checkExistingFriends(String expectedUsername) {
        tableWithAllFriends.shouldHave(text(expectedUsername));
        return this;
    }

    @Step("Выполняем поиск друзей по имени: '{username}'")
    @Nonnull
    public FriendsPage searchFriends(String username) {
        searchField.setValue(username).pressEnter();
        return this;
    }

    @Step("Проверяем, что список друзей пуст")
    @Nonnull
    public FriendsPage checkNoExistingFriends() {
        tableWithAllFriends.$$("tr").shouldHave(size(0));
        return this;
    }

    @Step("Проверяем наличие входящих запросов от: {expectedUsernames}")
    @Nonnull
    public FriendsPage checkExistingInvitations(String... expectedUsernames) {
        // Обратите внимание: textsInAnyOrder ожидает vararg, поэтому метод должен принимать String...
        requestsTable.$$("tr").shouldHave(textsInAnyOrder(expectedUsernames));
        return this;
    }

    @Step("Принимаем запрос в друзья от пользователя '{username}'")
    @Nonnull
    public FriendsPage acceptFriendship(String username) {
        friendsRequests.findBy(text(username)).$(byText("Accept")).click();
        return this;
    }

    @Step("Отклоняем запрос в друзья от пользователя '{username}'")
    @Nonnull
    public FriendsPage declineFriendship(String username) {
        friendsRequests.findBy(text(username)).$(byText("Decline")).click();
        alertWindow.$(byText("Decline")).click();
        return this;
    }

    @Step("Переходим на страницу 'All People'")
    @Nonnull
    public AllPeoplePage goToPeoplePage() {
        allPeoplePage.click();
        return new AllPeoplePage();
    }
}
