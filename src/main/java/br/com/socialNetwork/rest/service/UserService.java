package br.com.socialNetwork.rest.service;

import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.CommentRepository;
import br.com.socialNetwork.domain.repository.FollowerRepository;
import br.com.socialNetwork.domain.repository.PostRepository;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.login.UpdateField;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class UserService {

    UserRepository userRepository;
    PostRepository postRepository;
    CommentRepository commentRepository;
    FollowerRepository followerRepository;

    public UserService(UserRepository repository, PostRepository postRepository, CommentRepository commentRepository, FollowerRepository followerRepository){
        this.userRepository = repository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
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
           user.setPassword(newValue);
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
        }
    }
}
