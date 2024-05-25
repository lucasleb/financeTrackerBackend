package com.lucaslebrun.authapi.controllers;

import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.dtos.LoginUserDto;
import com.lucaslebrun.authapi.dtos.RegisterUserDto;
import com.lucaslebrun.authapi.responses.LoginResponse;
import com.lucaslebrun.authapi.services.AuthenticationService;
import com.lucaslebrun.authapi.services.DemoService;
import com.lucaslebrun.authapi.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    private final DemoService demoService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService,
            DemoService demoService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.demoService = demoService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        // if auhtenification fails, return 401
        if (authenticatedUser == null) {
            return ResponseEntity.status(401).build();
        }

        // if the authenticated user's email is "user@demo.com", populate the database
        // with dummy data
        if ("demo@user.com".equals(authenticatedUser.getEmail())) {
            demoService.initializeDemoSetup();
        }

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken)
                .setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}
