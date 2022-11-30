package br.com.socialNetwork.rest.service;

import br.com.socialNetwork.domain.model.Comment;
import br.com.socialNetwork.domain.model.Post;
import br.com.socialNetwork.domain.repository.CommentRepository;
import br.com.socialNetwork.domain.repository.UserRepository;
import com.google.gson.Gson;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CommentService {
    CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository){
        this.commentRepository = commentRepository;
    }


    public void removeChildComments(Comment comment) {
        List<Comment> commentChilds = commentRepository.findByParentId(comment.getId());

        if (!commentChilds.isEmpty()){
            for (Comment commentChild: commentChilds ){
                commentRepository.delete(commentChild);
            }
        }
    }

    public List<Comment> getComments(Post post){
        return commentRepository.findCommentsByPost(post);
    }

    public String getCommentChilds(Long commentId) {
        List<Comment> commentChilds = commentRepository.findByParentId(commentId);
        return new Gson().toJson(commentChilds);
    }
}
