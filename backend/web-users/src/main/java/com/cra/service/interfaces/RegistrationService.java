package com.cra.service.interfaces;

import com.cra.domain.entity.User;

public interface RegistrationService {

    User registerUser(String login, String password);

}
