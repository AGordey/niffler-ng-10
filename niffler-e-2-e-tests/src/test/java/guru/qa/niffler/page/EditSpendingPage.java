package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.page.component.Calendar;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class EditSpendingPage {
    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement saveOrAddBtn = $("#save");
    private final SelenideElement amountInput = $("#amount");
    private final SelenideElement categoryInput = $("#category");
    private final SelenideElement currency = $("#currency");


    private final Calendar calendar = new Calendar();

    @Step("Устанавливаем описание траты: '{description}'")
    @Nonnull
    public EditSpendingPage setNewSpendingDescription(String description) {
        descriptionInput.val(description);
        return this;
    }

    @Step("Сохраняем изменения и переходим на главную страницу")
    @Nonnull
    public MainPage save() {
        saveOrAddBtn.click();
        return new MainPage();
    }

    @Step("Устанавливаем сумму траты: '{amount}'")
    @Nonnull
    public EditSpendingPage setAmount(String amount) {
        amountInput.val(amount);
        return this;
    }

    @Step("Устанавливаем валюту: '{currency}'")
    @Nonnull
    public EditSpendingPage setCurrency(CurrencyValues currency) {
        this.currency.click();
        $$("[role='listbox'] li.MuiMenuItem-gutters").find(text(currency.name())).click();
        return this;
    }

    @Step("Устанавливаем категорию: '{category}'")
    @Nonnull
    public EditSpendingPage setCategory(String category) {
        categoryInput.val(category);
        return this;
    }

    @Step("Устанавливаем дату траты: '{ddmmyyyy}'")
    @Nonnull
    public EditSpendingPage setDate(String ddmmyyyy) {
        String clean = ddmmyyyy.replaceAll("[^0-9]", ""); // оставить только цифры
        LocalDate ld = LocalDate.parse(clean, DateTimeFormatter.ofPattern("ddMMyyyy"));
        Date date = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
        calendar.selectDateInCalendar(date);
        return this;
    }

    @Step("Устанавливаем описание траты на: '{description}'")
    @Nonnull
    public EditSpendingPage setDescription(String description) {
        descriptionInput.val(description);
        return this;
    }
}
