package courier;

import client.CourierClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Courier;
import model.CourierCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {

    private CourierClient courierClient;
    private Courier courier;
    private Integer courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        // Генерируем уникального курьера и сразу регистрируем его перед тестом авторизации
        String uniqueLogin = "courier_auth_" + System.currentTimeMillis();
        courier = new Courier(uniqueLogin, "Alesya123", "Sluchanko");
        courierClient.create(courier);
    }

    @After
    public void cleanUp() {
        // Удаляем созданного курьера, чтобы очистить базу данных
        if (courierId != null) {
            courierClient.delete(courierId);
        } else {
            // Если в тесте ID не сохранился, пробуем залогиниться и удалить
            try {
                courierId = courierClient.login(new CourierCredentials(courier.getLogin(), courier.getPassword()))
                        .then().extract().path("id");
                if (courierId != null) {
                    courierClient.delete(courierId);
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Test
    @DisplayName("Успешная авторизация курьера")
    @Description("Проверка, что курьер может авторизоваться при передаче правильных данных, запрос возвращает код 200 (OK) и id курьера")
    public void courierCanLogin() {
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        Response response = courierClient.login(credentials);

        response.then()
                .statusCode(SC_OK)
                .body("id", notNullValue());

        courierId = response.then().extract().path("id");
    }

    @Test
    @DisplayName("Ошибка авторизации с неправильным паролем")
    @Description("Проверка, что система возвращает ошибку 404 (Not Found) и точное сообщение, если указать неверный пароль")
    public void loginWithWrongPasswordReturnsError() {
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), "wrong_password");
        Response response = courierClient.login(credentials);

        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка авторизации под несуществующим пользователем")
    @Description("Проверка, что система возвращает ошибку 404 (Not Found) и точное сообщение при попытке авторизоваться под несуществующим логином")
    public void loginWithNonExistentCourierReturnsError() {
        CourierCredentials credentials = new CourierCredentials("non_existent_ninja_999", "pass123");
        Response response = courierClient.login(credentials);

        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка авторизации без указания логина")
    @Description("Проверка, что если не передать обязательное поле login, запрос возвращает ошибку 400 (Bad Request) и точное сообщение")
    public void loginWithoutLoginReturnsError() {
        // Передаем пустую строку вместо логина
        CourierCredentials credentials = new CourierCredentials("", courier.getPassword());
        Response response = courierClient.login(credentials);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка авторизации без указания пароля")
    @Description("Проверка, что если не передать обязательное поле password, запрос возвращает ошибку 400 (Bad Request) и точное сообщение")
    public void loginWithoutPasswordReturnsError() {

        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), "");
        Response response = courierClient.login(credentials);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}
