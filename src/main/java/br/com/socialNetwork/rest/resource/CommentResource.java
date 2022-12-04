package br.com.socialNetwork.rest.resource;

import br.com.socialNetwork.domain.model.Comment;
import br.com.socialNetwork.domain.model.Post;
import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.CommentRepository;
import br.com.socialNetwork.domain.repository.PostRepository;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.comment.CommentRequest;
import br.com.socialNetwork.rest.dto.comment.CommentResponse;
import br.com.socialNetwork.rest.dto.post.CommentPerPostResponse;
import br.com.socialNetwork.rest.service.CommentService;
import com.google.gson.Gson;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/comments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CommentResource {
    private CommentService commentService;

    @Inject
    public CommentResource(CommentService commentService){
        this.commentService = commentService;
    }


    @POST
    @Path("posts/{postId}")
    @Operation(summary = "Criar comentário")
    @Transactional
    public Response createComment(
            @PathParam("postId") Long postId,
            @QueryParam("userId") Long userId,
            @QueryParam("commentParentId") Long commentParentId,
            @HeaderParam("Authorization") String token,
            CommentRequest request){
        return commentService.createComment(postId, userId, commentParentId, request, token);
    }

    @PUT
    @Path("{commentId}")
    @Operation(summary = "Editar comentário")
    @Transactional
    public Response updateComment(@PathParam("commentId") Long commentId, CommentRequest commentRequest,
                                  @HeaderParam("Authorization") String token) {
        return commentService.updateComment(commentId, commentRequest, token);
    }

    @DELETE
    @Path("{commentId}")
    @Operation(summary = "Remover comentário")
    @Transactional
    public Response deleteComment(@PathParam("commentId") Long commentId, @HeaderParam("Authorization") String token){
        return commentService.deleteComment(commentId, token);
    }

    @GET
    @Path("posts/{postId}")
    @Operation(summary = "Retornar comentários principais")
    @Transactional
    public Response commentsByPost(@PathParam("postId") Long postId, @HeaderParam("Authorization") String token){
        return commentService.commentsByPost(postId, token);
    }

    @GET
    @Path("{commentId}")
    @Operation(summary = "Retornar comentários filhos")
    @Transactional
    public Response commentsChild(@PathParam("commentId") Long commentId,
                                  @HeaderParam("Authorization") String token){

        return commentService.commentsChild(commentId, token);
    }

    @PUT
    @Path("{commentId}/like")
    @Operation(summary = "Adicionar like ao comentário")
    @Transactional
    public Response likeComment(@PathParam("commentId") Long commentId, @HeaderParam("Authorization") String token){
        return commentService.likeComment(commentId, token);
    }
}
