package mate.academy.carsharingservice.controller;

import static mate.academy.carsharingservice.util.UserUtil.createUserResponseDto;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.carsharingservice.config.TestExternalServicesConfig;
import mate.academy.carsharingservice.dto.user.UpdateUserRoleRequestDto;
import mate.academy.carsharingservice.dto.user.UserResponseDto;
import mate.academy.carsharingservice.model.user.RoleName;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestExternalServicesConfig.class)
@ActiveProfiles("test")
public class UserControllerTests {
    protected static MockMvc mockMvc;

    private static final String EMAIL = "user1@example.com";

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

    @WithMockUser(username = "manager", roles = "MANAGER")
    @Test
    @DisplayName("Should update user's role and return updated UserResponseDto")
    @Sql(scripts = "classpath:database/users/add-three-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateUserRole_whenUserIdAndRequestIsValid_shouldReturnUpdatedUserResponseDto()
            throws Exception {
        UpdateUserRoleRequestDto requestDto = new UpdateUserRoleRequestDto();
        requestDto.setRole(RoleName.ROLE_CUSTOMER);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(put("/users/10/role")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto expected = new UserResponseDto();
        expected.setId(10L);
        expected.setFirstName("First");
        expected.setLastName("LastOne");
        expected.setEmail("user1@example.com");

        UserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class
        );
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual),
                "Expected and actual UserResponseDto are not equal"
        );
    }

    @WithUserDetails(value = EMAIL)
    @Test
    @DisplayName("Should show user's profile with his info")
    @Sql(scripts = "classpath:database/users/add-three-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCurrentUserProfile_whenUserLogged_shouldReturnUserResponseDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class
        );
        UserResponseDto expected = createUserResponseDto();

        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id"),
                "Expected and actual UserResponseDto are not equal"
        );
    }
}
