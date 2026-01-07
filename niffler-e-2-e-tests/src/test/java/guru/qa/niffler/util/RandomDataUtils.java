package guru.qa.niffler.util;

import com.github.javafaker.Faker;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RandomDataUtils {

    private static final Faker faker = new Faker();

    private RandomDataUtils() {
    }

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


}
