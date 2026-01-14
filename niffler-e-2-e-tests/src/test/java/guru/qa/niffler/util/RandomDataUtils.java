package guru.qa.niffler.util;

import com.github.javafaker.Faker;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@ParametersAreNonnullByDefault
public class RandomDataUtils {

    private static final Faker faker = new Faker();

    private RandomDataUtils() {
    }
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

    @Nonnull
    public static String randomUsername() {
        return faker.name().username();
    }

    @Nonnull
    public static String randomName() {
        return faker.name().firstName();
    }

    @Nonnull
    public static String randomSurname() {
        return faker.name().lastName();
    }

    @Nonnull
    public static String randomCategoryName() {
        return faker.hipster().word();
    }

    @Nonnull
    public static String randomSentence(int wordsCount) {
        return faker.lorem().sentence(wordsCount);
    }

    public static int randomNumber() {
        return faker.number().numberBetween(1,1000000);
    }
    public static String randomDate() {
        // Генерируем случайную дату как Date
        Date startDate = Date.from(LocalDate.of(1970, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date randomDate = faker.date().between(startDate, endDate);

        // Преобразуем Date → LocalDate → строка в формате ddMMyyyy
        LocalDate localDate = randomDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDate.format(FORMATTER);
    }

}
