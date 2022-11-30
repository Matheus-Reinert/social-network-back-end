package br.com.socialNetwork.rest.dto;

import br.com.socialNetwork.domain.model.Comment;
import lombok.Data;

import java.util.List;

@Data
public class CommentPerPostResponse {
    private List<CommentResponse> commentResponse;
}
