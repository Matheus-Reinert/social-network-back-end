package br.com.socialNetwork.rest.resource;

import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.user.CreateUserRequest;
import br.com.socialNetwork.rest.dto.user.ResponseError;
import br.com.socialNetwork.rest.dto.login.UpdateField;
import br.com.socialNetwork.rest.service.UserService;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserRepository repository;
    private final Validator userValidator;
    private final UserService userService;

    @Inject
    public UserResource(UserRepository repository, Validator validator, UserService service) {
        this.repository = repository;
        this.userValidator = validator;
        this.userService = service;
    }

    @POST
    @Transactional
    @Operation(summary = "Criar usuário")
    public Response createUser( CreateUserRequest userRequest){

        Set<ConstraintViolation<CreateUserRequest>> violations = userValidator.validate(userRequest);

        if(!violations.isEmpty()){
            return  ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = userService.createUser(userRequest);

        return Response.status(Response.Status.CREATED.getStatusCode()).entity(user).build();
    }

    @GET
    @Operation(summary = "Retornar todos usuários")
    public Response listAllUsers(){
        return Response.ok(repository.findAll().list()).build();
    }

    @GET
    @Path("{id}")
    @Operation(summary = "Retornar usuário")
    public Response getUserById(@PathParam("id") Long id){
        return userService.findUserById(id);
    }

    @DELETE
    @Path("{id}")
    @Operation(summary = "Remover usuário")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id){
        return userService.deleteUserById(id);
    }

    @PUT
    @Path("{id}")
    @Operation(summary = "Editar usuário")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, List<UpdateField> updateFields){
        return userService.updateUserFields(id, updateFields);
    }
}
