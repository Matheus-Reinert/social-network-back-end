package br.com.socialNetwork.rest.dto.comment;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentRequest {
    @NotBlank(message = "Comment is required")
    private String comment;
}
