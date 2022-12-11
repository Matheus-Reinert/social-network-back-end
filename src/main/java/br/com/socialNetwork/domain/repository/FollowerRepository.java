package br.com.socialNetwork.domain.repository;

import br.com.socialNetwork.domain.model.Follower;
import br.com.socialNetwork.domain.model.Post;
import br.com.socialNetwork.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user){
        var params = Parameters.with("follower", follower).and("user", user).map();

        PanacheQuery<Follower> query = find("follower =:follower and user =:user", params);
        Optional<Follower> result = query.firstResultOptional();

        return result.isPresent();
    }

    public List<Follower> findByUser(Long userId){
        PanacheQuery<Follower> query = find("user.id", userId);
        return query.list();
    }

    public void deleteByFollowerAndUser(Long followerId, Long userId) {
        var params = Parameters
                .with("userId", userId)
                .and("followerId", followerId)
                .map();

        delete("follower.id =:followerId and user.id =:userId", params);
    }

    public List<Long> findAllUsersIdsThatUserFollow(User user) {
        List<Long> usersIds = new ArrayList<>();

        PanacheQuery<Follower> query = find("follower.id", user.getId());
        if (query.list().size() > 0){
            query.list().forEach(n -> usersIds.add(n.getUser().getId()));
        }
        return usersIds;
    }

    public void deleteByUser(Long userId) {
        var params = Parameters
                .with("userId", userId)
                .map();

        delete("user.id =:userId", params);
    }
}