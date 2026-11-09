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

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_CONFLICT;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {

    private CourierClient courierClient;
    private Courier courier;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        // Генерируем уникальный логин, чтобы тесты не падали при повторном запуске
        String uniqueLogin = "courier_" + System.currentTimeMillis();
        courier = new Courier(uniqueLogin, "Alesya123", "Sluchanko");
    }

    @After
    public void cleanUp() {

        try {
            if (courier.getLogin() != null && courier.getPassword() != null) {
                Response loginResponse = courierClient.login(new CourierCredentials(courier.getLogin(), courier.getPassword()));
                if (loginResponse.getStatusCode() == 200) {
                    Integer courierId = loginResponse.then().extract().path("id");
                    if (courierId != null) {
                        courierClient.delete(courierId);
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Проверка, что курьера можно создать, запрос возвращает код 201 и тело ok: true")
    public void courierCanBeCreated() {
        Response response = courierClient.create(courier);

        response.then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("Проверка, что при попытке создать пользователя с логином, который уже есть, возвращается ошибка 409 и точное сообщение")
    public void cannotCreateDuplicateCourier() {
        // Создаем первого курьера
        courierClient.create(courier).then().statusCode(SC_CREATED);

        // Пытаемся создать точно такого же курьера еще раз
        Response response = courierClient.create(courier);

        response.then()
                .statusCode(SC_CONFLICT)
                .body("message", containsString("Этот логин уже используется"));

    }

    @Test
    @DisplayName("Ошибка создания курьера без логина")
    @Description("Проверка, что если нет обязательного поля login, запрос возвращает ошибку 400 и точное сообщение")
    public void cannotCreateCourierWithoutLogin() {
        courier.setLogin(null);
        Response response = courierClient.create(courier);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ошибка создания курьера без пароля")
    @Description("Проверка, что если нет обязательного поля password, запрос возвращает ошибку 400 и точное сообщение")
    public void cannotCreateCourierWithoutPassword() {
        courier.setPassword(null);
        Response response = courierClient.create(courier);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}
