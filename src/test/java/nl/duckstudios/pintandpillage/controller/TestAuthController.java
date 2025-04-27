package nl.duckstudios.pintandpillage.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import nl.duckstudios.pintandpillage.Exceptions.UnmetEmailRequirementsException;
import nl.duckstudios.pintandpillage.Exceptions.UnmetPasswordRequirementsException;
import nl.duckstudios.pintandpillage.Exceptions.UserAlreadyExistsException;
import nl.duckstudios.pintandpillage.config.JwtTokenUtil;
import nl.duckstudios.pintandpillage.dao.UserDAO;
import nl.duckstudios.pintandpillage.entity.User;
import nl.duckstudios.pintandpillage.model.LoginCredentials;
import nl.duckstudios.pintandpillage.service.UserService;
import nl.duckstudios.pintandpillage.testHelpers.RequestHelper;
import nl.duckstudios.pintandpillage.mocks.MockUserRequest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("AuthController")
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TestAuthController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext context;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private AuthenticationManager authManager;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserDAO userDAO;

    private RequestHelper requestHelper = new RequestHelper();

    ObjectWriter ow = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY).writer().withDefaultPrettyPrinter();

    // Arrange for Registration
    private MockUserRequest validMockUserRequest = requestHelper.generateUser("test@example.com", "TestUsername1", "Test1234!");
    private MockUserRequest noEmailMockUserRequest = requestHelper.generateUser("", "TestUsername1", "Test1234!");
    private MockUserRequest noPasswordMockUserRequest = requestHelper.generateUser("test@example.com", "TestUsername1", "");
    private MockUserRequest noUsernameMockUserRequest = requestHelper.generateUser("test@example.com", "", "Test1234!");
    private User genericUser = new User("test@example.com", "TestUsername1", "123awdawdaA@");

    // Arrange for login
    private LoginCredentials validLoginCredentials = new LoginCredentials("test@example.com", "Test1234!");

    @Test
    public void shouldThrowUnmetEmailRequirementsExceptionWhenTheEmailIsInvalid() throws Exception {
        when(this.passwordEncoder.encode(any())).thenReturn(noEmailMockUserRequest.getPassword());
        when(this.userDAO.findByEmail(any())).thenReturn(Optional.empty());
        String json = ow.writeValueAsString(noEmailMockUserRequest);
        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UnmetEmailRequirementsException));
    }

    @Test
    public void shouldThrowUnmetPasswordRequirementsExceptionWhenThePasswordIsInvalid() throws Exception {
        when(this.passwordEncoder.encode(any())).thenReturn(noPasswordMockUserRequest.getPassword());
        when(this.userDAO.findByEmail(any())).thenReturn(Optional.empty());
        String json = ow.writeValueAsString(noPasswordMockUserRequest);
        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UnmetPasswordRequirementsException));
    }

    @Test
    public void shouldReturnATokenWhenNoUsernameIsGiven() throws Exception {
        when(this.passwordEncoder.encode(any())).thenReturn(noUsernameMockUserRequest.getPassword());
        when(this.userDAO.findByEmail(any())).thenReturn(Optional.empty());
        when(this.userDAO.save(any())).thenReturn(genericUser);
        String json = ow.writeValueAsString(noUsernameMockUserRequest);
        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("token")));
    }

    @Test
    public void shouldThrowUserAlreadyExistsExceptionWhenAUserWithTheSameEmailIsPresent() throws Exception {
        when(this.passwordEncoder.encode(any())).thenReturn(validMockUserRequest.getPassword());
        when(this.userDAO.findByEmail(any())).thenReturn(Optional.of(new User(validMockUserRequest.getEmail(), validMockUserRequest.getUsername(), validMockUserRequest.getPassword())));
        String json = ow.writeValueAsString(validMockUserRequest);
        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserAlreadyExistsException));
    }

    // LOGIN TESTS
    @Test
    public void shouldThrowForbiddenExceptionWhenAuthmanagerDoesntAuthenticate() throws Exception{
        // no NO-Args constructor for AuthenticationException
        when(this.authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new AuthenticationException(""){});
        String json = ow.writeValueAsString(validLoginCredentials);
        this.mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldHaveAJWTAndFirstTimeLoggedInSetToTrueWhenTheUserLogsInForTheFirstTime() throws Exception {
        when(this.authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(this.jwtTokenUtil.generateToken(anyString())).thenReturn("generatedToken");
        when(this.userDAO.findByEmail(any())).thenReturn(Optional.of(new User(validMockUserRequest.getEmail(), validMockUserRequest.getUsername(), validMockUserRequest.getPassword())));
        String json = ow.writeValueAsString(validLoginCredentials);
        this.mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("generatedToken")))
                .andExpect(content().string(containsString("isFirstTimeLoggedIn\":true")));
    }

    @Test
    public void shouldHaveAJWTAndFirstTimeLoggedInSetToFalseWhenTheUserLogsInForTheSecondTime() throws Exception {
        User secondTimeLoggedInUser = new User(validMockUserRequest.getEmail(), validMockUserRequest.getUsername(), validMockUserRequest.getPassword());
        secondTimeLoggedInUser.setFirstTimeLoggedIn(false);
        when(this.authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(this.jwtTokenUtil.generateToken(anyString())).thenReturn("generatedToken");
        when(this.userDAO.findByEmail(any())).thenReturn(Optional.of(secondTimeLoggedInUser));
        String json = ow.writeValueAsString(validLoginCredentials);
        this.mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("generatedToken")))
                .andExpect(content().string(containsString("isFirstTimeLoggedIn\":false")));
    }
}
