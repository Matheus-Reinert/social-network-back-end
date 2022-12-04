package br.com.socialNetwork.rest.dto.post;

import br.com.socialNetwork.rest.dto.comment.CommentResponse;
import lombok.Data;

import java.util.List;

@Data
public class CommentPerPostResponse {
    private List<CommentResponse> commentResponse;
}
