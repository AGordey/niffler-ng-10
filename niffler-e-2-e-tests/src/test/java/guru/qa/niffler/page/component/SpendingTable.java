package guru.qa.niffler.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class SpendingTable {
    private final SelenideElement self = $("#spendings");
    private final SelenideElement periodOfSpendings = self.$("#period");
    private final SelenideElement editSpendingButton = self.$("[aria-label='Edit spending']");
    private final ElementsCollection menuOfPeriod = $("[role='listbox']").$$("li");
    private final SelenideElement deleteButton = self.$("#delete");
    private final SearchField searchField = new SearchField();
    private final ElementsCollection spendingRows = self.$("tbody").$$("tr");


    public SpendingTable selectPeriod(DataFilterValues period) {
        String periodStr = period.name();
        periodOfSpendings.click();
        menuOfPeriod.find(text(periodStr)).click();
        return this;
    }

    public EditSpendingPage editSpending(String description) {
        searchSpendingByDescription(description);
        editSpendingButton.click();
        return new EditSpendingPage();
    }

    public SpendingTable searchSpendingByDescription(String description) {
        searchField.search(description);
        return this;
    }

    public SpendingTable deleteSpending(String description) {
        searchSpendingByDescription(description);
        spendingRows.first().find("[type='checkbox']").click();
        deleteButton.$(byText("Delete")).click();
        return this;
    }

    public SpendingTable checkTableContains(String... expectedSpends) {
        for (String spend : expectedSpends) {
            searchField.clearIfNotEmpty();
            searchField.search(spend);
            spendingRows.find(text(spend)).should(visible);
        }
        return this;
    }

    public SpendingTable checkTableSize(int expectedSize) {
        spendingRows.should(size(expectedSize));
        return this;
    }
}
