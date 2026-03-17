package com.marketflow.resource;


import com.marketflow.dto.user.UpdateUserRequest;
import com.marketflow.service.UserService;
import jakarta.annotation.security.RolesAllowed;
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

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GET
    @Path("/me")
    @RolesAllowed({"BUYER", "SELLER", "ADMIN"})
    public Response me() {
        return Response.ok(userService.findMe()).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    public Response findById(@PathParam("id") UUID id) {
        return Response.ok(userService.findById(id)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"BUYER", "SELLER", "ADMIN"})
    public Response update(@PathParam("id") UUID id, @Valid UpdateUserRequest request) {
        return Response.ok(userService.update(id, request)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    public Response deactivate(@PathParam("id") UUID id) {
        userService.deactivate(id);
        return Response.noContent().build();
    }
}