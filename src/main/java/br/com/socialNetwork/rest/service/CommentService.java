package br.com.socialNetwork.rest.service;

import br.com.socialNetwork.domain.exception.comment.CreateCommentException;
import br.com.socialNetwork.domain.exception.comment.DeleteCommentException;
import br.com.socialNetwork.domain.exception.comment.UpdateCommentException;
import br.com.socialNetwork.domain.model.Comment;
import br.com.socialNetwork.domain.model.Post;
import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.CommentRepository;
import br.com.socialNetwork.domain.repository.PostRepository;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.comment.CommentRequest;
import br.com.socialNetwork.rest.dto.comment.CommentResponse;
import br.com.socialNetwork.rest.dto.post.CommentPerPostResponse;
import com.google.gson.Gson;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CommentService {
    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private PostRepository postRepository;
    private UserAuthenticationService userAuthenticationService;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository, UserAuthenticationService userAuthenticationService){
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.userAuthenticationService = userAuthenticationService;
    }

    public Response createComment(Long postId, Long userId, Long commentParentId, CommentRequest commentRequest, String token) {
        try{

            Comment comment;

            if (!userAuthenticationService.validateToken(token)){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            User user = userRepository.findById(userId);
            if(user == null){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            Post post = postRepository.findById(postId);
            if(post == null){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            if (commentParentId == null){
                comment = createParentComment(post, user, commentRequest);
            } else {
                comment = createChildComment(user, commentRequest, commentParentId);
            }

            commentRepository.persist(comment);

            return Response.status(Response.Status.CREATED).build();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CreateCommentException();
        }
    }

    private Comment createParentComment(Post post, User user, CommentRequest commentRequest) {
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setComment(commentRequest.getComment());
        comment.setLikes(0);
        return comment;
    }

    private Comment createChildComment(User user, CommentRequest commentRequest, Long commentParentId) {
        Comment comment = new Comment();
        Comment commentParent = commentRepository.findById(commentParentId);
        comment.setUser(user);
        comment.setComment(commentRequest.getComment());
        comment.setCommentParent(commentParent);
        comment.setLikes(0);
        return comment;
    }

    public Response updateComment(Long commentId, CommentRequest commentRequest, String token) {
        try {

            if (!userAuthenticationService.validateToken(token)){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            Comment comment = commentRepository.findById(commentId);

            if(comment == null){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            updateCommentText(comment, commentRequest);

            return Response.noContent().build();
        }catch (Exception ex){
            ex.printStackTrace();
            throw new UpdateCommentException();

        }
    }

    private void updateCommentText(Comment comment, CommentRequest commentRequest) {
        comment.setComment(commentRequest.getComment());
        commentRepository.persist(comment);
    }

    public Response deleteComment(Long commentId, String token) {
        try {
            if (!userAuthenticationService.validateToken(token)){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            Comment comment = commentRepository.findById(commentId);

            if(comment != null){
                deleteParentAndChildComments(comment);
                return Response.noContent().build();
            }

            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DeleteCommentException();
        }
    }

    private void deleteParentAndChildComments(Comment comment) {
        removeChildComments(comment);
        commentRepository.delete(comment);
    }

    public void removeChildComments(Comment comment) {
        List<Comment> commentChilds = commentRepository.findByParentId(comment.getId());

        if (!commentChilds.isEmpty()){
            for (Comment commentChild: commentChilds ){
                commentRepository.delete(commentChild);
            }
        }
    }

    public Response commentsByPost(Long postId, String token) {
        if (!userAuthenticationService.validateToken(token)){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Post post = postRepository.findById(postId);

        if(post == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Comment> comments = getComments(post);

        if(comments == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return commentsByPostResponse(comments);
    }

    public List<Comment> getComments(Post post){
        return commentRepository.findCommentsByPost(post);
    }

    private Response commentsByPostResponse(List<Comment> comments) {
        CommentPerPostResponse commentPerPostResponse = new CommentPerPostResponse();

        var commentsResponseList = comments.stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());

        commentPerPostResponse.setCommentResponse(commentsResponseList);
        return Response.ok(new Gson().toJson(commentPerPostResponse.getCommentResponse())).build();
    }

    public Response commentsChild(Long commentId, String token) {

        if (!userAuthenticationService.validateToken(token)){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Comment comment = commentRepository.findById(commentId);

        if(comment == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(getCommentChilds(comment.getId())).build();
    }

    public String getCommentChilds(Long commentId) {
        List<Comment> commentChilds = commentRepository.findByParentId(commentId);
        return new Gson().toJson(commentChilds);
    }

    public Response likeComment(Long commentId, String token) {

        if (!userAuthenticationService.validateToken(token)){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Comment comment = commentRepository.findById(commentId);

        if(comment == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        like(comment);

        return Response.ok().build();
    }

    private void like(Comment comment) {
        comment.like();
        commentRepository.persist(comment);
    }
}
