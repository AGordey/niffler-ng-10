package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.SpendJson;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class SpendConditions {

    @Nonnull
    public static WebElementsCondition haveSpendingInOrder(List<SpendJson> expected) {
        return haveSpendingInOrder(expected.toArray(new SpendJson[0]));
    }

    @Nonnull
    public static WebElementsCondition haveSpendingInOrder(SpendJson... expectedSpends) {
        return new WebElementsCondition() {
            private final List<String> expectedData = getListOfSpendingsExpected(expectedSpends);

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (expectedData.isEmpty()) {
                    throw new IllegalArgumentException("No expected spendings given");
                }
                if (expectedData.size() != elements.size()) {
                    return rejected(
                            String.format("Size mismatch (expected: %d, actual: %d)", expectedData.size(), elements.size()),
                            elements
                    );
                }
                List<String> actualData = getListOfSpendingsActual(elements);
                if (!expectedData.equals(actualData)) {
                    return rejected(
                            String.format("Content mismatch (expected: %s, actual: %s)", expectedData, actualData),
                            actualData
                    );
                }
                return accepted();
            }

            @Override
            public String toString() {
                return "spendings" + expectedData;
            }
        };
    }
    private static List<String> getListOfSpendingsExpected(SpendJson... spendJsons) {
        List<String> result = new LinkedList<>();

        for (SpendJson spendJson : spendJsons) {
            String dateStr = formatDate(spendJson.spendDate());
            //Преобразование суммы из double в формат как она отображается на фронтенде из "55555.0" -> "55555"
            String amountStr = String.format(Locale.ENGLISH, "%.0f", spendJson.amount());

            String oneOfSpending = "Spending from " + dateStr +
                    " with category " + spendJson.category().name() +
                    " with amount " + amountStr + " " + spendJson.currency().currencySign +
                    " with description " + spendJson.description();
            result.add(oneOfSpending);
        }
        return result;
    }
    //Преобразование даты в формат как она отображается на фронтенде из "Sat Feb 07 15:46:26 MSK 2026" -> "Feb 07, 2026"
    private static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        return sdf.format(date);
    }

    private static List<String> getListOfSpendingsActual(List<WebElement> elements) {
        List<String> result = new LinkedList<>();
        for (WebElement element : elements) {
            List<WebElement> spending = element.findElements(By.cssSelector("td"));

            String oneOfSpending = "Spending from " + spending.get(4).getText() +
                    " with category " + spending.get(1).getText() +
                    " with amount " + spending.get(2).getText() +
                    " with description " + spending.get(3).getText();
            result.add(oneOfSpending);


        }
        return result;
    }

}
