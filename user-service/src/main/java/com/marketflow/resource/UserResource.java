package com.marketflow.resource;


import com.marketflow.dto.user.UpdateUserRequest;
import com.marketflow.dto.user.UserResponse;
import com.marketflow.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/api/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserService userService;

    @Inject
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GET
    @Path("/me")
    @RolesAllowed({"BUYER", "SELLER", "ADMIN"})
    public Response me() {
        UserResponse response = new UserResponse(userService.findMe());
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    public Response findById(@PathParam("id") UUID id) {
        UserResponse response = new UserResponse(userService.findById(id));
        return Response.ok(response).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"BUYER", "SELLER", "ADMIN"})
    public Response update(@PathParam("id") UUID id, @Valid UpdateUserRequest request) {
        UserResponse response = new UserResponse(userService.update(id, request));
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    public Response deactivate(@PathParam("id") UUID id) {
        userService.deactivate(id);
        return Response.noContent().build();
    }
}