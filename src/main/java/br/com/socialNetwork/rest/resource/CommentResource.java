package br.com.socialNetwork.rest.resource;

import br.com.socialNetwork.domain.model.Comment;
import br.com.socialNetwork.domain.model.Post;
import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.CommentRepository;
import br.com.socialNetwork.domain.repository.PostRepository;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.*;
import br.com.socialNetwork.rest.service.CommentService;
import com.google.gson.Gson;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

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

    private UserRepository userRepository;
    private PostRepository postRepository;
    private CommentRepository commentRepository;
    private CommentService commentService;

    @Inject
    public CommentResource(UserRepository userRepository, PostRepository postRepository,
                           CommentRepository commentRepository, CommentService commentService){
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
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
            CommentRequest request){

        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = postRepository.findById(postId);

        if(post == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Comment comment = new Comment();

        if (commentParentId == null){
            comment.setPost(post);
            comment.setUser(user);
            comment.setComment(request.getComment());
            comment.setLikes(0);
        } else {
            Comment commentParent = commentRepository.findById(commentParentId);
            comment.setUser(user);
            comment.setComment(request.getComment());
            comment.setCommentParent(commentParent);
            comment.setLikes(0);
        }

        commentRepository.persist(comment);

        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("{commentId}")
    @Operation(summary = "Editar comentário")
    @Transactional
    public Response updateComment(@PathParam("commentId") Long commentId, CommentRequest commentRequest) {

        Comment comment = commentRepository.findById(commentId);

        if(comment == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        comment.setComment(commentRequest.getComment());
        commentRepository.persist(comment);

        return Response.noContent().build();
    }

    @DELETE
    @Path("{commentId}")
    @Operation(summary = "Remover comentário")
    @Transactional
    public Response deleteComment(@PathParam("commentId") Long commentId){
        Comment comment = commentRepository.findById(commentId);

        if(comment != null){
            commentService.removeChildComments(comment);
            commentRepository.delete(comment);
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("posts/{postId}")
    @Operation(summary = "Retornar comentários principais")
    @Transactional
    public Response commentsByPost(@PathParam("postId") Long postId){

        Post post = postRepository.findById(postId);

        if(post == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Comment> comments = commentService.getComments(post);

        if(comments == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        CommentPerPostResponse responseObject = new CommentPerPostResponse();

        var commentsResponseList = comments.stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());

        responseObject.setCommentResponse(commentsResponseList);
        return Response.ok(new Gson().toJson(responseObject.getCommentResponse())).build();
    }

    @GET
    @Path("{commentId}")
    @Operation(summary = "Retornar comentários filhos")
    @Transactional
    public Response commentsChild(@PathParam("commentId") Long commentId){

        Comment comment = commentRepository.findById(commentId);

        if(comment == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(commentService.getCommentChilds(comment.getId())).build();
    }

    @PUT
    @Path("{commentId}/like")
    @Operation(summary = "Adicionar like ao comentário")
    @Transactional
    public Response likeComment(@PathParam("commentId") Long commentId){

        Comment comment = commentRepository.findById(commentId);

        if(comment == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        comment.like();

        commentRepository.persist(comment);

        return Response.ok().build();
    }
}
