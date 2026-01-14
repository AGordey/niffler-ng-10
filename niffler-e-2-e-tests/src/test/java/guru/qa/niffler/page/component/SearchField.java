package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Selenide.$;

public class SearchField {

    private final SelenideElement self = $("input[aria-label='search']");
    private final SelenideElement searchButton = self.$("button[id='input-submit']");
    private final SelenideElement clearSearchButton = self.$("button[id='input-clear']");

    @Step("Ищем '{query}' в поисковой строке")
    public SearchField search(String query) {
        self.setValue(query);
        searchButton.click();
        return this;
    }

    @Step("Очистить поисковую строку компонента поиска")
    public SearchField clearIfNotEmpty() {
        String currentValue = self.getValue();
        if (currentValue != null && !currentValue.isEmpty()) {
            self.parent().$("button#input-clear").click();
        }
        self.should(empty);
        return this;
    }

}
