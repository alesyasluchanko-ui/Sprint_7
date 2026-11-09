package client;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/";
    private static final String ORDERS_PATH = "/api/v1/orders";

    public static RequestSpecification getSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .build();
    }

    @Step("Создание заказа")
    public Response create(Order order) {
        return given()
                .spec(getSpec())
                .body(order)
                .when()
                .post(ORDERS_PATH);
    }

    @Step("Получение списка заказов")
    public Response getOrderList() {
        return given()
                .spec(getSpec())
                .when()
                .get(ORDERS_PATH);
    }
}
