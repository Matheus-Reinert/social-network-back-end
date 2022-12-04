package io.github.matheus;


import br.com.socialNetwork.domain.model.Follower;
import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.login.UpdateField;
import br.com.socialNetwork.rest.dto.user.CreateUserRequest;
import br.com.socialNetwork.rest.dto.user.ResponseError;
import io.github.matheus.util.UserTestUtil;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @TestHTTPResource("/users")
    URL apiURL;

    @Inject
    UserRepository userRepository;
    @Inject
    UserTestUtil userTestUtil;
    static int userListSize;
    static int deletedUserId;

    @BeforeEach
    @Transactional
    void setUP(){
        var user = new User();
        user.setEmail("matheus.reinert@hotmail.com");
        user.setUsername("@matheusReinert");
        user.setPassword("teste");
        userRepository.persist(user);
    }

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
        userListSize = userRepository.findAll().list().size();

        given()
                    .contentType(ContentType.JSON)
                .when()
                    .get(apiURL)
                .then()
                    .statusCode(200).body("size()", Matchers.is(userListSize));
   }

    @Test
    @DisplayName("Should delete an users")
    @Order(4)
    public void deleteUser(){
        Random random = new Random();

        deletedUserId = random.nextInt(userListSize);
        if(deletedUserId == 0){
            deletedUserId = 1;
        }

        var response = given()
                .contentType(ContentType.JSON)
                .when()
                .pathParams("id", deletedUserId)
                .delete(apiURL + "/{id}")
                .then()
                .extract().response();

        int newListSize = userRepository.findAll().list().size();


        assertEquals(204, response.getStatusCode());
        assertEquals(userListSize, newListSize);
    }

    @Test
    @DisplayName("Should return 404 if not found user in delete")
    @Order(5)
    public void deleteUserError(){
        Random random = new Random();

        int id = random.nextInt(userListSize) + 1000;

        var response = given()
                .contentType(ContentType.JSON)
                .when()
                .pathParams("id", id)
                .delete(apiURL + "/{id}")
                .then()
                .extract().response();

        assertEquals(404, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 if not found users in update")
    @Order(6)
    public void updateUserError(){
        Random random = new Random();
        int id = random.nextInt(userListSize) + 1000;

        var response = given()
                .contentType(ContentType.JSON)
                .when()
                .pathParams("id", id)
                .put(apiURL + "/{id}")
                .then()
                .extract().response();

        assertEquals(404, response.getStatusCode());
    }
}