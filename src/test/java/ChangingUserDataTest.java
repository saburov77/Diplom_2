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

import static model.Constants.BASE_URI;
import static org.hamcrest.CoreMatchers.is;

public class ChangingUserDataTest {
    protected String email;
    protected String password;
    protected String name;
    protected String accessToken;
    protected UserApi userApi;

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
    }

    @After
    public void closeUp() {
        userApi.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Изменение Email пользователя с авторизацией")
    public void changingEmailWithAuthTest() {
        //Авторизация пользователя
        UserData userData = new UserData(email, password);
        Response response = userApi.loginUser(userData);
        accessToken = response.body().as(ResponseUserData.class).getAccessToken();
        //Изменение данных пользователя
        UserData newUserData = new UserData("i"+email, password, name);
        Response newResponse = userApi.changingUser(newUserData, accessToken);
        newResponse.then().assertThat().log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));

        String expectedEmail = newUserData.getEmail();
        String actualEmail = newResponse.body().as(ResponseUserData.class).getUser().getEmail();
        Assert.assertEquals(expectedEmail, actualEmail);
    }

    @Test
    @DisplayName("Изменение Name пользователя с авторизацией")
    public void changingNameWithAuthTest() {
        //Авторизация пользователя
        UserData userData = new UserData(email, password);
        Response response = userApi.loginUser(userData);
        accessToken = response.body().as(ResponseUserData.class).getAccessToken();
        //Изменение данных пользователя
        UserData newUserData = new UserData(email, password, name+"5");
        Response newResponse = userApi.changingUser(newUserData, accessToken);
        newResponse.then().assertThat().log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));

        String expectedName = newUserData.getName();
        String actualName = newResponse.body().as(ResponseUserData.class).getUser().getName();
        Assert.assertEquals(expectedName, actualName);
    }

    @Test
    @DisplayName("Изменение Email пользователя без авторизации")
    public void changingEmailWithoutAuthTest() {
        //Изменение данных пользователя
        UserData newUserData = new UserData("im"+email, password, name);
        Response newResponse = userApi.changingUser(newUserData, "accessToken");
        newResponse.then().assertThat().log().all()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false));

        String expectedMessage = "You should be authorised";
        String actualMessage = newResponse.body().as(ForbiddenResponseUserData.class).getMessage();
        Assert.assertEquals(expectedMessage, actualMessage);

    }

    @Test
    @DisplayName("Изменение Name пользователя без авторизации")
    public void changingNameWithoutAuthTest() {
        //Изменение данных пользователя
        UserData newUserData = new UserData(email, password, name+"1");
        Response newResponse = userApi.changingUser(newUserData, "accessToken");
        newResponse.then().assertThat().log().all()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false));

        String expectedMessage = "You should be authorised";
        String actualMessage = newResponse.body().as(ForbiddenResponseUserData.class).getMessage();
        Assert.assertEquals(expectedMessage, actualMessage);

    }
}
