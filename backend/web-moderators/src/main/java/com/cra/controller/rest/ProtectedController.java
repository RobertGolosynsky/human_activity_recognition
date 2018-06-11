package com.cra.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${ffriends.route.protected}")
public class ProtectedController {

    /**
     This is an example of some different kinds of granular restriction for endpoints. You can use the built-in SPEL expressions
     in @PreAuthorize such as 'hasRole()' to determine if a user has access. However, if you require logic beyond the methods
     Spring provides then you can encapsulate it in a service and register it as a bean to use it within the annotation as
     demonstrated below with 'securityService'.
     **/

    //@PreAuthorize("hasRole(UserRole.ADMIN)")
    //@PreAuthorize("@securityService.hasProtectedAccess()")
    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(T(com.cra.domain.entity.UserRole).ADMIN)")
    public ResponseEntity<?> getDaHoney() {
        return ResponseEntity.ok(":O");
    }

}
