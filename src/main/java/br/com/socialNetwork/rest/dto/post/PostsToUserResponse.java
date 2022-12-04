package br.com.socialNetwork.rest.dto.post;

import br.com.socialNetwork.rest.dto.post.PostResponse;
import lombok.Data;

import java.util.List;

@Data
public class PostsToUserResponse {
    private List<PostResponse> posts;
}
