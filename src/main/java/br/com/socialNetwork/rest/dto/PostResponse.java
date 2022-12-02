package br.com.socialNetwork.rest.dto;

import br.com.socialNetwork.domain.model.Follower;
import br.com.socialNetwork.domain.model.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponse {
    private long id;
    private String text;
    private LocalDateTime dateTime;
    private UserToPostResponse user;
    private double likes;

    public PostResponse() {
    }

    public static PostResponse fromEntity(Post post){
        var response = new PostResponse();
        response.setText(post.getText());
        response.setDateTime(post.getDateTime());

        return response;
    }

    public PostResponse(Post post) {
        this(post.getId(), post.getText(), post.getDateTime(), new UserToPostResponse(post.getUser().getId(), post.getUser().getName(), post.getUser().getLastName()), post.getLikes());
    }

    public PostResponse(long id, String text, LocalDateTime dateTime, UserToPostResponse user, double likes) {
        this.id = id;
        this.text = text;
        this.dateTime = dateTime;
        this.user = user;
        this.likes = likes;
    }
}