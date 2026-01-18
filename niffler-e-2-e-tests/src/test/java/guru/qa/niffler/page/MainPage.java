package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.util.ScreenDiffResult;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

    public static final String URL = CFG.frontUrl() + "main";
    private final SelenideElement spendingTable = $("#spendings");
    private final SelenideElement menuOfUser = $("[data-testid='PersonIcon']");
    private final SelenideElement profileOfUser = $(withText("Profile"));
    private final SelenideElement statisticsPart = $("#stat");
    private final SelenideElement friendsPage = $(byText("Friends"));
    private final SelenideElement allPeople = $(withText("All People"));
    private final SelenideElement searchField = $("input[aria-label='search']");
    private final SelenideElement deleteButton = $("#delete");
    SpendingTable spendingTableComponent = new SpendingTable();
    SearchField searchFieldComponent = new SearchField();
    private final SelenideElement chart = $("#chart canvas");
    // Элементы таблицы: категории в колонке "Category"
    private ElementsCollection tableCategories = $$("tbody tr td:nth-child(2) span");
    // Элементы легенды под диаграммой
    private ElementsCollection chartCategories = $$("#legend-container ul li");

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

    @Step("Удаляем трату с описанием '{description}'")
    @Nonnull
    public MainPage deleteSpending(String description) {
        spendingTableComponent.deleteSpending(description);
        return this;
    }

    @Step("Проверяем, что таблица содержит трату: '{description}'")
    @Nonnull
    public MainPage checkThatTableContains(String description) {
        spendingTable.$$("tbody tr").find(text(description)).should(visible);
        return this;
    }

    @Step("Выполняем поиск затраты по описанию: '{spendingDescription}'")
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

    @Step("Сравнение списков затрат в таблице затрат и под аналитикой по названиям категорий ")
    public MainPage assertCategoriesMatchByName() {
        searchFieldComponent.clearIfNotEmpty();
        var tableCategoryNames = tableCategories.texts();         // Извлекаем тексты из таблицы
        // Извлекаем тексты из легенды и убираем сумму (оставляем только название)
        var chartCategoryNames = chartCategories.texts().stream()
                .map(text -> text.replaceAll("\\s+\\d+\\s*₽$", "")) // удаляем " 10000 ₽" и т.п.
                .toList();
        Assertions.assertTrue(
                chartCategoryNames.containsAll(tableCategoryNames),
                "Названия категорий в таблице и на диаграмме не совпадают"
        );
        return this;
    }
    @Step("Сравнение списков затрат отображаемых в таблице затрат и под аналитикой по количеству ")
    public MainPage assertCategoriesMatchBySize() {
        searchFieldComponent.clearIfNotEmpty();
        var tableCategoryNames = tableCategories.texts();         // Извлекаем тексты из таблицы
        // Извлекаем тексты из легенды и убираем сумму (оставляем только название)
        var chartCategoryNames = chartCategories.texts().stream()
                .map(text -> text.replaceAll("\\s+\\d+\\s*₽$", "")) // удаляем " 10000 ₽" и т.п.
                .toList();
        Assertions.assertEquals(
                chartCategoryNames.size(),
                tableCategoryNames.size(),
                "Количество категорий в таблице и на диаграмме не совпадает"
        );
        return this;
    }

    @Nonnull
    @Step("Проверка скриншотов кружка статистики")
    public MainPage checkChartImage(BufferedImage expected) throws IOException {
        Selenide.sleep(2500);
        BufferedImage actual = ImageIO.read(chart.screenshot());
        assertFalse(new ScreenDiffResult(actual, expected));
        return this;
    }
}
