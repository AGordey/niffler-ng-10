package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
@WebTest
public class FriendsTest {

    @Test
    @User(friends = 1)
    @DisplayName("Должен отображаться список друзей")
    void friendShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .searchFriends(user.testData().friends().getFirst().username())
                .checkExistingFriends(user.testData().friends().getFirst().username());
    }

    @Test
    @User()
    @DisplayName("Таблица друзей должна быть пустой")
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .checkNoExistingFriends();
    }

    @Test
    @User(outcomeInvitations = 1)
    @DisplayName("Должен отображаться входящий запрос на добавление в друзья")
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .searchFriends(user.testData().outcomeInvitations().getFirst().username())
                .checkExistingInvitations(user.testData().outcomeInvitations().getFirst().username());
    }

    @Test
    @User(incomeInvitations = 1)
    @DisplayName("Статус добавления в друзья должен быть в статусе Waiting...")
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToAllPeoplePage()
                .searchFriends(user.testData().incomeInvitations().getFirst().username())
                .checkWaitingOfUserInvitations(user.testData().incomeInvitations().getFirst().username());
    }

    @Test
    @User(outcomeInvitations = 1)
    @DisplayName("Прием заявки в друзья")
    void acceptInvitationOfFriendship(UserJson user) {
        String nameOfUserWhoMadeIncomeInvitation = user.testData().outcomeInvitations().getFirst().username();

        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .checkExistingInvitations(nameOfUserWhoMadeIncomeInvitation)
                .acceptFriendship(nameOfUserWhoMadeIncomeInvitation)
                .checkSnackbarText("Invitation of " + nameOfUserWhoMadeIncomeInvitation + " accepted")
                .searchFriends(nameOfUserWhoMadeIncomeInvitation)
                .checkExistingFriends(nameOfUserWhoMadeIncomeInvitation);
    }

    @Test
    @User(outcomeInvitations = 1)
    @DisplayName("Отклонение заявки в друзья")
    void declineInvitationOfFriendship(UserJson user) {
        String nameOfUserWhoMadeIncomeInvitation = user.testData().outcomeInvitations().getFirst().username();

        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .checkExistingInvitations(nameOfUserWhoMadeIncomeInvitation)
                .declineFriendship(nameOfUserWhoMadeIncomeInvitation)
                .checkSnackbarText("Invitation of " + nameOfUserWhoMadeIncomeInvitation + " is declined")
                .searchFriends(nameOfUserWhoMadeIncomeInvitation)
                .checkNoExistingFriends();
    }
}
