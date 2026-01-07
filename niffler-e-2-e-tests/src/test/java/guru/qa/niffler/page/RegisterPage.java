package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class RegisterPage {
    public final SelenideElement
            usernameInput = $("#username"),
            passwordInput = $("#password"),
            passwordSubmitInput = $("#passwordSubmit"),
            sighUpButton = $("#register-button").as("Кнопка перехода на главную страницу"),
            signInButton = $(".form_sign-in").as("Кнопка при успешной регистрации"),
            errorMessage = $(".form__error"),
            successMessage = $(".form__paragraph_success");

    @Nonnull
    @Step("Create a new user ")
    public RegisterPage fillAndSubmitRegistration(String userName, String password, String confirmPassword) {
        usernameInput.setValue(userName);
        passwordInput.setValue(password);
        passwordSubmitInput.setValue(confirmPassword);
        sighUpButton.click();
        return this;
    }

    @Nonnull
    @Step("Set user name '{userName}' ")
    public RegisterPage setUsername(String userName) {
        usernameInput.setValue(userName);
        return this;
    }

    @Nonnull
    @Step("Set password '{password}'")
    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Nonnull
    @Step("Set password '{password}'")
    public RegisterPage setPasswordSubmit(String password) {
        passwordSubmitInput.setValue(password);
        return this;
    }

    @Nonnull
    @Step("Click sign up button ")
    public RegisterPage submitRegistration() {
        sighUpButton.click();
        return this;
    }

    @Nonnull
    @Step("Click sign in button after registration to Login page ")
    public LoginPage sighInButtonToLoginPage() {
        signInButton.click();
        return new LoginPage();
    }

    @Nonnull
    @Step("Check success message")
    public RegisterPage checkSuccessMessage() {
        successMessage.shouldHave(text("Congratulations! You've registered!"));
        return this;
    }

    @Nonnull
    @Step("Check Error message")
    public RegisterPage checkErrorMessage(String message) {
        errorMessage.shouldHave(text(message));
        return this;
    }

    @Nonnull
    @Step("Check Error message")
    public RegisterPage checkErrorMessageWithText(String text) {
        errorMessage.shouldHave(text(text));
        return this;
    }

}
