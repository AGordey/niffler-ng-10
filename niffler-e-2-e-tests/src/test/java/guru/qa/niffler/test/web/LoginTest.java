package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.Driver;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.converter.Browser;
import guru.qa.niffler.jupiter.extension.NonStaticBrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

//@WebTest
public class LoginTest {

    @RegisterExtension
    private final NonStaticBrowserExtension browserExtension = new NonStaticBrowserExtension();

    @EnumSource(value = Browser.class, names = {"CHROME", "FIREFOX"})
    @ParameterizedTest
    @User
    @DisplayName("Тест на регистрацию в зависимости от состояния проблемы указанной в аннотации")
    void mainPageShouldBeDisplayedAfterSuccessLogin(@Driver SelenideDriver driver, UserJson user) {
        browserExtension.drivers().add(driver);
        driver.open(LoginPage.URL);
        new LoginPage(driver).login(user.username(), user.testData().password());
        new MainPage(driver).checkThatPageLoaded();
    }
}