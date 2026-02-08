package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
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

    private final SelenideElement tableWithAllFriends ;
    private final SelenideElement alertWindow ;
    private final SelenideElement requestsTable ;
    private final SelenideElement allPeoplePage ;
    private final SelenideElement searchField ;
    private final ElementsCollection friendsRequests ;

    public FriendsPage() {
        this.tableWithAllFriends = Selenide.$("#simple-tabpanel-friends");
        this.alertWindow = Selenide.$("[aria-describedby='alert-dialog-slide-description']");
        this.requestsTable = Selenide. $("#requests");
        this.allPeoplePage = Selenide.$(byText("All people"));
        this.searchField = Selenide.$("input[aria-label='search']");
        this.friendsRequests = Selenide.$$("#requests");
    }

    public FriendsPage(SelenideDriver driver) {
        super(driver);
        this.tableWithAllFriends = driver.$("#simple-tabpanel-friends");
        this.alertWindow = driver.$("[aria-describedby='alert-dialog-slide-description']");
        this.requestsTable =driver. $("#requests");
        this.allPeoplePage = driver.$(byText("All people"));
        this.searchField = driver.$("input[aria-label='search']");
        this.friendsRequests = driver.$$("#requests");
    }

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
