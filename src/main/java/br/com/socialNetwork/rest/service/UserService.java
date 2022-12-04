package br.com.socialNetwork.rest.service;

import br.com.socialNetwork.domain.exception.user.UserUpdateException;
import br.com.socialNetwork.domain.exception.user.createUserException;
import br.com.socialNetwork.domain.exception.user.deleteUserException;
import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.CommentRepository;
import br.com.socialNetwork.domain.repository.FollowerRepository;
import br.com.socialNetwork.domain.repository.PostRepository;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.login.UpdateField;
import br.com.socialNetwork.rest.dto.user.CreateUserRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final FollowerRepository followerRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;

    public UserService(UserRepository repository, PostRepository postRepository, CommentRepository commentRepository,
                       FollowerRepository followerRepository, PasswordService passwordService, TokenService tokenService){
        this.userRepository = repository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
    }

    public void updateFieldValues(User user, List<UpdateField> updateFields) {
        for(UpdateField updateField: updateFields){
            updateFieldValuesInUser(user, updateField);
        }

        userRepository.persist(user);
    }

    private void updateFieldValuesInUser(User user, UpdateField updateField) {
       String field = updateField.getField();
       String newValue = updateField.getNewValue();

       if (field.equals("name")){
           user.setName(newValue);
       } else if (field.equals("lastName")) {
           user.setLastName(newValue);
       } else if (field.equals("subtitle")) {
           user.setSubtitle(newValue);
       } else if (field.equals("aboutMe")) {
           user.setAboutMe(newValue);
       } else if (field.equals("email")) {
           user.setEmail(newValue);
       } else if (field.equals("password")) {
           user.setPassword(passwordService.encoder().encode(newValue));
       } else if (field.equals("username")) {
           user.setUsername("@" + newValue);
       }
    }

    public void deleteUser(User user) {
        try {
            commentRepository.deleteByUser(user.getId());
            postRepository.deleteByUser(user.getId());
            followerRepository.deleteByUser(user.getId());
            userRepository.delete(user);
        } catch (Exception ex){
            ex.printStackTrace();
            throw new deleteUserException();
        }
    }

    public User createUser(CreateUserRequest userRequest) {
        try {
            User user = new User();
            user.setEmail(userRequest.getEmail());
            user.setPassword(passwordService.encoder().encode(userRequest.getPassword()));
            user.setToken(tokenService.generateToken());
            user.setUsername(userRequest.getUsername());
            userRepository.persist(user);

            return  user;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new createUserException();
        }
    }

    public Response findUserById(Long id) {
        User user = userRepository.findById(id);

        if(user != null){
            return Response.ok(user).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    public Response deleteUserById(Long id) {
        User user = userRepository.findById(id);

        if(user != null){
            deleteUser(user);
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    public Response updateUserFields(Long id, List<UpdateField> updateFields) {

        try {
            User user = userRepository.findById(id);

            if(user != null){
                updateFieldValues(user, updateFields);
                return Response.noContent().build();
            }

            return Response.status(Response.Status.NOT_FOUND).build();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new UserUpdateException();
        }

    }
}
