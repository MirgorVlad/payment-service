package org.mirgor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mirgor.dto.AuthRequest;
import org.mirgor.dto.AuthResponse;
import org.mirgor.dto.UserDto;
import org.mirgor.security.jwt.JwtService;
import org.mirgor.security.principal.SecurityUser;
import org.mirgor.service.UserService;
import org.mirgor.service.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final UserMapper mapper;

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody @Valid AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.email(),
                        authRequest.password()
                )
        );
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> registerUser(@RequestBody @Valid UserDto userDto) {
        var user = mapper.fromDto(userDto);
        var createdUser = userService.createUser(user);
        return new ResponseEntity<>(mapper.toDto(createdUser), HttpStatus.CREATED);
    }
}
