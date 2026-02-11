package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.model.*;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.AuthApiClient;
import guru.qa.niffler.service.SpendApiClient;
import guru.qa.niffler.service.UserApiClient;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);
    private static final Config CFG = Config.getInstance();
    private final AuthApiClient authApiClient = new AuthApiClient();
    private final SpendApiClient spendApiClient = new SpendApiClient();
    private final UserApiClient usersApiClient = new UserApiClient();

    private final boolean setupBrowser; //Поле, что бы запускать или не запускать браузер

    private ApiLoginExtension(boolean setupBrowser) {
        this.setupBrowser = setupBrowser;
    }

    public ApiLoginExtension() {
        this.setupBrowser = true;
    }

    public static ApiLoginExtension restApiLoginExtension() {
        return new ApiLoginExtension(false);
    }

    public static String getToken() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("token", String.class);
    }

    public static void setToken(String token) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("token", token);
    }

    public static String getCode() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("code", String.class);
    }

    public static void setCode(String code) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("code", code);
    }

    public static Cookie getJsessionIdCookie() {
        return new Cookie(
                "JSESSIONID",
                ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
        );
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(apiLogin -> {  // Если нашлась аннотация, то выполняем
                    final UserJson userToLogin; // Создаем, для хранение кредов которыми будем логиниться
                    final Optional<UserJson> userFromUserExtension = UserExtension.createdUser(); // Создаем на случай если есть @User
                    if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())) { //Проверяем, что если не указаны в @ApiLogin креды, то берем из @User
                        if (userFromUserExtension.isEmpty()) {
                            throw new IllegalStateException("@User must be present in case that @ApiLogin is empty!");
                        }
                        userToLogin = userFromUserExtension.get(); // Присваиваем данные в виде UserJson созданные в @User
                    } else { //Здесь, если это существующий юзер, т.е. в @ApiLogin указаны креды его и недолжно быть @User
                        UserJson user = getUser(apiLogin); //user - т.к. мы не генерируем нового, а указали существующего, добавляем инфо о друзьях, расходах,категориях

                        if (userFromUserExtension.isPresent()) {  //Обработка на случай, если мы генерируем юзера через @User, при указанных данных в @ApiLogin
                            throw new IllegalStateException("@User must not be present in case that @ApiLogin contains username or password!");
                        }
                        UserExtension.setUser(user); // устанавливаем данные для логина в расширение https://youtu.be/Re0ZVj0aQOA?t=3070 т.к. он может понадобиться в методе parametrResolver @User
                        userToLogin = user; //Присваиваем данные в виде UserJson созданные в @ApiLogin
                    }

                    final String token = authApiClient.login(   //Получаем токен авторизации по указанным в аннотации ApiLogin логину и паролю
                            userToLogin.username(),               //username из UserJson
                            userToLogin.testData().password()     //password из UserJson
                    );
                    setToken(token); //Устанавливаем полученный токен в ExtentionContext
                    if (setupBrowser) { // Запускаем браузер в случае веб тестов
                        Selenide.open(CFG.frontUrl());
                        Selenide.localStorage().setItem("id_token", getToken()); //Вставляем в localStorage поле id_token значение авторизационного токена
                        WebDriverRunner.getWebDriver().manage().addCookie( // Устанавливаем Cookie
                                new Cookie(
                                        "JSESSIONID",
                                        ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
                                )
                        );
                        Selenide.open(MainPage.URL, MainPage.class).checkThatPageLoaded();
                    }
                });
    }

    private List<SpendJson> getAllSpendingsOfUser(String username) {
        return spendApiClient.getAllSpends(username, null, null, null);
    }

    private List<CategoryJson> getAllCategoriesOfUser(String username) {
        return spendApiClient.getAllCategoriesByUsername(username);
    }

    private TestData getAllFriendsAndInvitesOfUser(ApiLogin apiLogin) {
        List<UserJson> allFriendsAndInvitations = usersApiClient.getFriends(apiLogin.username());
        List<UserJson> friends = allFriendsAndInvitations.stream().filter(allFriends ->
                allFriends.friendshipStatus() == FriendshipStatus.FRIEND).toList();
        List<UserJson> incomeInvitations = allFriendsAndInvitations.stream().filter(incomeInvitation ->
                incomeInvitation.friendshipStatus() == FriendshipStatus.INVITE_RECEIVED).toList();
        List<UserJson> outcomeInvitations = allFriendsAndInvitations.stream().filter(outcomeInvitation ->
                outcomeInvitation.friendshipStatus() == FriendshipStatus.INVITE_SENT).toList();
        return new TestData(apiLogin.password(), friends, incomeInvitations, outcomeInvitations);
    }

    private @NonNull UserJson getUser(ApiLogin apiLogin) {
        TestData testData = getAllFriendsAndInvitesOfUser(apiLogin);
        testData.addSpendings(getAllSpendingsOfUser(apiLogin.username()));
        testData.addCategories(getAllCategoriesOfUser(apiLogin.username()));
        return new UserJson(apiLogin.username(), testData);
    }

    @Override //Проверяем что тип String для входящего параметра и аннотация Token т.е void test(@Token String token)
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(String.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
    }

    @Override  // Если условия в supportsParameter выполняются, то выполнится этот метод
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getToken();
    }
}
