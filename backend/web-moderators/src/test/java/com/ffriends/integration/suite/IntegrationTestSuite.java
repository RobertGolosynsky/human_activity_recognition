package com.ffriends.integration.suite;

import com.ffriends.integration.controller.rest.AuthenticationControllerTest;
import com.ffriends.integration.controller.rest.ProtectedControllerTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AuthenticationControllerTest.class,
        ProtectedControllerTest.class
})
public class IntegrationTestSuite {

}
