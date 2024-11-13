package tests;

import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import models.RegisterMissingDataModel;
import models.RegisterBodyModel;
import models.RegisterResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static specs.RegisterSpec.*;

@Severity(SeverityLevel.BLOCKER)
@Owner("Maxim Shlemin")
public class ReqresApiTests extends TestBase {

    @Test
    @DisplayName("Проверка успешного получения списка пользователей при отправке " +
            "GET-запроса https://reqres.in/api/users?page=2")
    @Tag("reqres_api")
    void successfulGetListOfUserTest() {
        Response response = step("Отправить GET-запрос на получение списка пользователей", () ->
                given(listOfUsersRequestSpec)
                        .when()
                        .get("/users")
        );

        step("Проверка правильности ответа на GET-запрос", () ->
                response.then()
                        .spec(responseSpec200)
        );
    }

    @Test
    @DisplayName("Проверка количества разделов в теле ответа при отправке " +
            "GET-запроса https://reqres.in/api/users?page=2")
    @Tag("reqres_api")
    void amountOfKeysBodyDataListOfUserTest() {
        Response response = step("Отправить GET-запрос на получение списка пользователей", () ->
                given(listOfUsersRequestSpec)
                        .when()
                        .queryParam("page", "2")
                        .get("/users")
        );

        step("Проверка количества разделов в теле ответа", () ->
                response.then()
                        .spec(responseSpec200)
                        .body("data", hasSize(6))
        );
    }


    @Test
    @DisplayName("Регистрация пользователя с полными данными email/password")
    @Tag("reqres_api")
    void registerAccountWithRightCreditsTest() {
        RegisterBodyModel registerData = new RegisterBodyModel();
        registerData.setEmail("eve.holt@reqres.in");
        registerData.setPassword("pistol");

        RegisterResponseModel registerResponse = step("Отправить POST-запрос на регистрацию с правильными email" +
                " и password https://reqres.in/api/register", () ->

                given(registerRequestSpec)
                    .body(registerData)

               .when()
                    .post("/register")

               .then()
                        .spec(responseSpec200)
                    .extract().as(RegisterResponseModel.class));

        step("Проверить успешное завершение регистрации", () -> {
        assertEquals("4", registerResponse.getId());
        assertNotNull(registerResponse.getToken());
    });
}

    @Test
    @DisplayName("Регистрация пользователя без пароля")
    @Tag("reqres_api")
    void registerAccountMissingPasswordTest() {

        RegisterBodyModel registerData = new RegisterBodyModel();
        registerData.setEmail("eve.holt@reqres.in");

        RegisterMissingDataModel registerResponse = step("Отправить POST-запрос на регистрацию с правильными email" +
                " и password https://reqres.in/api/register", () ->

                given(registerRequestSpec)
                        .body(registerData)

                        .when()
                        .post("/register")

                        .then()
                        .spec(responseSpec400)
                        .extract().as(RegisterMissingDataModel.class));

        step("Проверить ответ и регистрацию пользователя", () -> {
            assertEquals("Missing password", registerResponse.getError());
        });
    }

    @Test
    @DisplayName("Регистрация пользователя без email и пароля")
    @Tag("reqres_api")
    void registerAccountMissingEmailPasswordTest() {

        RegisterBodyModel registerData = new RegisterBodyModel();
        registerData.setEmail("");
        registerData.setPassword("");

        RegisterMissingDataModel registerResponse = step("Отправить POST-запрос на регистрацию c отсутствующими email" +
                "и password https://reqres.in/api/register", () ->

                given(registerRequestSpec)
                        .body(registerData)

                        .when()
                        .post("/register")

                        .then()
                        .spec(responseSpec400)
                        .extract().as(RegisterMissingDataModel.class));

        step("Получение ошибки 400", () -> {
            assertEquals("Missing email or username", registerResponse.getError());
        });
    }

}
