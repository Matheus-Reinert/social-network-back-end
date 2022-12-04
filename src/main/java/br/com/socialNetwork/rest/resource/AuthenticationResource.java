package br.com.socialNetwork.rest.resource;

import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.rest.dto.login.LoginRequest;
import br.com.socialNetwork.rest.dto.login.UserAuthenticateResponse;
import br.com.socialNetwork.rest.service.UserAuthenticationService;
import org.eclipse.microprofile.openapi.annotations.Operation;

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
    @Operation(summary = "Efetuar login")
    @Transactional

    public Response login(LoginRequest loginRequest){
        User user = userAuthenticationService.authenticate(loginRequest);

        if(user == null){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        UserAuthenticateResponse userAuthenticateResponse = UserAuthenticateResponse.toResponse("Bearer ", user.getToken(), user);

        return Response
                .status(Response.Status.OK.getStatusCode())
                .entity(userAuthenticateResponse)
                .build();
    }
}
