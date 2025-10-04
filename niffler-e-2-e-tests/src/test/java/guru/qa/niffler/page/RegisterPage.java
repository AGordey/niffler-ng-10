package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.util.DataUtil.getRandomUserName;

public class RegisterPage {
    public final SelenideElement usernameInput = $("#username"),
            passwordInput = $("#password"),
            passwordSubmitInput = $("#passwordSubmit"),
            sighUpButton = $("#register-button"),
            signInButton = $(".form_sign-in"), //Кнопка при успешной регистрации
            errorMessageUserAlreadyExist = $(".form__error"), //Username `User_1` already exists
            successMessage = $(".form__paragraph_success");

    @Step("Set user name ")
    public RegisterPage setUsername(String userName) {
        usernameInput.setValue(userName);
        return this;
    }

    @Step("Set password ")
    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Set password ")
    public RegisterPage setPasswordSubmit(String password) {
        passwordSubmitInput.setValue(password);
        return this;
    }

    @Step("Click sign up button ")
    public RegisterPage submitRegistration() {
        sighUpButton.click();
        return this;
    }
    @Step("Click sign in button after registration to Login page ")
    public RegisterPage sighInButtonToLoginPage() {
        signInButton.click();
        return this;
    }

    @Step ("Check Error message")
    public RegisterPage checkSuccessMessage(){
        successMessage.shouldBe(visible);
        return this;
    }
    @Step ("Check Error message")
    public RegisterPage checkErrorMessage(){
        errorMessageUserAlreadyExist.shouldBe(visible);
        return this;
    }


}
