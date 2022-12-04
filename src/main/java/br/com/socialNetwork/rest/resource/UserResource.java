package br.com.socialNetwork.rest.resource;

import br.com.socialNetwork.domain.model.User;
import br.com.socialNetwork.domain.repository.UserRepository;
import br.com.socialNetwork.rest.dto.user.CreateUserRequest;
import br.com.socialNetwork.rest.dto.user.ResponseError;
import br.com.socialNetwork.rest.dto.login.UpdateField;
import br.com.socialNetwork.rest.service.PasswordService;
import br.com.socialNetwork.rest.service.TokenService;
import br.com.socialNetwork.rest.service.UserService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
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
    private final Validator validator;
    private final UserService service;

    private final TokenService tokenService;

    private final PasswordService passwordService;

    @Inject
    public UserResource(UserRepository repository, Validator validator, UserService service, TokenService tokenService, PasswordService passwordService) {
        this.repository = repository;
        this.validator = validator;
        this.service = service;
        this.tokenService = tokenService;
        this.passwordService = passwordService;
    }

    @POST
    @Transactional
    @Operation(summary = "Criar usuário")
    public Response createUser( CreateUserRequest userRequest){

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);

        if(!violations.isEmpty()){
            return  ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordService.encoder().encode(userRequest.getPassword()));
        user.setToken(tokenService.generateToken());
        user.setUsername(userRequest.getUsername());
        repository.persist(user);

        return Response
                .status(Response.Status.CREATED.getStatusCode())
                .entity(user)
                .build();
    }

    @GET
    @Operation(summary = "Retornar todos usuários")
    public Response listAllUsers(){
        PanacheQuery<User> query = repository.findAll();
        return Response.ok(query.list()).build();
    }

    @GET
    @Path("{id}")
    @Operation(summary = "Retornar usuário")
    public Response getUserById(@PathParam("id") Long id){
        User user = repository.findById(id);

        if(user != null){
            return Response.ok(user).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("{id}")
    @Operation(summary = "Remover usuário")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id){
        User user = repository.findById(id);

        if(user != null){
            repository.delete(user);
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Operation(summary = "Editar usuário")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, List<UpdateField> updateFields){

        User user = repository.findById(id);

        if(user != null){
            service.updateFieldValues(user,updateFields);
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
