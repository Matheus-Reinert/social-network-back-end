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

@ApplicationScoped
public class PostRepository implements PanacheRepository<Post> {

    public void deleteByPostAndUser(Long postId, Long userId) {
        var params = Parameters
                .with("userId", userId)
                .and("postId", postId)
                .map();

        delete("id =:postId and user.id =:userId", params);
    }

    public List<Post> getPostsByUsersThatUserFollow(List<Long> peopleTheUserFollowsIds) {
        PanacheQuery<Post> query = find("user.id in ?1", Sort.by("dateTime", Sort.Direction.Descending), peopleTheUserFollowsIds);
        return query.list();
    }

    public void deleteByUser(Long userId) {
        var params = Parameters
                .with("userId", userId)
                .map();

        delete("user.id =:userId", params);
    }
}
