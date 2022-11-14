import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;

public class UpdateUserDataAfterAuthorizationTest {
    private ApiClient client;
    private UserData userData;
    private String email;
    private String name;
    private String password;
    private  UserData responseRegistration;
    private UserData responseAuthorization;
    private String accessToken;

    @Before
    public void setUp() {
        client = new ApiClient();
        email = new Random().nextInt(500) + "@test.com";
        name = new Random().nextInt(500) + "Test1";
        password = "123BCa";
        userData = new UserData(email, password, name, null, null, null, null, null);
        responseRegistration = client.createRegistration(userData).then().extract().as(UserData.class);
        responseAuthorization = client.createAuthorization(userData)
                .then()
                .extract().as(UserData.class);
        accessToken = responseAuthorization.getAccessToken();
        final UserData userDataAfterAuthorization = client.getUserDataByAccessToken(accessToken)
                .then()
                .extract().as(UserData.class);
        name = userDataAfterAuthorization.getUser().getName();
        email = userDataAfterAuthorization.getUser().getEmail();
    }

    @After
    public void deleteUser() {
        if(responseRegistration.getAccessToken() != null) {
            String accessToken = responseRegistration.getAccessToken();
            client.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Обновление почты и имени пользователя для авторизованного пользователя")
    public void updateEmailAndNameOfUserTest() {
        String newEmail = new Random().nextInt() + email;
        String newName = new Random().nextInt() + name;
        userData = new UserData(newEmail, password, newName, null, null, null, null, null);
        final UserData updatedData = client.updateUserData(accessToken, userData)
                .then()
                .statusCode(200)
                .extract().as(UserData.class);
        Assert.assertEquals(newEmail, updatedData.getUser().getEmail());
        Assert.assertEquals(newName, updatedData.getUser().getName());
    }

    @Test
    @DisplayName("Обновление почты пользователя для авторизованного пользователя")
    public void updateEmailOfUserTest() {
        String newEmail = new Random().nextInt() + email;
        userData = new UserData(newEmail, password, name, null, null, null, null, null);
        final UserData responseOfUpdateEmail = client.updateUserData(accessToken, userData)
                .then()
                .statusCode(200)
                .extract().as(UserData.class);
        Assert.assertEquals(newEmail, responseOfUpdateEmail.getUser().getEmail());
    }

    @Test
    @DisplayName("Обновление имени пользователя для авторизованного пользователя")
    public void updateNameOfUserTest() {
        String newName = new Random().nextInt() + name;
        userData = new UserData(email, password, newName, null, null, null, null, null);
        final UserData responseOfUpdatedData = client.updateUserData(accessToken, userData)
                .then()
                .statusCode(200)
                .extract().as(UserData.class);
        Assert.assertEquals(newName, responseOfUpdatedData.getUser().getName());
    }

    @Test
    @DisplayName("Обновление почты невалидными данными")
    @Issue("BUG-1")
    public void updateEmailOfUserWithInvalidDataTest() {
        userData = new UserData(email, password, name, null, null, null, null, null);
        final UserData responseOfUpdatedData = client.updateUserData2(accessToken, userData)
                .then()
                .statusCode(403)
                .extract().as(UserData.class);
        Assert.assertEquals("User with such email already exists", responseOfUpdatedData.getMessage());
    }
}