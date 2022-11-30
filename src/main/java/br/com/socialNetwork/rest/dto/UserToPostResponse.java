package br.com.socialNetwork.rest.dto;

import lombok.Data;

@Data
public class UserToPostResponse {
    Long id;
    String name;
    String lastName;

    public UserToPostResponse(Long id, String name, String lastName) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
    }
}
