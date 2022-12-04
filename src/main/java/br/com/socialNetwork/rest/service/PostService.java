package br.com.socialNetwork.rest.service;

import br.com.socialNetwork.domain.exception.post.*;
import br.com.socialNetwork.domain.model.Follower;
import br.com.socialNetwork.domain.model.Post;
import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.FollowerRepository;
import br.com.socialNetwork.domain.repository.PostRepository;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.post.CreatePostRequest;
import br.com.socialNetwork.rest.dto.post.PostResponse;
import br.com.socialNetwork.rest.dto.post.PostsToUserResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PostService {

    private UserRepository userRepository;
    private FollowerRepository followerRepository;
    private PostRepository postRepository;

    private UserAuthenticationService userAuthenticationService;

    public PostService(UserRepository userRepository, FollowerRepository followerRepository, PostRepository postRepository, UserAuthenticationService userAuthenticationService) {
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
        this.postRepository = postRepository;
        this.userAuthenticationService = userAuthenticationService;
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

    public Response createPost(Long userId, CreatePostRequest postRequest, String token) {

        try {
            boolean validToken = userAuthenticationService.validateToken(token);

            if (!validToken){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            User user = userRepository.findById(userId);
            if(user == null){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            createPostAndPersist(postRequest, user);
            return Response.status(Response.Status.CREATED).build();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new PostCreateException();
        }

    }

    private void createPostAndPersist(CreatePostRequest postRequest, User user) {
        Post post = new Post();
        post.setText(postRequest.getText());
        post.setUser(user);

        postRepository.persist(post);
    }

    public Response deletePost(Long userId, Long postId, String token) {
        try{
            boolean validToken = userAuthenticationService.validateToken(token);

            if (!validToken){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            User user = userRepository.findById(userId);
            if(user == null){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            Post post = postRepository.findById(postId);

            if(post == null){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            postRepository.deleteByPostAndUser(post.getId(), user.getId());

            return Response.noContent().build();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new PostDeleteException();
        }
    }

    public Response listPosts(Long userId, Long followerId, String token) {

        try {
            boolean validToken = userAuthenticationService.validateToken(token);

            if (!validToken){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            User user = userRepository.findById(userId);
            if(user == null){
                return Response.status(Response.Status.NOT_FOUND).build();
            }


            if(followerId == null){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("You forgot the header followerId")
                        .build();
            }

            User follower = userRepository.findById(followerId);

            if(follower == null){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Nonexistent followerId")
                        .build();
            }

            boolean follows = followerRepository.follows(follower, user);

            if(!follows){
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("You can't see these posts")
                        .build();
            }

            return listPostResponse(user);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ListPostException();
        }

    }

    private Response listPostResponse(User user) {

        PanacheQuery<Post> query = postRepository.find("user", Sort.by("dateTime", Sort.Direction.Descending), user);
        var list = query.list();

        var postResponseList = list.stream()
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response.ok(postResponseList).build();
    }

    public Response likePost(Long postId, String token) {
        try {
            boolean validToken = userAuthenticationService.validateToken(token);

            if (!validToken){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            Post post = postRepository.findById(postId);

            if(post == null){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            post.like();
            postRepository.persist(post);

            return Response.ok().build();

        } catch (Exception ex){
            ex.printStackTrace();
            throw new PostLikeException();
        }
    }

    public Response getPostsByUser(String token) {
        try{
            boolean validToken = userAuthenticationService.validateToken(token);

            if (!validToken){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            var postsToUserVisualize = getPostsToUserVisualize(token);

            if(postsToUserVisualize == null || postsToUserVisualize.size() == 0){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            return postsToUserVisualizeResponse(postsToUserVisualize);

        }catch (Exception ex) {
            ex.printStackTrace();
            throw new PostsToUserException();
        }
    }

    private Response postsToUserVisualizeResponse(List<Post> postsToUserVisualize) {
        PostsToUserResponse postsToUserResponse = new PostsToUserResponse();

        var postsList = postsToUserVisualize.stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());

        postsToUserResponse.setPosts(postsList);
        return Response.ok(postsToUserResponse).build();
    }
}
