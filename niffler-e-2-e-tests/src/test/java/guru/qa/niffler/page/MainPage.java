package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.utils.ScreenDiffResult;
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
    private final SelenideElement spendingTable ;
    private final SelenideElement menuOfUser ;
    private final SelenideElement profileOfUser ;
    private final SelenideElement statisticsPart ;
    private final SelenideElement friendsPage ;
    private final SelenideElement allPeople;
    private final SelenideElement searchField ;
    private final SelenideElement deleteButton ;
    private final SpendingTable spendingTableComponent ;
    private final SearchField searchFieldComponent ;
    private final SelenideElement chart;
    // Элементы таблицы: категории в колонке "Category"
    private ElementsCollection tableCategories ;
    // Элементы легенды под диаграммой
    private ElementsCollection chartCategories;

    public MainPage(SelenideDriver driver) {
        super(driver);
        this.spendingTable = driver.$("#spendings");
        this.menuOfUser = driver.$("[data-testid='PersonIcon']");
        this.profileOfUser = driver.$(withText("Profile"));
        this.statisticsPart = driver.$("#stat");
        this.friendsPage = driver.$(byText("Friends"));
        this.allPeople = driver.$(withText("All People"));
        this.searchField = driver.$("input[aria-label='search']");
        this.deleteButton = driver.$("#delete");
        this.spendingTableComponent = new SpendingTable();
        this.searchFieldComponent = new SearchField();
        this.chart = driver.$("canvas[role='img']");
        // Элементы таблицы: категории в колонке "Category"
        this.tableCategories = driver.$$("tbody tr td:nth-child(2) span");
        // Элементы легенды под диаграммой
        this.chartCategories = driver.$$("#legend-container ul li");
    }

    public MainPage() {
        this.spendingTable = Selenide.$("#spendings");
        this.menuOfUser = Selenide.$("[data-testid='PersonIcon']");
        this.profileOfUser = Selenide.$(withText("Profile"));
        this.statisticsPart = Selenide.$("#stat");
        this.friendsPage = Selenide.$(byText("Friends"));
        this.allPeople = Selenide.$(withText("All People"));
        this.searchField = Selenide.$("input[aria-label='search']");
        this.deleteButton = Selenide.$("#delete");
        this.spendingTableComponent = new SpendingTable();
        this.searchFieldComponent = new SearchField();
        this.chart = Selenide.$("canvas[role='img']");
        // Элементы таблицы: категории в колонке "Category"
        this.tableCategories = Selenide.$$("tbody tr td:nth-child(2) span");
        // Элементы легенды под диаграммой
        this.chartCategories = Selenide.$$("#legend-container ul li");
    }

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
    public MainPage checkChartImage(BufferedImage expected) {
        Selenide.sleep(3000);
        BufferedImage actual = null;
        try {
            actual = ImageIO.read(chart.screenshot());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertFalse(new ScreenDiffResult(actual, expected));
        return this;
    }
}
