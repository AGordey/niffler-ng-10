package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

@WebTest
public class LoginTest {

    @RegisterExtension
    private final BrowserExtension browserExtension = new BrowserExtension();
    private final SelenideDriver chrome = new SelenideDriver(SelenideUtils.chromeConfig);
    private final SelenideDriver firefox = new SelenideDriver(SelenideUtils.firefoxConfig);
    //    @DisabledByIssue("2")
    @Test
    @DisplayName("Тест на регистрацию в зависимости от состояния проблемы указанной в аннотации")
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        browserExtension.drivers().addAll(List.of(chrome, firefox));
        chrome.open(LoginPage.URL);
        firefox.open(LoginPage.URL);
        new LoginPage(chrome).login("ginny.kertzmann", "12345");
        new MainPage(chrome).checkThatPageLoaded();
        new LoginPage(firefox).login("ginny.kertzmann1", "12345");
        new MainPage(firefox).checkThatPageLoaded();
    }
}