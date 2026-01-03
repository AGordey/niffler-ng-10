package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.*;
import org.junit.jupiter.api.Test;

public class ProfileTest {
    private static final Config CFG = Config.getInstance();
    private final MainPage mainPage = new MainPage();
    private final ProfilePage profilePage = new ProfilePage();
    private LoginPage loginPage;


    @User(
            categories = @Category(
                    archived = false
            )
    )
    @Test
    void activeCategoryShouldPresentInCategoryList(UserJson user) {
        loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        loginPage.login(user.username(), user.testData().password());
        mainPage.goToProfilePage();
        profilePage.checkCategoryIsDisplayed(user.testData().categories().getFirst().name());

    }

    @User(
            categories = @Category(
                    archived = true
            )
    )
    @Test
    void archivedCategoryShouldNotBePresentedInActiveCategoryList(UserJson user) {
        loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        loginPage.login(user.username(), user.testData().password());
        mainPage.goToProfilePage();
        profilePage.showActiveAndArchivedCategoriesList();
        profilePage.checkArchiveCategoryIsDisplayed(user.testData().categories().getFirst().name());

    }

}
