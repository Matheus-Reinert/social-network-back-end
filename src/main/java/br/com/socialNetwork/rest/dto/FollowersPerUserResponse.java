package br.com.socialNetwork.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class FollowersPerUserResponse {
    private Integer followerCount;
    private List<FollowerResponse> content;
}