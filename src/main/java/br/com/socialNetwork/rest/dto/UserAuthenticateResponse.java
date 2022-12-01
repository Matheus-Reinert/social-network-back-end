package br.com.socialNetwork.rest.dto;

import br.com.socialNetwork.domain.model.User;
import lombok.Getter;


@Getter
public class UserAuthenticateResponse {
//    private String email;
//    private String name;
    private String token;
    private UserResponseToLogin user;

    public UserAuthenticateResponse(String token, User user){
        this.token = token;
        this.user = new UserResponseToLogin(user);
    }

    public static UserAuthenticateResponse toResponse(String type, String token, User user){
        return new UserAuthenticateResponse(type + token, user);
    }
}

