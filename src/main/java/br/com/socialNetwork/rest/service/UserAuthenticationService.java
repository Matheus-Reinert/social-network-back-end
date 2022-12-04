package br.com.socialNetwork.rest.service;

import br.com.socialNetwork.domain.exception.token.ExpiredTokenException;
import br.com.socialNetwork.domain.exception.token.InvalidTokenException;
import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.login.LoginRequest;
import io.jsonwebtoken.Claims;

import javax.enterprise.context.ApplicationScoped;
import java.util.Date;

@ApplicationScoped
public class UserAuthenticationService {

    private final UserRepository userRepository;
    private TokenService tokenService;
    private PasswordService passwordService;

    public UserAuthenticationService(UserRepository userRepository, TokenService tokenService, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordService = passwordService;
    }

    public User authenticate(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());

        if (user == null) {
            return null;
        } else {
            boolean validPassword = passwordService.encoder().matches(loginRequest.getPassword(),user.getPassword());
            if(validPassword){
                user.setToken(tokenService.generateToken());
                return user;
            }
        }
        return null;
    }

    public boolean validateToken(String token) {

        try{
            String treatedToken = token.replace("Bearer ", "");
            Claims claims = tokenService.decodeToken(treatedToken);

            if (claims.getExpiration().before(new Date(System.currentTimeMillis())))
                throw new ExpiredTokenException();

            return true;

        } catch (ExpiredTokenException et){
            et.printStackTrace();
            throw et;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new InvalidTokenException();
        }


    }
}
