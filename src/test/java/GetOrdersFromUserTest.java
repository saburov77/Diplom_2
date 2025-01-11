import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static model.Constants.BASE_URI;
import static org.hamcrest.CoreMatchers.is;

public class GetOrdersFromUserTest {
    protected String email;
    protected String password;
    protected String name;
    protected String accessToken;
    protected UserApi userApi;
    protected OrderApi orderApi;
    protected List<String> ingredientsImmortal;
    protected List<String> ingredientsAlfa;


    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        userApi = new UserApi();
        email = "leo" + RandomStringUtils.randomNumeric(3) + "@yandex.ru";
        password = "pass" + RandomStringUtils.randomNumeric(4);
        name = "User" + RandomStringUtils.randomNumeric(4);
        UserData userData = new UserData(email, password, name);
        Response response = userApi.createUser(userData);
        accessToken = response.body().as(ResponseUserData.class).getAccessToken();
        orderApi = new OrderApi();
        ingredientsImmortal = Arrays.asList("61c0c5a71d1f82001bdaaa6f","61c0c5a71d1f82001bdaaa6c");
        ingredientsAlfa = Arrays.asList("61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa78");
    }

    @After
    public void closeUp() {
        userApi.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя с авторизацией пользователя")
    public void getOrderWithAuthTest() {
        //Авторизация
        UserData userData = new UserData(email, password);
        Response responseUser = userApi.loginUser(userData);
        accessToken = responseUser.body().as(ResponseUserData.class).getAccessToken();
        //Создание заказов
        OrderData firstOrderData = new OrderData(ingredientsImmortal);
        orderApi.CreateOrder(firstOrderData, accessToken);
        OrderData secondOrderData = new OrderData(ingredientsAlfa);
        orderApi.CreateOrder(secondOrderData, accessToken);
        //Получение закозов пользователя
        Response response = orderApi.GetOrdersOfUser(accessToken);
        response.then().assertThat().log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Получение заказов пользователя без авторизации")
    public void getOrderWithoutAuthTest() {
        //Авторизация
        UserData userData = new UserData(email, password);
        Response responseUser = userApi.loginUser(userData);
        accessToken = responseUser.body().as(ResponseUserData.class).getAccessToken();
        //Создание заказов
        OrderData firstOrderData = new OrderData(ingredientsImmortal);
        orderApi.CreateOrder(firstOrderData, accessToken);
        OrderData secondOrderData = new OrderData(ingredientsAlfa);
        orderApi.CreateOrder(secondOrderData, accessToken);
        //Получение закозов пользователя
        Response response = orderApi.GetOrdersOfUser("");
        response.then().assertThat().log().all()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false));
        String actualMassage = response.body().as(ForbiddenResponseUserData.class).getMessage();
        String expectedMassage = "You should be authorised";
        Assert.assertEquals(expectedMassage, actualMassage);
    }
}
