package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.SpendConditions.haveSpendingInOrder;

public class SpendingTable extends BaseComponent<SpendingTable> {

    private final SelenideElement periodOfSpendings = self.$("#period");
    private final SelenideElement editSpendingButton = self.$("[aria-label='Edit spending']");
    private final ElementsCollection menuOfPeriod = $("[role='listbox']").$$("li");
    private final SelenideElement deleteButtonOnPage = self.$("#delete");
    private final SelenideElement deleteButtonInDialog = $("[aria-describedby='alert-dialog-slide-description']").$(byText("Delete"));
    private final SearchField searchField = new SearchField();
    private final ElementsCollection spendingRows = self.$("tbody").$$("tr");

    public SpendingTable() {
        super($("#spendings"));
    }

    @Step("Выбираем период '{period}' в выпадающем меню")
    public SpendingTable selectPeriod(DataFilterValues period) {
        String periodStr = period.name();
        periodOfSpendings.click();
        menuOfPeriod.find(text(periodStr)).click();
        return this;
    }

    @Step("Ищем запись по описанию '{description}'")
    public EditSpendingPage editSpending(String description) {
        searchSpendingByDescription(description);
        editSpendingButton.click();
        return new EditSpendingPage();
    }

    @Step("Поиск запись о растрате с описанием '{description}'")
    public SpendingTable searchSpendingByDescription(String description) {
        searchField.search(description);
        return this;
    }

    @Step("Удаляем запись о растрате")
    public SpendingTable deleteSpending(String description) {
        searchSpendingByDescription(description);
        spendingRows.first().find("[type='checkbox']").click();
        deleteButtonOnPage.click();
        deleteButtonInDialog.click();
        return this;
    }

    @Step("Проверяем, что таблица содержит затраты: {expectedSpends}")
    public SpendingTable checkTableContains(String... expectedSpends) {
        for (String spend : expectedSpends) {
            searchField.clearIfNotEmpty();
            searchField.search(spend);
            spendingRows.find(text(spend)).should(visible);
        }
        return this;
    }

    @Step("Проверяем, что таблица содержит '{expectedSize}' элемент(ов)")
    public SpendingTable checkTableSize(int expectedSize) {
        spendingRows.should(size(expectedSize));
        return this;
    }

    @Step("Проверяем, что таблица содержит затраты ")
    public SpendingTable checkSpendingsInOrder(List<SpendJson> expectedSpendings) {
        spendingRows.should(haveSpendingInOrder(expectedSpendings));
        return this;
    }


}
