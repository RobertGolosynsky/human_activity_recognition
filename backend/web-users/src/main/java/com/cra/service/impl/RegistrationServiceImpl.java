package com.cra.service.impl;

import com.cra.domain.entity.User;
import com.cra.repository.UserRepository;
import com.cra.service.interfaces.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public User registerUser(String login, String password) {
        if (userRepository.findByLoginIgnoreCase(login) != null) {
            throw new RuntimeException("Username already exists!");
        }

        final String encodedPass = passwordEncoder.encode(password);
        final User user = new User(login, encodedPass);

        return userRepository.save(user);
    }

}
