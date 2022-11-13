package br.com.socialNetwork.domain.repository;

import br.com.socialNetwork.domain.model.Comment;
import br.com.socialNetwork.domain.model.Follower;
import br.com.socialNetwork.domain.model.Post;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CommentRepository implements PanacheRepository<Comment> {
    public List<Comment> findCommentsByPost(Post post) {
        var params = Parameters.with("post", post).map();
        PanacheQuery<Comment> query = find("post =: post and commentParent_id is null", params);
        return query.list();
    }

    public List<Comment> findByParentId(Long commentParentId) {
        PanacheQuery<Comment> query = find("commentParentId", Sort.by("dateTime", Sort.Direction.Descending), commentParentId);
        return query.list();
    }
}