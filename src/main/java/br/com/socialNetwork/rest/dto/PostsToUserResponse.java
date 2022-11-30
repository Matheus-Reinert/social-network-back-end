package br.com.socialNetwork.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostsToUserResponse {
    private List<PostResponse> posts;
}
