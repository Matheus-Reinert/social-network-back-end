package br.com.socialNetwork.rest.dto.comment;

import lombok.Data;

@Data
public class UserPerCommentResponse {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String username;

    public UserPerCommentResponse(Long id, String name, String lastName, String email, String username) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
    }
}
