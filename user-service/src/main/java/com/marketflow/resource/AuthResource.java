package com.marketflow.resource;

import com.marketflow.dto.auth.LoginRequest;
import com.marketflow.dto.auth.RefreshRequest;
import com.marketflow.dto.auth.RegisterRequest;
import com.marketflow.dto.auth.TokenResponse;
import com.marketflow.service.AuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class AuthResource {

    private final AuthService authService;

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Path("/register")
    public Response register(@Valid RegisterRequest request) {
        TokenResponse token = authService.register(request);
        return Response.status(Response.Status.CREATED).entity(token).build();
    }

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        TokenResponse token = authService.login(request);
        return Response.ok(token).build();
    }

    @POST
    @Path("/refresh")
    public Response refresh(@Valid RefreshRequest request) {
        TokenResponse token = authService.refresh(request);
        return Response.ok(token).build();
    }
}