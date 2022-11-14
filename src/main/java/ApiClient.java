import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ApiClient {
    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    public static final String CREATE_USER = "/api/auth/register";
    public static final String LOGIN_USER = "/api/auth/login";
    public static final String USER_DATA = "/api/auth/user";
    public static final String INGREDIENTS = "/api/ingredients";
    public static final String ORDERS = "/api/orders";
    private final Filter requestFilter = new RequestLoggingFilter();
    private final Filter responseFilter = new ResponseLoggingFilter();

    @Step("Объявлен метод создания пользователя, отправляющий запрос на сервер")
    public Response createRegistration(UserData registration) {
        return RestAssured.with()
                .filters(requestFilter, responseFilter)
                .baseUri(BASE_URL)
                .body(registration)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .post(CREATE_USER);
    }

    @Step("Логин под существующим пользователем")
    public Response createAuthorization(UserData registration) {
        return RestAssured.with()
                .filters(requestFilter, responseFilter)
                .baseUri(BASE_URL)
                .body(registration)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .post(LOGIN_USER);
    }

    @Step("Получение данных пользователя")
    public Response getUserDataByAccessToken(String accessToken) {
        return RestAssured.with()
                .filters(requestFilter, responseFilter)
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .get(USER_DATA);
    }

    @Step("Изменение данных пользователя")
    public Response updateUserData(String accessToken, UserData userData) {
        return RestAssured.with()
                .filters(requestFilter, responseFilter)
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .body(userData)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .patch(USER_DATA);
    }

    @Step("Изменение данных пользователя")
    public Response updateUserData2(String accessToken, UserData userData) {
        return RestAssured.with()
                .filters(requestFilter, responseFilter)
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .body(userData)
                .accept(ContentType.HTML)
                .contentType(ContentType.HTML)
                .patch(USER_DATA);
    }

    @Step("Удаление пользователя")
    public void deleteUser(String accessToken) {
        RestAssured.with()
                .filters(requestFilter, responseFilter)
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .delete(USER_DATA);
    }

    @Step("Получение данных об ингредиентах")
    public Response getIngredients() {
        return RestAssured.with()
                .filters(requestFilter, responseFilter)
                .baseUri(BASE_URL)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .get(INGREDIENTS);
    }

    @Step("Создание заказа")
    public Response createOrder(OrderData orderData) {
        return RestAssured.with()
                .filters(requestFilter, responseFilter)
                .baseUri(BASE_URL)
                .body(orderData)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .post(ORDERS);
    }

}