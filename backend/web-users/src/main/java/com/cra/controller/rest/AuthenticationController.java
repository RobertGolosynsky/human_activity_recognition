package com.cra.controller.rest;

import com.cra.domain.entity.User;
import com.cra.factory.SecurityUser;
import com.cra.factory.SecurityUserFactory;
import com.cra.model.json.request.AuthenticationRequest;
import com.cra.model.json.response.AuthenticationResponse;
import com.cra.service.TokenUtils;
import com.cra.service.interfaces.ExtendedUserDetailsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/login")
public class AuthenticationController {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Value("${cra.token.header}")
    private String tokenHeader;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private ExtendedUserDetailsService extendedUserDetailsService;

    @PostMapping
    public ResponseEntity<?> authenticationRequest(@RequestBody AuthenticationRequest authenticationRequest) throws AuthenticationException {
        // Perform the authentication
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-authentication so we can generate token
        SecurityUser userDetails = this.extendedUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        String token = this.tokenUtils.generateToken(userDetails);

        // Return the token
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

    @GetMapping(value = "/refresh")
    public ResponseEntity<?> authenticationRequest(HttpServletRequest request) {
        String token = request.getHeader(this.tokenHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String username = this.tokenUtils.getUsernameFromToken(token);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        SecurityUser user = this.extendedUserDetailsService.loadUserByUsername(username);

        String refreshedToken = this.tokenUtils.refreshToken(token);
        return ResponseEntity.ok(new AuthenticationResponse(refreshedToken));
    }

    @GetMapping(value = "/test")
    public String test(HttpServletRequest request) {
        return "Hello";
    }
}
