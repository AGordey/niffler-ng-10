package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.util.RandomDataUtils;
import org.junit.jupiter.api.Test;

public class ProfileTest {
    private static final Config CFG = Config.getInstance();
    private final MainPage mainPage = new MainPage();
    private final ProfilePage profilePage = new ProfilePage();
    private final Header header = new Header();
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

    @User
    @Test
    void editingProfile(UserJson user) {
        String name = RandomDataUtils.randomName();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToProfilePage();
        profilePage.setNewName(name)
                .pressSaveChangesBtn();
        header.toMainPage();
        mainPage.goToProfilePage();
        profilePage.checkNewName(name);
    }


    @User()
    @Test
    void nameShouldBeEditedInProfile(UserJson user) {
        final String testUsername = RandomDataUtils.randomName();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded();

        Selenide.open(CFG.frontUrl() + "profile", ProfilePage.class)
                .setNewName(testUsername)
                .pressSaveChangesBtn()
                .checkSnackbarText("Profile successfully updated");
    }

}
