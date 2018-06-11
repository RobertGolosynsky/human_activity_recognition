package com.ffriends.controller.rest;

import com.ffriends.model.json.request.AuthenticationRequest;
import com.ffriends.model.json.response.AuthenticationResponse;
import com.ffriends.service.TokenUtils;
import com.ffriends.service.interfaces.ExtendedUserDetailsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${ffriends.route.authentication}")
public class AuthenticationController {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Value("${ffriends.token.header}")
    private String tokenHeader;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private ExtendedUserDetailsService extendedUserDetailsService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> authenticationRequest(@RequestBody AuthenticationRequest authenticationRequest, Device device) throws AuthenticationException {
        // Perform the authentication
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-authentication so we can generate token
        TokenUtils.ExtendedUserDetails userDetails = this.extendedUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        String token = this.tokenUtils.generateToken(userDetails, device);

        // Return the token
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

    @RequestMapping(value = "${ffriends.route.authentication.refresh}", method = RequestMethod.GET)
    public ResponseEntity<?> authenticationRequest(HttpServletRequest request) {
        String token = request.getHeader(this.tokenHeader);
        if (token == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String username = this.tokenUtils.getUsernameFromToken(token);
        if (username == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        TokenUtils.ExtendedUserDetails user = this.extendedUserDetailsService.loadUserByUsername(username);

        if (this.tokenUtils.canTokenBeRefreshed(token, user.getLastPasswordReset())) {
            String refreshedToken = this.tokenUtils.refreshToken(token);
            return ResponseEntity.ok(new AuthenticationResponse(refreshedToken));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
