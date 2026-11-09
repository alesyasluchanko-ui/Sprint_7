package order;

import client.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrderListTest {

    private OrderClient orderClient;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверка через OrderClient, что в тело ответа возвращается список заказов")
    public void getOrderListReturnsOrders() {
        Response response = orderClient.getOrderList();

        response.then()
                .statusCode(SC_OK)
                .body("orders", notNullValue());
    }
}
