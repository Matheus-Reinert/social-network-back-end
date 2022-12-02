package io.github.matheus;

import br.com.socialNetwork.domain.model.Comment;
import br.com.socialNetwork.domain.model.Post;
import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.PostRepository;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.CommentRequest;
import br.com.socialNetwork.rest.resource.CommentResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(CommentResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommentResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    PostRepository postRepository;

    Long userId;
    Long postId = Long.valueOf(1);

    @BeforeEach
    @Transactional
    public void setUP(){
        var user = new User();
        user.setEmail("matheus.reinert@hotmail.com");
        user.setUsername("@matheusReinert");
        user.setPassword("teste");
        userRepository.persist(user);
        userId = user.getId();

        var userThatWillComment = new User();
        userThatWillComment.setEmail("eliaser.reinert@hotmail.com");
        userThatWillComment.setUsername("@eliaserReinert");
        userThatWillComment.setPassword("teste");
        userRepository.persist(userThatWillComment);

        Post post = new Post();
        post.setText("UEPAAAA");
        post.setUser(user);
        postRepository.persist(post);
    }

    @Test
    @Order(1)
    @DisplayName("should create a comment in a post")
    public void createCommentTest(){
        var body = new CommentRequest();
        body.setComment("teste");

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("postId", postId)
                .queryParam("userId", userId)
                .when()
                .post("posts/{postId}")
                .then()
                .statusCode(201);
    }

    @Test
    @Order(2)
    @DisplayName("should create a reply comment in a comment")
    public void createReplyCommentTest(){
        var body = new CommentRequest();
        body.setComment("teste");

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("postId", postId)
                .queryParam("userId", userId)
                .queryParam("commentParentId", 1)
                .when()
                .post("posts/{postId}")
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("Should list all principal comments")
    @Order(3)
    public void listAllPrincipalComments(){

        given()
                .contentType(ContentType.JSON)
                .when()
                .pathParams("postId", postId)
                .get("posts/{postId}")
                .then()
                .statusCode(200).body("size()", Matchers.is(1));

    }

    @Test
    @DisplayName("Should list all reply comments")
    @Order(4)
    public void listAllReplyComments(){

        given()
                .contentType(ContentType.JSON)
                .when()
                .pathParams("commentId", 1)
                .get("{commentId}")
                .then()
                .statusCode(200).body("size()", Matchers.is(1));

    }
}
