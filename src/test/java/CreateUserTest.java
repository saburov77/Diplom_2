import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.ForbiddenResponseUserData;
import model.ResponseUserData;
import model.UserApi;
import model.UserData;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static model.Constants.BASE_URI;
import static org.hamcrest.CoreMatchers.is;

public class CreateUserTest {
    protected String email;
    protected String password;
    protected String name;
    protected String accessToken;
    protected UserApi userApi;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        userApi = new UserApi();
        email = "Leo" + RandomStringUtils.randomNumeric(3) + "@yandex.ru";
        password = "pass" + RandomStringUtils.randomNumeric(4);
        name = "User" + RandomStringUtils.randomNumeric(4);
    }

    @After
    public void closeUp() {
        userApi.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создать уникального пользователя")
    public void createUserTest() {
        UserData userData = new UserData(email, password, name);
        Response response = userApi.createUser(userData);
        response.then().assertThat().log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
        accessToken = response.body().as(ResponseUserData.class).getAccessToken();
    }

    @Test
    @DisplayName("Создать пользователя, который уже зарегистрирован")
    public void sameUserTest() {
        UserData userData = new UserData(email, password, name);
        Response firstUser = userApi.createUser(userData);
        Response secondUser = userApi.createUser(userData);
        secondUser.then().assertThat().log().all()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false));
        String actualMassage = secondUser.body().as(ForbiddenResponseUserData.class).getMessage();
        String expectedMassage = "User already exists";
        Assert.assertEquals(expectedMassage, actualMassage);
        accessToken = firstUser.body().as(ResponseUserData.class).getAccessToken();
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить одно из обязательных поле")
    public void createUserWithoutLoginTest() {
        UserData userData = new UserData("", password, name);
        Response response = userApi.createUser(userData);
        response.then().assertThat().log().all()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false));
        String actualMassage = response.body().as(ForbiddenResponseUserData.class).getMessage();
        String expectedMassage = "Email, password and name are required fields";
        Assert.assertEquals(expectedMassage, actualMassage);
    }
}
