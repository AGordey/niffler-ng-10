package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {
    private final SelenideElement spendingTable = $("#spendings");
    private final SelenideElement menuOfUser = $("[data-testid='PersonIcon']");
    private final SelenideElement profileOfUser = $(withText("Profile"));
    private final SelenideElement statisticsPart = $("#stat");
    private final SelenideElement friendsPage = $(byText("Friends"));
    private final SelenideElement allPeople = $(withText("All People"));
    private final SelenideElement searchField = $("input[aria-label='search']");

    @Step("Проверяем, что главная страница загружена")
    @Nonnull
    public MainPage checkThatPageLoaded() {
        spendingTable.should(visible);
        statisticsPart.should(visible);
        return this;
    }

    @Step("Редактируем трату с описанием '{description}'")
    @Nonnull
    public EditSpendingPage editSpending(String description) {
        spendingTable.$$("tbody tr").find(text(description)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    @Step("Проверяем, что таблица содержит трату: '{description}'")
    @Nonnull
    public MainPage checkThatTableContains(String description) {
        spendingTable.$$("tbody tr").find(text(description)).should(visible);
        return this;
    }

    @Step("Выполняем поиск траты по описанию: '{spendingDescription}'")
    @Nonnull
    public MainPage searchSpending(String spendingDescription) {
        searchField.setValue(spendingDescription).pressEnter();
        return this;
    }

    @Step("Переходим на страницу профиля")
    @Nonnull
    public ProfilePage goToProfilePage() {
        menuOfUser.click();
        profileOfUser.click();
        return new ProfilePage();
    }

    @Step("Переходим на страницу друзей")
    @Nonnull
    public FriendsPage goToFriendsPage() {
        menuOfUser.click();
        friendsPage.click();
        return new FriendsPage();
    }

    @Step("Переходим на страницу 'All People'")
    @Nonnull
    public AllPeoplePage goToAllPeoplePage() {
        menuOfUser.click();
        allPeople.click();
        return new AllPeoplePage();
    }
}
