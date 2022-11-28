package br.com.socialNetwork.rest.resource;

import br.com.socialNetwork.domain.model.Post;
import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.rest.dto.CreatePostRequest;
import br.com.socialNetwork.rest.dto.LoginRequest;
import br.com.socialNetwork.rest.dto.UserAuthenticateResponse;
import br.com.socialNetwork.rest.service.UserAuthenticationService;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationResource {

    private UserAuthenticationService userAuthenticationService;

    public AuthenticationResource(UserAuthenticationService userAuthenticationService){
        this.userAuthenticationService = userAuthenticationService;
    }

    @POST
    @Transactional
//    public Response login(LoginRequest loginRequest, @HeaderParam("authorization") String authorization){

    public Response login(LoginRequest loginRequest){
        User user = userAuthenticationService.authenticate(loginRequest);

        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        UserAuthenticateResponse userAuthenticateResponse = UserAuthenticateResponse.toResponse("Bearer ", user.getToken());

//        return Response.ok().build();

        return Response
                .status(Response.Status.OK.getStatusCode())
                .entity(userAuthenticateResponse)
                .build();
    }
}
