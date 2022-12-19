package nl.duckstudios.pintandpillage.testHelpers;

import nl.duckstudios.pintandpillage.mocks.MockUserRequest;

public class RequestHelper {

    public MockUserRequest generateUser(String email, String username, String password) {
        MockUserRequest generatedUser = new MockUserRequest(email, username, password);
        return generatedUser;
    }
}