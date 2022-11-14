import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class UpdateUserDataWithoutAuthorizationTest {
    private ApiClient client;
    private UserData responseRegistration;
    private String accessToken;

    @Before
    public void setUp() {
        client = new ApiClient();
    }

    @Test
    @DisplayName("Обновление данных пользователя без авторизации")
    @Issue("BUG-2")
    public void updateUserDataWithoutAuthorizationTest() {
        String email = new Random().nextInt(1000) + "@ya.ru";
        String name = "Anna";
        String password = "12345";
        final UserData userData = new UserData(email, password, name, null, null, null, null, null);
        responseRegistration = client.createRegistration(userData)
                .then()
                .extract().as(UserData.class);
        accessToken = responseRegistration.getAccessToken();
        final UserData userDataGotByToken = client.getUserDataByAccessToken(accessToken)
                .then()
                .extract().as(UserData.class);
        name = userDataGotByToken.getUser().getName();
        email = userDataGotByToken.getUser().getEmail();
        final UserData newUserData = new UserData(new Random().nextInt(500) + email, password, name + new Random().nextInt(50), null, null, null, null, null);
        final UserData responseUpdatedData = client.updateUserData(accessToken, newUserData)
                .then()
                .statusCode(401)
                .extract().as(UserData.class);
        Assert.assertFalse(responseUpdatedData.getSuccess());
        Assert.assertEquals("You should be authorised", responseUpdatedData.getMessage());
    }

    @After
    public void deleteUser() {
        if(responseRegistration != null){
            client.deleteUser(accessToken);
        }
    }
}