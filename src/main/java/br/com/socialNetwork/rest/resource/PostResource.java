package br.com.socialNetwork.rest.resource;

import br.com.socialNetwork.domain.model.Post;
import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.FollowerRepository;
import br.com.socialNetwork.domain.repository.PostRepository;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.post.CreatePostRequest;
import br.com.socialNetwork.rest.dto.post.PostResponse;
import br.com.socialNetwork.rest.dto.post.PostsToUserResponse;
import br.com.socialNetwork.rest.service.PostService;
import br.com.socialNetwork.rest.service.UserAuthenticationService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Path("/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository userRepository;
    private PostRepository repository;
    private FollowerRepository followerRepository;
    private UserAuthenticationService userAuthenticationService;
    private PostService postService;

    @Inject
    public  PostResource(UserRepository userRepository, PostRepository repository,
                         FollowerRepository followerRepository, UserAuthenticationService userAuthenticationService,
                         PostService postService){
        this.userRepository = userRepository;
        this.repository = repository;
        this.followerRepository = followerRepository;
        this.userAuthenticationService = userAuthenticationService;
        this.postService = postService;
    }

    @POST
    @Path("/users/{userId}")
    @Transactional
    @Operation(summary = "Criar post")
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request){

        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);

        repository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Transactional
    @Operation(summary = "Remover post")
    @Path("/users/{userId}")
    public Response deletePost(@PathParam("userId") Long userId, @HeaderParam("postId") Long postId){
        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = repository.findById(postId);

        if(post == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        repository.deleteByPostAndUser(post.getId(), user.getId());

        return Response.noContent().build();
    }

    @GET
    @Operation(summary = "Retornar post")
    @Path("/users/{userId}")
    public Response listPosts(@PathParam("userId") Long userId, @HeaderParam("followerId") Long followerId){
        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }


        if(followerId == null){
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("You forgot the header followerId")
                    .build();
        }

        User follower = userRepository.findById(followerId);

        if(follower == null){
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Nonexistent followerId")
                    .build();
        }

        boolean follows = followerRepository.follows(follower, user);

        if(!follows){
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You can't see these posts")
                    .build();
        }

        PanacheQuery<Post> query = repository.find("user", Sort.by("dateTime", Sort.Direction.Descending), user);
        var list = query.list();

        var postResponseList = list.stream()
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response.ok(postResponseList).build();
    }


    @PUT
    @Path("/{postId}/like")
    @Operation(summary = "Adicionar like ao post")
    @Transactional
    public Response likePost(@PathParam("postId") Long postId){

        Post post = repository.findById(postId);

        if(post == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        post.like();

        repository.persist(post);

        return Response.ok().build();
    }

    @Path("/user")
    @Transactional
    @Operation(summary = "Retornar posts de usuários seguidos")
    @GET
    public Response getPostsByUser(@HeaderParam("Authorization") String token){
        boolean validToken = userAuthenticationService.validateToken(token);

        if (!validToken){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var postsToUserVisualize = postService.getPostsToUserVisualize(token);

        if(postsToUserVisualize == null || postsToUserVisualize.size() == 0){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        PostsToUserResponse responseObject = new PostsToUserResponse();

        var postsList = postsToUserVisualize.stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());

        responseObject.setPosts(postsList);
        return Response.ok(responseObject).build();
    }


}
