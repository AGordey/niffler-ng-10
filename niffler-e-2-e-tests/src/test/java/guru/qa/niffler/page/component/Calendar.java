package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class Calendar {

    private final SelenideElement self = $(".MuiInputBase-adornedEnd");
    private final SelenideElement inputField = self.$("input[name='date']");
    private final SelenideElement calendarButton = self.$("input[name='date']").$("button");
    private final SelenideElement openDatesButton = self.$("button");
    private final ElementsCollection daysList = $$("button.MuiPickersDay-dayWithMargin");
    private final SelenideElement dateCalendar = $(".MuiDateCalendar-root");
    private final SelenideElement currentYearMonth = dateCalendar.find(".MuiPickersCalendarHeader-label");
    private final ElementsCollection yearList = $$(".MuiPickersYear-yearButton");
    private final SelenideElement previousMonthButton = dateCalendar.find("button[title='Previous month']");
    private final SelenideElement nextMonthButton = dateCalendar.find("button[title='Next month']");
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
    private final ElementsCollection dayCalendarList = dateCalendar.find(".MuiDayCalendar-monthContainer")
            .findAll("button");

    @Step("Выбираем '{date}' в компоненте календарь ")
    public Calendar selectDateInCalendar(Date date) {
        if (dateNotFromFuture(date)) {
            LocalDate inputDate = date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            openDatesButton.click();
            selectYearFromDate(inputDate);
            selectMonthFromDate(inputDate);
            selectDayFromDate(inputDate);
        }
        return this;
    }

    private boolean dateNotFromFuture(Date date) throws IllegalArgumentException {
        LocalDate inputDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate today = LocalDate.now();
        if (inputDate.isAfter(today)) {
            throw new IllegalArgumentException("Не поддерживается дата в будущем");
        }
        return true;
    }

    private void selectDayFromDate(LocalDate date) throws IllegalArgumentException {
        int day = date.getDayOfMonth();
        daysList.find(text(String.valueOf(day))).click();
    }

    private void selectMonthFromDate(LocalDate date) throws IllegalArgumentException {
        int month = date.getMonthValue();
        int currentMonth = YearMonth.parse(currentYearMonth.text(), formatter).getMonthValue();
        while (currentMonth != month) {
            if (currentMonth < month) {
                nextMonthButton.click();
                currentMonth++;
            } else {
                previousMonthButton.click();
                currentMonth--;
            }
        }
    }


    private void selectYearFromDate(LocalDate date) throws IllegalArgumentException {
        int year = date.getYear();
        if (year < 1970 || year > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("Не поддерживается такой год или год из будущего");
        }
        currentYearMonth.click();
        yearList.find(text(String.valueOf(year))).click();
    }

}
