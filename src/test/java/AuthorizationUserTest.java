import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;

public class AuthorizationUserTest {
    private ApiClient client;
    private UserData userData;
    private UserData responseRegistration;
    private UserData responseAuthorization;

    @Before
    public void setUp() {
        client = new ApiClient();
        String email = new Random().nextInt(100) + "@test.com";
        String name = new Random().nextInt(100) + "Test";
        String password = "123Abc";
        userData = new UserData(email, password, name, null, null, null, null, null);
        responseRegistration = client.createRegistration(userData).then().extract().as(UserData.class);
    }

    @After
    public void deleteUser() {
        if(responseRegistration.getAccessToken() != null) {
            String accessToken = responseRegistration.getAccessToken();
            client.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Авторизация под существующим пользователем")
    public void validAuthorization() {
        responseAuthorization = client.createAuthorization(userData)
                .then()
                .statusCode(200)
                .extract().as(UserData.class);
        Assert.assertTrue(responseAuthorization.getSuccess());
    }

    @Test
    @DisplayName("Авторизация с неверным логином и паролем")
    public void invalidLogInAndPasswordAuthorization() {
        final UserData invalidUserData = new UserData("test@test.ru", "123cba", null, null, null, null, null, null);
        responseAuthorization = client.createAuthorization(invalidUserData)
                .then()
                .statusCode(401)
                .extract().as(UserData.class);
        Assert.assertFalse(responseAuthorization.getSuccess());
        Assert.assertEquals("email or password are incorrect", responseAuthorization.getMessage());
    }
}