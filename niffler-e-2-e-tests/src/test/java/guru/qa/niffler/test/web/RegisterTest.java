package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegisterTest {
    private static final Config CFG = Config.getInstance();
    private LoginPage loginPage;

    @BeforeEach
    void setUp() {
        loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
    }

    @Test
    void shouldRegisterNewUser() {
        //Открываем страницу регистрации
        //setUserName
         //set Password
         //Confirm Password
        //click SIgnUpButton
        // check "Congratulations! You've registered!"
        //click signInButton
        //
    }

    @Test
    void shouldNotRegisterWithExistingUserName() {

    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {

    }

    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin() {

    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {

    }
}
