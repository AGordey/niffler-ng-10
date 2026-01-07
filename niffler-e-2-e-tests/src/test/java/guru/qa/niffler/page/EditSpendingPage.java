package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditSpendingPage {
    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement saveBtn = $("#save");

    @Nonnull
    public EditSpendingPage setNewSpendingDescription(String description) {
        descriptionInput.val(description);
//    saveBtn.click();
        return this;
    }

    @Nonnull
    public MainPage save() {
        saveBtn.click();
        return new MainPage();
    }
}
