package io.github.matheus.util;

import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.login.UpdateField;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class UserTestUtil {

    @Inject
    UserRepository userRepository;

    public List<UpdateField> getUpdateFields() {
        List<UpdateField> updateFields = new ArrayList<>();

        updateFields.add(new UpdateField("name", "AfterTest"));
        updateFields.add(new UpdateField("lastName", "AfterTest"));
        updateFields.add(new UpdateField("subtitle", "AfterTest"));
        updateFields.add(new UpdateField("aboutMe", "AfterTest"));
        updateFields.add(new UpdateField("email", "AfterTest"));
        updateFields.add(new UpdateField("password", "AfterTest"));
        updateFields.add(new UpdateField("username", "AfterTest"));

        return updateFields;
    }

    public void fillAllFieldsBeforeUpdate(User userBeforeUpdate) {
        userBeforeUpdate.setName("BeforeTest");
        userBeforeUpdate.setLastName("BeforeTest");
        userBeforeUpdate.setSubtitle("BeforeTest");
        userBeforeUpdate.setAboutMe("BeforeTest");
        userBeforeUpdate.setEmail("BeforeTest");
        userBeforeUpdate.setPassword("BeforeTest");
        userBeforeUpdate.setUsername("BeforeTest");
        userBeforeUpdate.setToken("eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2NzAyMDA0MjgsInN1YiI6IlRlc3RlIEp3dCIsImV4cCI6MTY3MDIwMDYwOH0.T0irK8XX-3erg_ShcmZhdHleKjWWCvcaJvpQhqbrkGk");
        userRepository.persist(userBeforeUpdate);
    }
}
