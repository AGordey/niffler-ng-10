package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

@WebTest
public class ProfileTest {

    private final Header header = new Header();

    @User(
            categories = @Category(
                    archived = false
            )
    )
    @Test
    @ApiLogin
    @DisplayName("Активная категория должна отображаться в списке активных категорий")
    void activeCategoryShouldPresentInCategoryList(UserJson user) {
        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .checkCategoryIsDisplayed(user.testData().categories().getFirst().name());

    }

    @User(
            categories = @Category(
                    archived = true
            )
    )
    @Test
    @ApiLogin
    @DisplayName("Архивная категория не должна отображаться в списке активных категорий")
    void archivedCategoryShouldNotBePresentedInActiveCategoryList(UserJson user) {

        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .showActiveAndArchivedCategoriesList()
                .checkArchiveCategoryIsDisplayed(user.testData().categories().getFirst().name());

    }

    @User
    @Test
    @ApiLogin
    @DisplayName("Тест на редактирование имени в профиле")
    void editingProfile(UserJson user) {
        String name = RandomDataUtils.randomName();

        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .setNewName(name)
                .pressSaveChangesBtn();
        header.toMainPage()
                .goToProfilePage()
                .checkNewName(name);
    }

    @User
    @Test
    @ApiLogin
    @DisplayName("Тест на редактирование имени в профиле")
    void nameShouldBeEditedInProfile(UserJson user) {
        final String testUsername = RandomDataUtils.randomName();

        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .setNewName(testUsername)
                .pressSaveChangesBtn()
                .checkSnackbarText("Profile successfully updated");
    }

    @User
    //@Test Не требуется, поскольку мы уже в @ScreenShotTest ее включили
    @ApiLogin
    @DisplayName("Скриншот тест на установку аватара")
    @ScreenShotTest("img/avatar/expected-avatar.png")
    void newAvatarInProfile(UserJson user, BufferedImage expected) throws IOException {

        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .setNewAvatar("img/avatar/new-avatar.png")
                .checkSnackbarText("Profile successfully updated")
                .checkAvatar(expected);
    }

}
