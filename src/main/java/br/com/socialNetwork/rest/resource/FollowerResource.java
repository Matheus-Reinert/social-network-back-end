package br.com.socialNetwork.rest.resource;

import br.com.socialNetwork.domain.model.Follower;
import br.com.socialNetwork.domain.repository.FollowerRepository;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.follower.FollowerRequest;
import br.com.socialNetwork.rest.dto.follower.FollowerResponse;
import br.com.socialNetwork.rest.dto.follower.FollowersPerUserResponse;
import br.com.socialNetwork.rest.service.FollowerService;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private FollowerService followerService;

    @Inject
    public FollowerResource(FollowerService followerService){
        this.followerService = followerService;
    }

    @PUT
    @Transactional
    @Operation(summary = "Seguir usuário")
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest followerRequest, @HeaderParam("Authorization") String token){
        return followerService.followUser(userId, followerRequest, token);
    }

    @GET
    @Operation(summary = "Retornar todos seguidores")
    public Response listFollowers(@PathParam("userId") Long userId, @HeaderParam("Authorization") String token){
        return followerService.listFollower(userId, token);
    }

    @DELETE
    @Operation(summary = "Deixar de seguir usuário")
    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId,@QueryParam("followerId") Long followerId,
            @HeaderParam("Authorization") String token) {
        return followerService.unfollowUser(userId, followerId, token);
    }
}