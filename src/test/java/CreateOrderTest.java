import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.Random;

public class CreateOrderTest {
    private ApiClient client;
    private UserData userData;
    private  UserData responseRegistration;

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
    @DisplayName("Создать заказ. Пользователь авторизован. Ингредиенты выбраны")
    public void createOrderWithValidIngredientsTest() {
        client.createAuthorization(userData);
        OrderData orderData = client.getIngredients()
                .then()
                .extract().as(OrderData.class);
        OrderData chosenIngredients = new OrderData(List.of(orderData.getData().get(0).get_id(), orderData.getData().get(1).get_id()), null, null, null, null, null, List.of());
        OrderData responseOfCreateOrder = client.createOrder(chosenIngredients)
                .then()
                .statusCode(200)
                .extract().as(OrderData.class);
        Assert.assertTrue(responseOfCreateOrder.getSuccess());
    }

    @Test
    @DisplayName("Создать заказ. Пользователь авторизован. Ингредиент с неверным хешем")
    @Issue("BUG-4")
    public void createOrderWithInvalidIngredientTest() {
        client.createAuthorization(userData);
        OrderData orderData = client.getIngredients()
                .then()
                .extract().as(OrderData.class);
        OrderData ingredientsOfNewOrder = new OrderData(List.of(orderData.getData().get(0).get_id().replace("61c0c5a71d1f82001bdaaa6d", "Myid")), null, null, null, null, null, List.of());
        client.createOrder(ingredientsOfNewOrder)
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("Создать заказ. Пользователь авторизован. Ингредиенты не выбраны")
    public void createOrderWithoutIngredientsTest() {
        client.createRegistration(userData);
        client.createAuthorization(userData);
        OrderData orderData = new OrderData(List.of(), null, null, null, null, null, List.of());
        OrderData responseOfCreateOrder = client.createOrder(orderData)
                .then()
                .statusCode(400)
                .extract().as(OrderData.class);
        Assert.assertFalse(responseOfCreateOrder.getSuccess());
        Assert.assertEquals("Ingredient ids must be provided", responseOfCreateOrder.getMessage());
    }

    @Test
    @DisplayName("Создать заказ. Пользователь не авторизован. Ингредиенты выбраны")
    @Issue("BUG-3")
    public void createOrderWithoutAuthorizationTest() {
        OrderData orderData = client.getIngredients()
                .then()
                .extract().as(OrderData.class);
        OrderData chosenIngredients = new OrderData(List.of(orderData.getData().get(0).get_id(), orderData.getData().get(1).get_id()), null, null, null, null, null, List.of());
        OrderData responseOfCreateOrder = client.createOrder(chosenIngredients)
                .then()
                .statusCode(401)
                .extract().as(OrderData.class);
        Assert.assertFalse(responseOfCreateOrder.getSuccess());
    }
}