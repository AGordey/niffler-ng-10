package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage {
    private final SelenideElement
            avatarOfUser = $("#PersonIcon"),
            uploadNewPictureButton = $(withText("Upload new picture")),
            registerPasskeyButton = $("#:r10:"),
            saveChangesButton = $("#:r11:"),
            userNameField = $("#username"),
            nameOfUserNameField = $("#name"),
            categoryField = $("#category"),
            toggleShowArchived = $(".PrivateSwitchBase-input");


    private final ElementsCollection
            categories = $$(".MuiChip-filled.MuiChip-colorPrimary"),
            categoriesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");

}
