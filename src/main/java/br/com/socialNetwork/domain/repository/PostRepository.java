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

    public List<Post> getPostsByUsersThatUserFollow(List<Follower> peopleTheUserFollows) {
        List<Post> posts = new ArrayList<>();
        for (Follower person: peopleTheUserFollows){
            PanacheQuery<Post> query = find("user.id", Sort.by("dateTime", Sort.Direction.Descending), person.getId());
            if (query.list().size() > 0){
                for (int i = 0; i < query.list().size(); i++){
                    posts.add(query.list().get(i));
                }
            }
        }

        return posts;
    }

    public void deleteByUser(Long userId) {
        var params = Parameters
                .with("userId", userId)
                .map();

        delete("user.id =:userId", params);
    }
}
