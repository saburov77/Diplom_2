package model;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static model.Constants.*;

public class OrderApi {

    @Step("Создание заказа")
    public Response CreateOrder(OrderData orderData, String accessToken){
        return given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .and()
                .body(orderData)
                .when()
                .post(CREATE_ORDER_URI);
    }

    @Step("Получение списка заказов пользователя")
    public Response GetOrdersOfUser(String accessToken){
        return given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .when()
                .get(CREATE_ORDER_URI);
    }
}
