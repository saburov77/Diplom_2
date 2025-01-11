package model;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static model.Constants.*;

public class UserApi {

    @Step("Отправка запроса POST на создание нового пользователя")
    public Response createUser(UserData userData) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(userData)
                .when()
                .post(USER_CREATE_URI);
    }

    @Step("Удаление пользователя по токену")
    public void deleteUser(String accessToken) {
        if (accessToken != null && !accessToken.isEmpty()) {
            given()
                    .header("Authorization", accessToken)
                    .header("Content-type", "application/json")
                    .delete(AUTHORIZATION_USER_URI)
                    .then().statusCode(HttpStatus.SC_ACCEPTED);
        }
    }

    @Step("Отправка запроса POST на авторизацию пользователя")
    public Response loginUser(UserData userData) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(userData)
                .when()
                .post(LOGIN_USER_URI);
    }

    @Step("Изменение данных о пользователе")
    public Response changingUser(UserData userData, String accessToken){
        return given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .and()
                .body(userData)
                .when()
                .patch(AUTHORIZATION_USER_URI);
    }
}
