package br.com.socialNetwork.rest.dto.comment;

import br.com.socialNetwork.domain.model.Comment;

public class CommentResponse {
    private Long id;
    private String comment;
    private UserPerCommentResponse user;
    private double likes;



    public CommentResponse(Comment comment) {
        this(comment.getId(), comment.getComment(),
                new UserPerCommentResponse(comment.getUser().getId(),
                comment.getUser().getName(), comment.getUser().getLastName(),
                comment.getUser().getEmail(), comment.getUser().getUsername()),
                comment.getLikes());
    }

    public CommentResponse(long id, String comment, UserPerCommentResponse user, double likes) {
        this.id = id;
        this.comment = comment;
        this.user = user;
        this.likes = likes;
    }
}
