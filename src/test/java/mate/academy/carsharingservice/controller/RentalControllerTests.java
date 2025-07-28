package mate.academy.carsharingservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import mate.academy.carsharingservice.config.TestExternalServicesConfig;
import mate.academy.carsharingservice.dto.rental.RentalRequestDto;
import mate.academy.carsharingservice.dto.rental.RentalResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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
public class RentalControllerTests {
    protected static MockMvc mockMvc;

    private static final Long USER_ID = 10L;
    private static final Long CAR_ID = 1L;
    private static final String CAR_MODEL = "Corolla";
    private static final String EMAIL = "user1@example.com";
    private static final String EMAIL_MANAGER = "user2@example.com";
    private static final LocalDate RENTAL_DATE = LocalDate.now().plusDays(1);
    private static final LocalDate RETURN_DATE = RENTAL_DATE.plusDays(1);
    private static final Boolean IS_ACTIVE = true;

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

    @WithUserDetails(value = EMAIL)
    @Test
    @DisplayName("Should create new rental for user")
    @Sql(scripts = {
            "classpath:database/cars/add-three-cars.sql",
            "classpath:database/users/add-three-users-with-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createRental_whenRentalRequestIsCorrect_shouldReturnRentalResponseDto() throws Exception {
        var requestDto = new RentalRequestDto(
                CAR_ID, RENTAL_DATE, RETURN_DATE);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/rentals")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), RentalResponseDto.class);
        var expected = new RentalResponseDto(
                1L, CAR_ID, CAR_MODEL, USER_ID,
                EMAIL, RENTAL_DATE,
                RETURN_DATE, null
        );

        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id"),
                "Expected and actual RentalResponseDto are not equal"
        );
    }

    @WithUserDetails(value = EMAIL)
    @Test
    @DisplayName("Should return user's active rentals")
    @Sql(scripts = {
            "classpath:database/cars/add-three-cars.sql",
            "classpath:database/users/add-three-users-with-roles.sql",
            "classpath:database/rentals/add-rentals-active.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getRentals_whenCustomer_shouldReturnHisRentals() throws Exception {
        MvcResult result = mockMvc.perform(get("/rentals")
                        .param("userId", USER_ID.toString())
                        .param("isActive", IS_ACTIVE.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode content = root.get("content");

        RentalResponseDto[] actual = objectMapper
                .readValue(content.toString(), RentalResponseDto[].class);
        assertEquals(1, actual.length);

        var expected = new RentalResponseDto(
                301L, CAR_ID, CAR_MODEL,
                USER_ID, EMAIL,
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(5), null
        );
        assertEquals(1, Arrays.stream(actual).count(),
                "Incorrect number of rentals for the customer");

        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual[0], "id"),
                "Rental's doesn't match"
        );
    }

    @WithUserDetails(value = EMAIL_MANAGER)
    @Test
    @DisplayName("Should return all active rentals for manager")
    @Sql(scripts = {
            "classpath:database/cars/add-three-cars.sql",
            "classpath:database/users/add-three-users-with-roles.sql",
            "classpath:database/rentals/add-rentals-active.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getRentals_whenManager_shouldReturnAllRentals() throws Exception {
        MvcResult result = mockMvc.perform(get("/rentals")
                        .param("isActive", IS_ACTIVE.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode content = root.get("content");

        RentalResponseDto[] actual = objectMapper
                .readValue(content.toString(), RentalResponseDto[].class);
        assertEquals(3, actual.length);

        List<RentalResponseDto> expected = List.of(
                new RentalResponseDto(
                301L, CAR_ID, CAR_MODEL,
                USER_ID, EMAIL,
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(5), null
                ),
                new RentalResponseDto(
                        302L, CAR_ID, CAR_MODEL,
                        20L, EMAIL_MANAGER,
                        LocalDate.now().minusDays(1),
                        LocalDate.now().plusDays(3), null
                ),
                new RentalResponseDto(
                        303L, 2L, "Model 3",
                        30L, "user3@example.com",
                        LocalDate.now().minusDays(3),
                        LocalDate.now().plusDays(2), null
                )
        );
        assertEquals(3, Arrays.stream(actual).count(),
                "Incorrect number of rentals for the customer");

        for (int i = 0; i < expected.size(); i++) {
            assertTrue(
                    EqualsBuilder.reflectionEquals(expected.get(i), actual[i], "id"),
                    "Rentals at index " + i + " does not match"
            );
        }
    }

    @WithUserDetails(value = EMAIL)
    @Test
    @DisplayName("Should return car and actual return date should be today")
    @Sql(scripts = {
            "classpath:database/cars/add-three-cars.sql",
            "classpath:database/users/add-three-users-with-roles.sql",
            "classpath:database/rentals/add-rentals-active.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void returnCar_whenRentalIsActive_shouldReturnCar() throws Exception {
        MvcResult result = mockMvc.perform(post("/rentals/301/return")
                        .pathInfo("/301")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), RentalResponseDto.class);
        var expected = new RentalResponseDto(
                301L, CAR_ID, CAR_MODEL, USER_ID,
                EMAIL, LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(5), LocalDate.now()
        );

        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id"),
                "Expected and actual RentalResponseDto are not equal"
        );
    }
}
