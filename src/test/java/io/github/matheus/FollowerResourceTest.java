package io.github.matheus;

import br.com.socialNetwork.domain.model.Follower;
import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.FollowerRepository;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.follower.FollowerRequest;
import br.com.socialNetwork.rest.resource.FollowerResource;
import br.com.socialNetwork.rest.service.TokenService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    @Inject
    TokenService tokenService;
    Long userId;
    Long followerId;
    static String token;

    @BeforeEach
    @Transactional
    void setUP(){
        var user = new User();
        user.setEmail("matheus.reinert@hotmail.com");
        user.setUsername("@matheusReinert");
        user.setPassword("teste");
        user.setToken(tokenService.generateToken());
        userRepository.persist(user);
        userId = user.getId();
        token = user.getToken();

        var follower = new User();
        user.setEmail("eliaser.reinert@hotmail.com");
        user.setUsername("@eliaserReinert");
        user.setPassword("teste");
        userRepository.persist(follower);
        followerId = follower.getId();

        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    @Test
    @DisplayName("Should return 409 when followerId is equal to User id")
    public void sameUserAsFollowerTest(){

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .header("Authorization", token)
                .when()
                .put()
                .then()
                .statusCode(409)
                .body(Matchers.is("You can't follow yourself"));
    }

    @Test
    @DisplayName("Should return 404 on follow a user when userId doesn't exist")
    public void userNotFoundWhenTryingToFollowTest(){

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        var nonexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", nonexistentUserId)
                .header("Authorization", token)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should follow a user")
    public void followUserTest(){

        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .header("Authorization", token)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 on list user followers and user when userId doesn't exist")
    public void userNotFoundWhenListingToFollowTest(){

        var nonexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", nonexistentUserId)
                .header("Authorization", token)
                .when()
                .get()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should list a user's followers")
    public void listFollowTest(){

        var response = given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .header("Authorization", token)
                .when()
                .get()
                .then()
                .extract().response();

        var followerCount  = response.jsonPath().get("followerCount");
        var followersContent = response.jsonPath().getList("content");

        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, followerCount);
        assertEquals(1, followersContent.size());
    }

    @Test
    @DisplayName("Should return 404 on unfollow user and userId doesn't exist")
    public void userNotFoundWhenUnfollowingUserTest(){

        var nonexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", nonexistentUserId)
                .header("Authorization", token)
                .queryParam("followerId", followerId)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should return unfollow an user")
    public void unfollowUserTest(){
        given()
                .pathParam("userId", userId)
                .queryParam("followerId", followerId)
                .header("Authorization", token)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }


}
