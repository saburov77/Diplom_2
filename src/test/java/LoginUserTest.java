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

import static model.Constants.BASE_URI;
import static org.hamcrest.CoreMatchers.is;

public class LoginUserTest {
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
        UserData userData = new UserData(email, password, name);
        Response response = userApi.createUser(userData);
        accessToken = response.body().as(ResponseUserData.class).getAccessToken();
    }

    @After
    public void closeUp() {
        userApi.deleteUser(accessToken);
    }

    @Test
    @DisplayName("логин под существующим пользователем")
    public void loginExistUserTest() {
        UserData userData = new UserData(email, password);
        Response response = userApi.loginUser(userData);
        response.then().assertThat().log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
        accessToken = response.body().as(ResponseUserData.class).getAccessToken();

    }

    @Test
    @DisplayName("логин с неверным логином и паролем")
    public void loginWithIncorrectUserTest() {
        UserData userData = new UserData("i"+email, password+"1");
        Response response = userApi.loginUser(userData);
        response.then().assertThat().log().all()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false));
        String actualMassage = response.body().as(ForbiddenResponseUserData.class).getMessage();
        String expectedMassage = "email or password are incorrect";
        Assert.assertEquals(expectedMassage, actualMassage);
    }
}
