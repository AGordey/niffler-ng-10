package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
//import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {

    public static final String URL = CFG.authUrl() + "login";

    private final SelenideElement usernameInput;
    private final SelenideElement passwordInput;
    private final SelenideElement logInButton;
    private final SelenideElement logInWithPasskeyButton;
    private final SelenideElement registerButton;
    private final SelenideElement errorMessage;

    public LoginPage(SelenideDriver driver) {
        super(driver);
        this.usernameInput = driver.$("#username");
        this.passwordInput = driver.$("#password");
        this.logInButton = driver.$("#login-button");
        this.logInWithPasskeyButton = driver.$("#login-with-passkey-button");
        this.registerButton = driver.$("#register-button");
        this.errorMessage = driver.$(".form__error");
    }

    public LoginPage() {
        this.usernameInput = Selenide.$("#username");
        this.passwordInput = Selenide.$("#password");
        this.logInButton = Selenide.$("#login-button");
        this.logInWithPasskeyButton = Selenide.$("#login-with-passkey-button");
        this.registerButton = Selenide.$("#register-button");
        this.errorMessage = Selenide.$(".form__error");
    }

    @Nonnull
    @Step("Успешная авторизация и переход на главную страницу")
    public MainPage login(String username, String password) {
        usernameInput.val(username);
        passwordInput.val(password);
        logInButton.click();
        return new MainPage();
    }

    @Nonnull
    @Step("Неуспешная авторизация и переход на главную страницу")
    public LoginPage unsuccessfulLogin(String username, String password) {
        usernameInput.val(username);
        passwordInput.val(password);
        logInButton.click();
        return this;
    }

    @Nonnull
    @Step("Переход на страницу регистрации пользователя со страницы логина")
    public RegisterPage goToRegistrationPage() {
        registerButton.click();
        return new RegisterPage();
    }

    @SneakyThrows
    @Nonnull
    @Step("Проверка что страница логина загрузилась")
    public LoginPage checkLoginPageLoaded() {
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        logInButton.shouldBe(visible);
        logInWithPasskeyButton.shouldBe(visible);
        registerButton.shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Проверяем сообщение об ошибке, содержащее текст: '{text}'")
    public LoginPage checkErrorMessageWithText(String text) {
        errorMessage.shouldHave(text(text));
        return this;
    }

}
