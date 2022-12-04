package io.github.matheus;


import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.user.CreateUserRequest;
import br.com.socialNetwork.rest.dto.user.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @TestHTTPResource("/users")
    URL apiURL;

    @Inject
    UserRepository userRepository;

    @Test
    @DisplayName("should create user successfully")
    @Order(1)
    public void createUserTest(){
        var user = new CreateUserRequest();
        user.setEmail("matheus.reinert@hotmail.com");
        user.setUsername("@matheusReinert");
        user.setPassword("teste");

        var response =
                given()
                        .contentType(ContentType.JSON)
                        .body(user)
                        .when()
                        .post(apiURL)
                        .then()
                        .extract().response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @DisplayName("Should return error when json is not valid")
    @Order(2)
    public void createUserValidationErrorTest(){
        var user = new CreateUserRequest();
        user.setUsername(null);
        user.setEmail(null);
        user.setPassword(null);

        var response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(apiURL)
                .then()
                .extract().response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");

        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
    }

    @Test
    @DisplayName("Should list all users")
    @Order(3)
    public void listAllUsersTest(){
        int listSize = userRepository.findAll().list().size();

        given()
                    .contentType(ContentType.JSON)
                .when()
                    .get(apiURL)
                .then()
                    .statusCode(200).body("size()", Matchers.is(listSize));
   }

}