package com.cra.controller.rest;

import com.cra.domain.entity.Model;
import com.cra.domain.entity.User;
import com.cra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/models")
public class ModelController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/list")
    public @ResponseBody
    List<Model> getModels(Principal principal) {
        final User user = userRepository.findByEmail(principal.getName());

        return user.getModels();
    }

}
