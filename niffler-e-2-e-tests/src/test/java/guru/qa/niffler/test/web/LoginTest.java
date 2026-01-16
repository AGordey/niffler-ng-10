package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.DisabledByIssue;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.util.RandomDataUtils.randomUsername;

@WebTest
public class LoginTest {

    @DisabledByIssue("2")
    @Test
    @DisplayName("Тест на регистрацию в зависимости от состояния проблемы указанной в аннотации")
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(randomUsername(), "12345")
                .checkThatPageLoaded();
    }
}