package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.Bubble;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class StatConditions {

    @Nonnull
    public static WebElementCondition statBubble(Bubble expected) {
        String colorExpected = expected.color().rgb;
        String textExpected = expected.text();
        return new WebElementCondition("Text and color of Bubble " + textExpected + " " + colorExpected) {
            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                final String rgbaOfBubble = element.getCssValue("background-color");
                final String textOfBubble = element.getText();
                return new CheckResult(
                        colorExpected.equals(rgbaOfBubble) && textExpected.equals(textOfBubble),
                        rgbaOfBubble + " - '" + textOfBubble + "'"
                );
            }
        };
    }

    @Nonnull
    public static WebElementsCondition statBubble(@Nonnull Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final Map<String, String> expectedData = bubblesToMap(expectedBubbles);

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (expectedData.isEmpty()) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (expectedData.size() != elements.size()) {
                    return rejected(
                            String.format("Size mismatch (expected: %d, actual: %d)", expectedData.size(), elements.size()),
                            webElementsToSet(elements)
                    );
                }
                Map<String, String> actualData = webElementsToMap(elements);
                //добавляю метод .toString() т.к. он преобразует в строку с учетом порядка
                //и метод equals сравнит строки с учетом порядка
                if (!expectedData.toString().equals(actualData.toString())) {
                    Map<String, String> actualDataDiff = new LinkedHashMap<>(actualData);
                    for (Map.Entry<String, String> expected : expectedData.entrySet()) {
                        actualDataDiff.remove(expected.getKey(),expected.getValue());
                    }
                    return rejected(
                            String.format("Content mismatch (expected: %s, actual: %s, mismatch in: %s)", expectedData, actualData, actualDataDiff),
                            actualData
                    );
                }
                return accepted();
            }

            @Override
            public String toString() {
                return "statBubble" + expectedData;
            }
        };
    }

    @Nonnull
    public static WebElementsCondition statBubbleInAnyOrder(@Nonnull Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final  Set<String> expectedData = bubblesToSet(expectedBubbles);

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (expectedData.isEmpty()) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (expectedData.size() != elements.size()) {
                    return rejected(
                            String.format("Size mismatch (expected: %d, actual: %d)", expectedData.size(), elements.size()),
                            webElementsToSet(elements)
                    );
                }
                Set<String> actualData = webElementsToSet(elements);
                if (!expectedData.equals(actualData)) {
                    Set<String> actualDataDiff = Set.copyOf(actualData);;
                    for (String expected : expectedData) {
                        actualDataDiff.remove(expected);
                    }
                    return rejected(
                            String.format("Content mismatch (expected: %s, actual: %s, mismatch in: %s)", expectedData, actualData, actualDataDiff),
                            actualData
                    );
                }
                return accepted();
            }

            @Override
            public String toString() {
                return "statBubble" + expectedData;
            }
        };
    }


    @Nonnull
    public static WebElementsCondition statBubblesContains(@Nonnull Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final  Set<String> expectedData = bubblesToSet(expectedBubbles);

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (expectedData.isEmpty()) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                Set<String> actualData = webElementsToSet(elements);
                if (!actualData.containsAll(expectedData)) {
                    return rejected(
                            String.format("Content not contain (expected: %s, actual: %s)", expectedData, actualData),
                            actualData
                    );
                }
                return accepted();
            }

            @Override
            public String toString() {
                return "statBubble" + expectedData;
            }
        };
    }

    private static Map<String, String> bubblesToMap(Bubble... expected) {
        Map<String, String> result = new LinkedHashMap<>();
        for (Bubble bubble : expected) {
            result.put(bubble.color().rgb, bubble.text());
        }
        return result;
    }

    private static Map<String, String> webElementsToMap(List<WebElement> elements) {
        Map<String, String> result = new LinkedHashMap<>();
        for (WebElement element : elements) {
            result.put(element.getCssValue("background-color"), element.getText());
        }
        return result;
    }

    private static Set<String> bubblesToSet(Bubble... expected) {
        Set<String> result = new HashSet<>();
        for (Bubble bubble : expected) {
            result.add(bubble.color().rgb + "=" + bubble.text());
        }
        return result;
    }

    private static Set<String> webElementsToSet(List<WebElement> elements) {
        Set<String> result = new HashSet<>();
        for (WebElement element : elements) {
            result.add(element.getCssValue("background-color") + "=" + element.getText());
        }
        return result;
    }

}
