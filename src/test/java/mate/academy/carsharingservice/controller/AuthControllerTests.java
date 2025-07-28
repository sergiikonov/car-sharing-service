package mate.academy.carsharingservice.controller;

import static mate.academy.carsharingservice.util.UserUtil.createValidRequest;
import static mate.academy.carsharingservice.util.UserUtil.createValidUserResponseDto;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.carsharingservice.config.TestExternalServicesConfig;
import mate.academy.carsharingservice.dto.user.UserLoginRequestDto;
import mate.academy.carsharingservice.dto.user.UserLoginResponseDto;
import mate.academy.carsharingservice.dto.user.UserRegistrationRequestDto;
import mate.academy.carsharingservice.dto.user.UserResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestExternalServicesConfig.class)
public class AuthControllerTests {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Should register user")
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void register_whenCorrectUserRequest_shouldReturnUserResponseDto() throws Exception {
        UserRegistrationRequestDto request = createValidRequest();
        String jsonRequest = objectMapper.writeValueAsString(request);
        MvcResult result = mockMvc.perform(post("/auth/registration")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        var actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class
        );

        var expected = createValidUserResponseDto();
        assertNotNull(actual.getId());
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id"),
                "Expected and actual UserResponseDto are not equal"
        );
    }

    @Test
    @DisplayName("Should login user with correct credentials")
    @Sql(scripts = "classpath:database/users/add-three-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void login_whenUserLoginRequestDtoCorrect_shouldReturnUserLoginResponseDto() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto(
                "user1@example.com", "12345");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        post("/auth/login")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserLoginResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserLoginResponseDto.class
        );
        assertNotNull(actual);
    }

    @Test
    @DisplayName("Should return 500 with invalid credentials")
    @Sql(scripts = "classpath:database/users/add-three-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void login_whenUserLoginRequestDtoInvalid_shouldReturnInternalServerError() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto(
                "user1@example.com", "wrong_password");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(post("/auth/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
