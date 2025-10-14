package guru.qa.niffler.util;

import com.github.javafaker.Faker;

public class DataUtil {
    private static final Faker faker = new Faker();

    public static String getRandomUserName() {
        return faker.name().username();
    }
    public static String getRandomPassword() {
        return faker.internet().password(5,15);
    }
    public static String getRandomCategory() {
                return faker.hipster().word();
    }

}
