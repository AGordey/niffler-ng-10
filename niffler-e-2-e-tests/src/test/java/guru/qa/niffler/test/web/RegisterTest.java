package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@WebTest
public class RegisterTest {

    private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    void shouldRegisterNewUser() {
        String userName = randomUsername();
        String userPassword = "12345";
        driver.open(LoginPage.URL, LoginPage.class)
                .goToRegistrationPage()
                .fillAndSubmitRegistration(userName, userPassword, userPassword)
                .checkSuccessMessage()
                .sighInButtonToLoginPage()
                .checkLoginPageLoaded();
    }

    @Test
    @DisplayName("Проверка недопустимости регистрация нового пользователя с существующим именем")
    void shouldNotRegisterWithExistingUserName() {
        String userName = randomUsername();
        String userPassword = "12345";
        String userPasswordNew = userPassword + "123";

        driver.open(LoginPage.URL, LoginPage.class)
                .goToRegistrationPage()
                .fillAndSubmitRegistration(userName, userPassword, userPassword)
                .checkSuccessMessage()
                .sighInButtonToLoginPage()
                .checkLoginPageLoaded()
                .goToRegistrationPage()
                .fillAndSubmitRegistration(userName, userPasswordNew, userPasswordNew)
                .checkErrorMessageWithText("Username `" + userName + "` already exists");
    }

    @Test
    @DisplayName("Недопустимости регистрация нового пользователя если пароли не совпадают")
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String userName = randomUsername();
        String userPassword = "12345";
        String userPasswordNew = userPassword.concat("123");
        driver.open(LoginPage.URL, LoginPage.class)
                .goToRegistrationPage()
                .fillAndSubmitRegistration(userName, userPassword, userPasswordNew)
                .checkErrorMessageWithText("Passwords should be equal");
    }

    @Test
    @DisplayName("Отображение главной страницы после успешной авторизации")
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        String userName = randomUsername();
        String userPassword = "12345";
        driver.open(LoginPage.URL, LoginPage.class)
                .goToRegistrationPage()
                .fillAndSubmitRegistration(userName, userPassword, userPassword)
                .sighInButtonToLoginPage()
                .login(userName, userPassword)
                .checkThatPageLoaded();
    }

    @Test
    @DisplayName("Проверка отображения страницы логина после неуспешной авторизации")
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        String userName = randomUsername();
        String userPassword = "12345";
        String userPasswordIncorrect = userPassword.concat("123");

        driver.open(LoginPage.URL, LoginPage.class)
                .goToRegistrationPage()
                .fillAndSubmitRegistration(userName, userPassword, userPassword)
                .sighInButtonToLoginPage()
                .unsuccessfulLogin(userName, userPasswordIncorrect)
                .checkLoginPageLoaded()
                .checkErrorMessageWithText("Неверные учетные данные пользователя");

    }
}