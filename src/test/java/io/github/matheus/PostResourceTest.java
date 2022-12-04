package io.github.matheus;

import br.com.socialNetwork.domain.model.Follower;
import br.com.socialNetwork.domain.model.Post;
import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.FollowerRepository;
import br.com.socialNetwork.domain.repository.PostRepository;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.post.CreatePostRequest;
import br.com.socialNetwork.rest.resource.PostResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;

    @Inject
    PostRepository postRepository;
    Long userId;
    Long userNotFollowerId;

    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setUP(){
        var user = new User();
        user.setEmail("matheus.reinert@hotmail.com");
        user.setUsername("@matheusReinert");
        user.setPassword("teste");
        userRepository.persist(user);
        userId = user.getId();

        var userNotFollower = new User();
        user.setEmail("eliaser.reinert@hotmail.com");
        user.setUsername("@eliaserReinert");
        user.setPassword("teste");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        var userFollower = new User();
        user.setEmail("janeaScheferReinert@hotmail.com");
        user.setUsername("@janeReinert");
        user.setPassword("teste");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);

        Post post = new Post();
        post.setText("UEPAAAA");
        post.setUser(user);
        postRepository.persist(post);

    }

    @Test
    @DisplayName("should create a post for a user")
    public void createPostTest(){
        var body = new CreatePostRequest();
        body.setText("OOi");

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId",userId)
                .when()
                .post("users/{userId}")
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("should return 404 when try to make a post for an nonexistent user")
    public void postForUnexistentUserTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("OOi");

        var nonexistentUserId = 99;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId",nonexistentUserId)
                .when()
                .post("users/{userId}")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return 404 when user doesn't exist")
    public void listPostUserNotFoundTest(){
        var nonexistentUserId = 999;

        given().
                pathParam("userId", nonexistentUserId)
                .when()
                .get("users/{userId}")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return 404 when followerId header is not present")
    public void listPostFollowerHeaderNotSendTest(){

        given().
                pathParam("userId", userId)
                .when()
                .get("users/{userId}")
                .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the header followerId"));
    }

    @Test
    @DisplayName("Should return 404 when followerId doesn't exist")
    public void listPostFollowerNotFoundTest(){

        var nonexistentFollowerId = 999;

        given().
                pathParam("userId", userId)
                .header("followerId", nonexistentFollowerId)
                .when()
                .get("users/{userId}")
                .then()
                .statusCode(400)
                .body(Matchers.is("Nonexistent followerId"));
    }

    @Test
    @DisplayName("Should return 403 when follower isn't a follower")
    public void listPostNotAFollowerTest(){

        given().
                pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
                .when()
                .get("users/{userId}")
                .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts"));
    }

    @Test
    @DisplayName("Should return posts")
    public void listPostsTest(){

        given()
                .pathParam("userId", userId)
                .header("followerId", userFollowerId)
                .when()
                .get("users/{userId}")
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }


}