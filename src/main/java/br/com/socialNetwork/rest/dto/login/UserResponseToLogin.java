package br.com.socialNetwork.rest.dto.login;

import br.com.socialNetwork.domain.model.User;
import lombok.Data;

@Data
public class UserResponseToLogin {
    private long id;
    private String username;

    UserResponseToLogin(User user){
        this.id = user.getId();
        this.username = user.getUsername();
    }
}
