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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateCourierTest {

    private CourierClient courierClient;
    private Courier courier;
    private Integer courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        // Генерируем уникальный логин, чтобы тесты не падали при повторном запуске
        String uniqueLogin = "courier_" + System.currentTimeMillis();
        courier = new Courier(uniqueLogin, "Alesya123", "Sluchanko");
    }

    @After
    public void cleanUp() {
        // Если курьер был создан и мы получили его ID, удаляем его из базы
        if (courierId != null) {
            courierClient.delete(courierId);
        }
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Проверка, что курьера можно создать, запрос возвращает код 201 и тело ok: true")
    public void courierCanBeCreated() {
        Response response = courierClient.create(courier);

        response.then()
                .statusCode(201)
                .body("ok", equalTo(true));


        Response loginResponse = courierClient.login(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        courierId = loginResponse.then().extract().path("id");
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("Проверка, что при попытке создать пользователя с логином, который уже есть, возвращается ошибка 409")
    public void cannotCreateDuplicateCourier() {
        // Создаем первого курьера
        courierClient.create(courier).then().statusCode(201);

        // Получаем его ID для очистки базы в @After
        courierId = courierClient.login(new CourierCredentials(courier.getLogin(), courier.getPassword()))
                .then().extract().path("id");

        // Пытаемся создать точно такого же курьера еще раз
        Response response = courierClient.create(courier);

        response.then()
                .statusCode(409)
                .body("message", notNullValue());
    }

    @Test
    @DisplayName("Ошибка создания курьера без логина")
    @Description("Проверка, что если нет обязательного поля login, запрос возвращает ошибку 400")
    public void cannotCreateCourierWithoutLogin() {
        courier.setLogin(null);
        Response response = courierClient.create(courier);

        response.then()
                .statusCode(400)
                .body("message", notNullValue());
    }

    @Test
    @DisplayName("Ошибка создания курьера без пароля")
    @Description("Проверка, что если нет обязательного поля password, запрос возвращает ошибку 400")
    public void cannotCreateCourierWithoutPassword() {
        courier.setPassword(null);
        Response response = courierClient.create(courier);

        response.then()
                .statusCode(400)
                .body("message", notNullValue());
    }
}
