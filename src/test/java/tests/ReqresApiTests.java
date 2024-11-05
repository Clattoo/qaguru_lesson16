package tests;

import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import models.RegisterMissingPasswordModel;
import models.RegisterBodyModel;
import models.RegisterResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.ListOfUserSpec.listOfUsersRequestSpec;
import static specs.ListOfUserSpec.listOfUsersResponseSpec;
import static specs.RegisterSpec.*;

public class ReqresApiTests extends TestBase {

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Owner("Maxim Shlemin")
    @DisplayName("Проверка успешного получения списка пользователей при отправке " +
            "GET-запроса https://reqres.in/api/users?page=2")

    void successfulGetListOfUserTest() {
        given(listOfUsersRequestSpec)
                .when()
                    .get()
                .then()
                    .spec(listOfUsersResponseSpec);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Owner("Maxim Shlemin")
    @DisplayName("Проверка количества разделов в теле ответа при отправке " +
            "GET-запроса https://reqres.in/api/users?page=2")

    void amountOfKeysBodyDataListOfUserTest() {
        given(listOfUsersRequestSpec)
                .when()
                    .get()
                .then()
                    .spec(listOfUsersResponseSpec)
                    .body("data", hasSize(6));
    }


    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Maxim Shlemin")
    @DisplayName("Регистрация пользователя с полными данными email/password")

    void registerAccountWithRightCreditsTest() {
        RegisterBodyModel registerData = new RegisterBodyModel();
        registerData.setEmail("eve.holt@reqres.in");
        registerData.setPassword("pistol");

        RegisterResponseModel registerResponse = step("Отправить POST-запрос на регистрацию с правильными email" +
                " и password https://reqres.in/api/register", () ->

                given(registerRequestSpec)
                    .body(registerData)

               .when()
                    .post()

               .then()
                        .spec(registerResponseSpec)
                    .extract().as(RegisterResponseModel.class));

        step("Проверить ответ и регистрацию пользователя", () -> {
        assertEquals("4", registerResponse.getId());
        assertEquals("QpwL5tke4Pnpja7X4", registerResponse.getToken());
    });
}

    @Test
    void registerAccountWithoutPasswordTest() {
//        String registerData = "{\"email\": \"sydney@fife\"}";

        RegisterBodyModel registerData = new RegisterBodyModel();
        registerData.setEmail("eve.holt@reqres.in");

        RegisterMissingPasswordModel registerResponse = step("Отправить POST-запрос на регистрацию с правильными email" +
                " и password https://reqres.in/api/register", () ->

                given(registerRequestSpec)
                        .body(registerData)

                        .when()
                        .post()

                        .then()
                        .spec(missingPasswordRegisterResponseSpec)
                        .extract().as(RegisterMissingPasswordModel.class));

        step("Проверить ответ и регистрацию пользователя", () -> {
            assertEquals("Missing password", registerResponse.getError());
        });
    }

    @Test
    void registerAccountWithWrongBodyTest() {
        String registerData = "#}";

        given()
                .body(registerData)
                .contentType(JSON)
                .log().uri()
                .log().body()
                .log().headers()

                .when()
                    .post("/register")

                .then()
                    .log().status()
                    .statusCode(400);
    }

}
