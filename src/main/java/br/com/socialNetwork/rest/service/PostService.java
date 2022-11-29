package br.com.socialNetwork.rest.service;

import br.com.socialNetwork.domain.model.Follower;
import br.com.socialNetwork.domain.model.Post;
import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.FollowerRepository;
import br.com.socialNetwork.domain.repository.PostRepository;
import br.com.socialNetwork.domain.repository.UserRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PostService {

    private UserRepository userRepository;
    private FollowerRepository followerRepository;
    private PostRepository postRepository;

    public PostService(UserRepository userRepository, FollowerRepository followerRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
        this.postRepository = postRepository;
    }

    public List<Post> getPostsToUserVisualize(String token) {
        String treatedToken = token.replace("Bearer ", "");
        List<Post> posts = new ArrayList<>();

        User user = userRepository.findUserByToken(treatedToken);

        if (user == null){
            return posts;
        }

        List<Follower> peopleTheUserFollows = followerRepository.findAllUsersThatUserFollow(user);

        if(peopleTheUserFollows.size() == 0){
            return posts;
        }

        posts = postRepository.getPostsByUsersThatUserFollow(peopleTheUserFollows);

        return posts;
    }
}
