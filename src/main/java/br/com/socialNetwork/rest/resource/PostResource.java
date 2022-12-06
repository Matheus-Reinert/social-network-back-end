package br.com.socialNetwork.rest.resource;

import br.com.socialNetwork.rest.dto.post.CreatePostRequest;
import br.com.socialNetwork.rest.service.PostService;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {
    private PostService postService;

    @Inject
    public  PostResource(PostService postService){
        this.postService = postService;
    }

    @POST
    @Path("/users/{userId}")
    @Transactional
    @Operation(summary = "Criar post")
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest postRequest, @HeaderParam("Authorization") String token){
         return postService.createPost(userId, postRequest, token);
    }

    @DELETE
    @Transactional
    @Operation(summary = "Remover post")
    @Path("/users/{userId}")
    public Response deletePost(@PathParam("userId") Long userId, @HeaderParam("postId") Long postId, @HeaderParam("Authorization") String token){
        return postService.deletePost(userId, postId, token);
    }

    @GET
    @Operation(summary = "Retornar post")
    @Path("/users/{userId}")
    public Response listPosts(@PathParam("userId") Long userId, @HeaderParam("followerId") Long followerId, @HeaderParam("Authorization") String token){
        return postService.listPosts(userId, followerId, token);
    }


    @PUT
    @Path("/{postId}/like")
    @Operation(summary = "Adicionar like ao post")
    @Transactional
    public Response likePost(@PathParam("postId") Long postId, @HeaderParam("Authorization") String token){
        return postService.likePost(postId, token);
    }

    @Path("/user")
    @Transactional
    @Operation(summary = "Retornar posts de usuários seguidos")
    @GET
    public Response getPostsByUser(@HeaderParam("Authorization") String token){
        return postService.getPostsByUser(token);
    }


}
