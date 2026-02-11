package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsTest {

    @Test
    @ApiLogin
    @User(friends = 1)
    @DisplayName("Должен отображаться список друзей")
    void friendShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .searchFriends(user.testData().friends().getFirst().username())
                .checkExistingFriends(user.testData().friends().getFirst().username());
    }

    @Test
    @ApiLogin
    @User()
    @DisplayName("Таблица друзей должна быть пустой")
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkNoExistingFriends();
    }

    @Test
    @ApiLogin
    @User(outcomeInvitations = 1)
    @DisplayName("Должен отображаться входящий запрос на добавление в друзья")
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .searchFriends(user.testData().outcomeInvitations().getFirst().username())
                .checkExistingInvitations(user.testData().outcomeInvitations().getFirst().username());
    }

    @Test
    @ApiLogin
    @User(incomeInvitations = 1)
    @DisplayName("Статус добавления в друзья должен быть в статусе Waiting...")
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(AllPeoplePage.URL, AllPeoplePage.class)
                .searchFriends(user.testData().incomeInvitations().getFirst().username())
                .checkWaitingOfUserInvitations(user.testData().incomeInvitations().getFirst().username());
    }

    @Test
    @ApiLogin
    @User(outcomeInvitations = 1)
    @DisplayName("Прием заявки в друзья")
    void acceptInvitationOfFriendship(UserJson user) {
        String nameOfUserWhoMadeIncomeInvitation = user.testData().outcomeInvitations().getFirst().username();

        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkExistingInvitations(nameOfUserWhoMadeIncomeInvitation)
                .acceptFriendship(nameOfUserWhoMadeIncomeInvitation)
                .checkSnackbarText("Invitation of " + nameOfUserWhoMadeIncomeInvitation + " accepted")
                .searchFriends(nameOfUserWhoMadeIncomeInvitation)
                .checkExistingFriends(nameOfUserWhoMadeIncomeInvitation);
    }

    @Test
    @ApiLogin
    @User(outcomeInvitations = 1)
    @DisplayName("Отклонение заявки в друзья")
    void declineInvitationOfFriendship(UserJson user) {
        String nameOfUserWhoMadeIncomeInvitation = user.testData().outcomeInvitations().getFirst().username();

        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkExistingInvitations(nameOfUserWhoMadeIncomeInvitation)
                .declineFriendship(nameOfUserWhoMadeIncomeInvitation)
                .checkSnackbarText("Invitation of " + nameOfUserWhoMadeIncomeInvitation + " is declined")
                .searchFriends(nameOfUserWhoMadeIncomeInvitation)
                .checkNoExistingFriends();
    }
}
