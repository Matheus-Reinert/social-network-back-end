package br.com.socialNetwork.rest.service;

import br.com.socialNetwork.domain.exception.follower.FollowUserException;
import br.com.socialNetwork.domain.exception.follower.UnfollowUserException;
import br.com.socialNetwork.domain.model.Follower;
import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.FollowerRepository;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.follower.FollowerRequest;
import br.com.socialNetwork.rest.dto.follower.FollowerResponse;
import br.com.socialNetwork.rest.dto.follower.FollowersPerUserResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@ApplicationScoped
public class FollowerService {

    private FollowerRepository followerRepository;
    private UserRepository userRepository;
    private UserAuthenticationService userAuthenticationService;

    public FollowerService(FollowerRepository followerRepository, UserRepository userRepository, UserAuthenticationService userAuthenticationService) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
        this.userAuthenticationService = userAuthenticationService;
    }

    public Response followUser(Long userId, FollowerRequest followerRequest, String token) {
        try {
            boolean validToken = userAuthenticationService.validateToken(token);

            if (!validToken){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            
            if(userId.equals(followerRequest.getFollowerId())){
                return Response.status(Response.Status.CONFLICT)
                        .entity("You can't follow yourself").build();
            }

            var user = userRepository.findById(userId);
            if(user == null){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            var follower = userRepository.findById(followerRequest.getFollowerId());
            boolean follows = followerRepository.follows(follower, user);

            if(!follows){
                createFollower(user, follower);
            }

            return Response.status(Response.Status.NO_CONTENT).build();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new FollowUserException();
        }
    }

    private void createFollower(User user, User follower) {
        var entity = new Follower();
        entity.setUser(user);
        entity.setFollower(follower);
        followerRepository.persist(entity);
    }

    public Response listFollower(Long userId, String token) {

        boolean validToken = userAuthenticationService.validateToken(token);

        if (!validToken){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        var user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return followerListResponse(userId);
    }

    private Response followerListResponse(Long userId) {
        var list = followerRepository.findByUser(userId);

        FollowersPerUserResponse followersPerUserResponse = new FollowersPerUserResponse();
        followersPerUserResponse.setFollowerCount(list.size());

        var followerList = list.stream()
                .map(FollowerResponse::new)
                .collect(Collectors.toList());

        followersPerUserResponse.setContent(followerList);
        return Response.ok(followersPerUserResponse).build();
    }

    public Response unfollowUser(Long userId, Long followerId, String token) {
        try {

            boolean validToken = userAuthenticationService.validateToken(token);

            if (!validToken){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            var user = userRepository.findById(userId);
            if(user == null){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            followerRepository.deleteByFollowerAndUser(followerId, userId);

            return Response.status(Response.Status.NO_CONTENT).build();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new UnfollowUserException();
        }
    }
}
