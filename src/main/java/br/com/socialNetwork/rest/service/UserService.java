package br.com.socialNetwork.rest.service;

import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.login.UpdateField;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class UserService {

    UserRepository repository;

    public UserService(UserRepository repository){
        this.repository = repository;
    }

    public void updateFieldValues(User user, List<UpdateField> updateFields) {
        for(UpdateField updateField: updateFields){
            updateFieldValuesInUser(user, updateField);
        }
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

       repository.persist(user);
    }
}
