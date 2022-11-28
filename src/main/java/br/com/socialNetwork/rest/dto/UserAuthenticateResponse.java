package br.com.socialNetwork.rest.dto;

import lombok.Getter;


@Getter
public class UserAuthenticateResponse {
//    private String email;
//    private String name;
    private String token;

    public UserAuthenticateResponse(String token){
        this.token = token;
    }

    public static UserAuthenticateResponse toResponse(String type, String token){
        return new UserAuthenticateResponse(type + token);
    }
}

