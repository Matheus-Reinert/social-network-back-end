package br.com.socialNetwork.domain.repository;

import br.com.socialNetwork.domain.model.Follower;
import br.com.socialNetwork.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    public User findByEmail(String email){
        PanacheQuery<User> query = find("email", email);
        if (query.list().size() > 0){
            return query.list().get(0);
        } else {
            return null;
        }

    }

}
