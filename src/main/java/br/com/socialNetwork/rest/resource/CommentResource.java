package br.com.socialNetwork.rest.resource;

import br.com.socialNetwork.domain.model.Comment;
import br.com.socialNetwork.domain.model.Post;
import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.CommentRepository;
import br.com.socialNetwork.domain.repository.PostRepository;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.CommentRequest;
import br.com.socialNetwork.rest.service.CommentService;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    @Path("posts/{postId}/comments")
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
    @Path("posts/{postId}/comments")
    @Transactional
    public Response commentsByPost(@PathParam("postId") Long postId){

        Post post = postRepository.findById(postId);

        if(post == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(commentService.getComments(post)).build();
    }

    @GET
    @Path("{commentId}/comments")
    @Transactional
    public Response commentsChild(@PathParam("commentId") Long commentId){

        Comment comment = commentRepository.findById(commentId);

        if(comment == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(commentService.getCommentChilds(comment.getId())).build();
    }

    @PUT
    @Path("{commentId}/like/comments")
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
