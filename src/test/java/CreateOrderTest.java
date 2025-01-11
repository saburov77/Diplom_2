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

public class CreateOrderTest {
    protected String email;
    protected String password;
    protected String name;
    protected String accessToken;
    protected UserApi userApi;
    protected OrderApi orderApi;
    protected List<String> ingredients;
    protected List<String> notValidHashIds;

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
        ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa6f","61c0c5a71d1f82001bdaaa6c");
        notValidHashIds = Arrays.asList("1","1");
    }

    @After
    public void closeUp() {
        userApi.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией, с ингредиентами")
    public void createOrderWithAuthTest() {
       //Авторизация
        UserData userData = new UserData(email, password);
        Response responseUser = userApi.loginUser(userData);
        accessToken = responseUser.body().as(ResponseUserData.class).getAccessToken();
        //Создание заказа
        OrderData orderData = new OrderData(ingredients);
        Response response = orderApi.CreateOrder(orderData, accessToken);
        response.then().assertThat().log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Создание заказа без авторизации, с ингредиентами")
    public void createOrderWithoutAuthTest() {
        //Создание заказа
        OrderData orderData = new OrderData(ingredients);
        Response response = orderApi.CreateOrder(orderData, "");
        response.then().assertThat().log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        //Авторизвция
        UserData userData = new UserData(email, password);
        Response responseUser = userApi.loginUser(userData);
        accessToken = responseUser.body().as(ResponseUserData.class).getAccessToken();
        //Создание заказа
        OrderData orderData = new OrderData();
        Response response = orderApi.CreateOrder(orderData, accessToken);
        response.then().assertThat().log().all()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", is(false));
        String actualMassage = response.body().as(ForbiddenResponseUserData.class).getMessage();
        String expectedMassage = "Ingredient ids must be provided";
        Assert.assertEquals(expectedMassage, actualMassage);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией, с невалидным хэш ингредиентов")
    public void createOrderWithNotValidHashIngredientsTest() {
        //Авторизвция
        UserData userData = new UserData(email, password);
        Response responseUser = userApi.loginUser(userData);
        accessToken = responseUser.body().as(ResponseUserData.class).getAccessToken();
        //Создание заказа
        OrderData orderData = new OrderData(notValidHashIds);
        Response response = orderApi.CreateOrder(orderData, accessToken);
        response.then().assertThat().log().all()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

}
