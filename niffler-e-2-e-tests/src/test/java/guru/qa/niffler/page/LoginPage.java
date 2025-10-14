package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
  private final SelenideElement usernameInput = $("#username");
  private final SelenideElement passwordInput = $("#password");
  private final SelenideElement submitBtn = $("#login-button");
  private final SelenideElement registerButton = $("#register-button");
  private final SelenideElement errorMessage = $(".form__error");

  public MainPage login(String username, String password) {
    usernameInput.val(username);
    passwordInput.val(password);
    submitBtn.click();
    return new MainPage();
  }

  @Step("Go To Registration Page From Login Page")
  public RegisterPage goToRegistrationPage() {
    registerButton.click();
    return new RegisterPage();
  }
}
