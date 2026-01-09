package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Header {
    private final SelenideElement self = $("#root header");

    private final SelenideElement newSpendingButton = self.find("[href='/spending']");
    private final SelenideElement menuButton = self.find("button[aria-label='Menu']");
    private final SelenideElement mainPageButton = self.find("[href='/main']");
    private final ElementsCollection menuItems = self.$$("li");

    @Step("Переходим на станицу Friends")
    public FriendsPage toFriendsPage() {
        menuButton.click();
        menuItems.find(text("Friends")).click();
        return new FriendsPage();
    }
    @Step("Переходим на станицу All People")
    public AllPeoplePage toAllPeoplePage() {
        menuButton.click();
        menuItems.find(text("All People")).click();
        return new AllPeoplePage();
    }
    @Step("Переходим на станицу Profile")
    public ProfilePage toProfilePage() {
        menuButton.click();
        menuItems.find(text("Profile")).click();
        return new ProfilePage();
    }
    @Step("")
    public LoginPage toLoginPage() {
        menuButton.click();
        menuItems.find(text("Sign out")).click();
        return new LoginPage();
    }
    @Step("")
    public EditSpendingPage addSpendingPage() {
        newSpendingButton.click();
        return new EditSpendingPage();
    }
    @Step("")
    public MainPage toMainPage() {
        mainPageButton.click();
        return new MainPage();
    }
    @Step("")
    public void checkHeaderText() {
        self.$("h1").shouldHave(text("Niffler"));
    }
    @Step("")
    public void checkNewSpendingButton() {
        self.$("a.MuiButtonBase-root")
                .shouldHave(
                        attribute("href", "/spending"),
                        text("New spending")
                );
    }


}
