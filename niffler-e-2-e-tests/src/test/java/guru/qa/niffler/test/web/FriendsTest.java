package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.Driver;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.converter.Browser;
import guru.qa.niffler.jupiter.extension.NonStaticBrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@WebTest
public class FriendsTest {

    @RegisterExtension
    private final NonStaticBrowserExtension browserExtension = new NonStaticBrowserExtension();

    @EnumSource(value = Browser.class, names = {"CHROME", "FIREFOX"})
    @ParameterizedTest
    @User(friends = 1)
    @DisplayName("Должен отображаться список друзей")
    void friendShouldBePresentInFriendsTable(@Driver SelenideDriver driver, UserJson user) {
        browserExtension.drivers().add(driver);
        driver.open(LoginPage.URL);
        new LoginPage(driver).login(user.username(), user.testData().password());
        new MainPage(driver).goToFriendsPage();
        new FriendsPage(driver).searchFriends(user.testData().friends().getFirst().username())
                .checkExistingFriends(user.testData().friends().getFirst().username());
    }

    @EnumSource(value = Browser.class, names = {"CHROME", "FIREFOX"})
    @ParameterizedTest
    @User()
    @DisplayName("Таблица друзей должна быть пустой")
    void friendsTableShouldBeEmptyForNewUser(@Driver SelenideDriver driver, UserJson user) {
        driver.open(LoginPage.URL);
        new LoginPage(driver).login(user.username(), user.testData().password());
        new MainPage(driver).goToFriendsPage();
        new FriendsPage(driver).checkNoExistingFriends();
    }

    @EnumSource(value = Browser.class, names = {"CHROME", "FIREFOX"})
    @ParameterizedTest
    @User(outcomeInvitations = 1)
    @DisplayName("Должен отображаться входящий запрос на добавление в друзья")
    void incomeInvitationBePresentInFriendsTable(@Driver SelenideDriver driver, UserJson user) {
        driver.open(LoginPage.URL);
        new LoginPage(driver).login(user.username(), user.testData().password());
        new MainPage(driver).goToFriendsPage();
        new FriendsPage(driver).searchFriends(user.testData().outcomeInvitations().getFirst().username())
                .checkExistingInvitations(user.testData().outcomeInvitations().getFirst().username());
    }

    @EnumSource(value = Browser.class, names = {"CHROME", "FIREFOX"})
    @ParameterizedTest
    @User(incomeInvitations = 1)
    @DisplayName("Статус добавления в друзья должен быть в статусе Waiting...")
    void outcomeInvitationBePresentInAllPeoplesTable(@Driver SelenideDriver driver, UserJson user) {
        driver.open(LoginPage.URL);
        new LoginPage(driver).login(user.username(), user.testData().password());
        new MainPage(driver).goToAllPeoplePage();
        new FriendsPage(driver).searchFriends(user.testData().incomeInvitations().getFirst().username());
        new AllPeoplePage(driver).checkWaitingOfUserInvitations(user.testData().incomeInvitations().getFirst().username());
    }

    @EnumSource(value = Browser.class, names = {"CHROME", "FIREFOX"})
    @ParameterizedTest
    @User(outcomeInvitations = 1)
    @DisplayName("Прием заявки в друзья")
    void acceptInvitationOfFriendship(@Driver SelenideDriver driver, UserJson user) {
        String nameOfUserWhoMadeIncomeInvitation = user.testData().outcomeInvitations().getFirst().username();

        driver.open(LoginPage.URL);
        new LoginPage(driver).login(user.username(), user.testData().password());
        new MainPage(driver).goToFriendsPage();
        new FriendsPage(driver).checkExistingInvitations(nameOfUserWhoMadeIncomeInvitation)
                .acceptFriendship(nameOfUserWhoMadeIncomeInvitation)
                .checkSnackbarText("Invitation of " + nameOfUserWhoMadeIncomeInvitation + " accepted")
                .searchFriends(nameOfUserWhoMadeIncomeInvitation)
                .checkExistingFriends(nameOfUserWhoMadeIncomeInvitation);
    }

    @EnumSource(value = Browser.class, names = {"CHROME", "FIREFOX"})
    @ParameterizedTest
    @User(outcomeInvitations = 1)
    @DisplayName("Отклонение заявки в друзья")
    void declineInvitationOfFriendship(@Driver SelenideDriver driver, UserJson user) {
        String nameOfUserWhoMadeIncomeInvitation = user.testData().outcomeInvitations().getFirst().username();

        driver.open(LoginPage.URL);
        new LoginPage(driver).login(user.username(), user.testData().password());
        new MainPage(driver).goToFriendsPage();
        new FriendsPage(driver).checkExistingInvitations(nameOfUserWhoMadeIncomeInvitation)
                .declineFriendship(nameOfUserWhoMadeIncomeInvitation)
                .checkSnackbarText("Invitation of " + nameOfUserWhoMadeIncomeInvitation + " is declined")
                .searchFriends(nameOfUserWhoMadeIncomeInvitation)
                .checkNoExistingFriends();
    }
}
