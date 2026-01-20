package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.util.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
public class ProfileTest {

    private final Header header = new Header();

    @User(
            categories = @Category(
                    archived = false
            )
    )
    @Test
    @DisplayName("Активная категория должна отображаться в списке активных категорий")
    void activeCategoryShouldPresentInCategoryList(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToProfilePage()
                .checkCategoryIsDisplayed(user.testData().categories().getFirst().name());

    }

    @User(
            categories = @Category(
                    archived = true
            )
    )
    @Test
    @DisplayName("Архивная категория не должна отображаться в списке активных категорий")
    void archivedCategoryShouldNotBePresentedInActiveCategoryList(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToProfilePage()
                .showActiveAndArchivedCategoriesList()
                .checkArchiveCategoryIsDisplayed(user.testData().categories().getFirst().name());

    }

    @User
    @Test
    @DisplayName("Тест на редактирование имени в профиле")
    void editingProfile(UserJson user) {
        String name = RandomDataUtils.randomName();
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToProfilePage()
                .setNewName(name)
                .pressSaveChangesBtn();
        header.toMainPage()
                .goToProfilePage()
                .checkNewName(name);
    }


    @User
    @Test
    @DisplayName("Тест на редактирование имени в профиле")
    void nameShouldBeEditedInProfile(UserJson user) {
        final String testUsername = RandomDataUtils.randomName();

        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded();

        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .setNewName(testUsername)
                .pressSaveChangesBtn()
                .checkSnackbarText("Profile successfully updated");
    }

}
