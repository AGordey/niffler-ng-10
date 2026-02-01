package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension;
import guru.qa.niffler.util.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<ProfilePage> {

    public static String URL = CFG.frontUrl() + "profile";

    private final SelenideElement
            avatarOfUser = $("#PersonIcon"),
            avatarForScreenshotTest = $("#image__input").parent().$("img"),
            uploadNewPictureButton = $("#image__input"),
            registerPasskeyButton = $(byText("Register Passkey")),
            saveChangesButton = $("button[type='submit']"),
            userNameField = $("#username"),
            nameOfUserNameField = $("#name"),
            categoryField = $("#category"),
            toggleShowArchived = $(".MuiSwitch-input"),
            makeCategoryArchiveButton = $("[aria-label=Archive category]"),
            confirmButtonToMakeCategoryArchive = $$("[type=button]").findBy(text("Archive")),
            EditCategoryButton = $("[aria-label=Edit category]");

    //Коллекции из списка не архивных и архивных категорий
    private final ElementsCollection
            categories = $$(".MuiChip-filled.MuiChip-colorPrimary"),
            categoriesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");

    @Nonnull
    @Step("Проверяем наличие элементов на странице профиля")
    public ProfilePage checkProfilePageLoaded() {
        avatarOfUser.shouldBe(visible);
        uploadNewPictureButton.shouldBe(visible);
        registerPasskeyButton.shouldBe(visible);
        saveChangesButton.shouldBe(visible);
        userNameField.shouldBe(visible);
        nameOfUserNameField.shouldBe(visible);
        categoryField.shouldBe(visible);
        toggleShowArchived.shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Добавляем новую неархивную категорию: '{category}'")
    public ProfilePage addNewCategory(String category) {
        categoryField.setValue(category).pressEnter();
        return this;
    }

    @Nonnull
    @Step("Устанавливаем новое имя пользователя: '{name}'")
    public ProfilePage setNewName(String name) {
        nameOfUserNameField.setValue(name);
        return this;
    }

    @Nonnull
    @Step("Проверяем, что имя пользователя установлено как: '{name}'")
    public ProfilePage checkNewName(String name) {
        nameOfUserNameField.shouldHave(value(name));
        return this;
    }

    @Nonnull
    @Step("Нажимаем кнопку 'Сохранить изменения'")
    public ProfilePage pressSaveChangesBtn() {
        saveChangesButton.click();
        return this;
    }

    @Nonnull
    @Step("Проверяем, что категория отображается в списке: '{category}'")
    public ProfilePage checkCategoryIsDisplayed(String category) {
        categories.find(text(category)).shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Добавляем новую архивную категорию: '{category}'")
    public ProfilePage addNewArchiveCategory(String category) {
        categoryField.setValue(category).pressEnter();
        $(byText(category)).parent().parent().$("[aria-label='Archive category']").click();
        $(byText("Archive category")).parent().$(byText("Archive")).click();
        return this;
    }
    @Nonnull
    @Step("Делаем категорию '{category}' архивной")
    public ProfilePage makeCategoryArchive(String category) {
        $(byText(category)).parent().parent().$("[aria-label='Archive category']").click();
        $(byText("Archive category")).parent().$(byText("Archive")).click();
        return this;
    }

    @Nonnull
    @Step("Проверяем, что архивная категория отображается в списке: '{category}'")
    public ProfilePage checkArchiveCategoryIsDisplayed(String category) {
        categoriesArchived.find(text(category)).shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Отображаем как активные, так и архивные категории")
    public ProfilePage showActiveAndArchivedCategoriesList() {
        toggleShowArchived.click();
        return this;
    }

    @Nonnull
    @Step("Устанавливаем новый аватар")
    public ProfilePage setNewAvatar(String path) {
        uploadNewPictureButton.uploadFromClasspath(path);
        saveChangesButton.click();
        return this;
    }
    @Step("Проверяем скриншот аватара в профиле")
    @Nonnull
    public ProfilePage checkAvatar(BufferedImage expected) throws IOException {
        Selenide.sleep(1000);
        BufferedImage actualImage = ImageIO.read(Objects.requireNonNull(avatarForScreenshotTest.screenshot()));
        assertFalse(
                new ScreenDiffResult(
                        actualImage, expected
                ),
                ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE
        );
        return this;
    }


}