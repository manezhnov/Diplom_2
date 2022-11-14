import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;

public class CreateUserTest {
    private String email;
    private String name;
    private String password;
    private ApiClient client;
    private UserData userData;
    private UserData responseRegistration;

    @Before
    public void setUp() {
        client = new ApiClient();
        email = new Random().nextInt(100) + "@test.com";
        name = new Random().nextInt(100) + "Test";
        password = "123Abc";
        userData = new UserData(email, password, name, null, null, null, null, null);
    }

    @After
    public void deleteUser() {
        if(responseRegistration.getAccessToken() != null) {
            String accessToken = responseRegistration.getAccessToken();
            client.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создать уникального пользователя")
    public void validCreateUserTest() {
        responseRegistration = client.createRegistration(userData)
                .then()
                .statusCode(200)
                .extract().as(UserData.class);
        Assert.assertNotNull(responseRegistration);
    }

    @Test
    @DisplayName("Создать пользователя, который уже зарегистрирован")
    public void createNotUniqueUserTest() {
        responseRegistration = client.createRegistration(userData).then().statusCode(200).extract().as(UserData.class);
        UserData responseRegistrationNotUniqueUser = client.createRegistration(userData)
                .then()
                .statusCode(403)
                .extract().as(UserData.class);
        Assert.assertEquals("User already exists", responseRegistrationNotUniqueUser.getMessage());
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить одно из обязательных полей: емейл")
    public void createUserWithEmptyEmailFieldTest() {
        userData = new UserData("", password, name, null, null, null, null, null);
        responseRegistration = client.createRegistration(userData)
                .then()
                .statusCode(403)
                .extract().as(UserData.class);
        Assert.assertEquals("Email, password and name are required fields", responseRegistration.getMessage());
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить одно из обязательных полей: пароль")
    public void createUserWithEmptyPasswordFieldTest() {
        userData = new UserData(email, "", name, null, null, null, null, null);
        responseRegistration = client.createRegistration(userData)
                .then()
                .statusCode(403)
                .extract().as(UserData.class);
        Assert.assertEquals("Email, password and name are required fields", responseRegistration.getMessage());
    }
}