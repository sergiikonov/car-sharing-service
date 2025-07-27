package mate.academy.carsharingservice.controller;

import static mate.academy.carsharingservice.util.CarUtil.createCarDto;
import static mate.academy.carsharingservice.util.CarUtil.createCarRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import mate.academy.carsharingservice.dto.car.CarDto;
import mate.academy.carsharingservice.dto.car.CreateCarRequestDto;
import mate.academy.carsharingservice.model.car.Type;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTests {
    protected static MockMvc mockMvc;

    private static final Long ID = 1L;
    private static final String BRAND = "Toyota";
    private static final String MODEL = "Corolla";

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
    @DisplayName("Should create car with valid params and return saved CarDto")
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCar_whenRequestIsValid_shouldReturnSavedCarDto() throws Exception {
        CreateCarRequestDto request = createCarRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(request);
        MvcResult result = mockMvc.perform(post("/cars")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CarDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), CarDto.class);
        CarDto expected = createCarDto(
                ID, BRAND, MODEL, Type.SEDAN, 5, BigDecimal.valueOf(40)
        );
        assertNotNull(actual.getId());
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id"),
                "Expected and actual CarDto are not equal"
        );
    }

    @WithMockUser(username = "customer", roles = "CUSTOMER")
    @Test
    @DisplayName("Should return page of cars")
    @Sql(scripts = "classpath:database/cars/add-three-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAll_whenCalled_shouldReturnPageOfCars() throws Exception {
        MvcResult result = mockMvc.perform(get("/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode content = root.get("content");

        CarDto[] actual = objectMapper.readValue(content.toString(), CarDto[].class);
        assertEquals(3, actual.length);

        List<CarDto> expected = List.of(
                createCarDto(ID, BRAND, MODEL, Type.SEDAN, 5, BigDecimal.valueOf(40.0)),
                createCarDto(2L, "Tesla", "Model 3", Type.SUV, 10, BigDecimal.valueOf(100.0)),
                createCarDto(3L, "Fiat", "Panda", Type.HATCHBACK, 0, BigDecimal.valueOf(25.0))
        );

        for (int i = 0; i < expected.size(); i++) {
            assertTrue(
                    EqualsBuilder.reflectionEquals(expected.get(i), actual[i], "id"),
                    "Car at index " + i + " does not match"
            );
        }
    }

    @WithMockUser(username = "customer", roles = "CUSTOMER")
    @Test
    @DisplayName("Should return CarDto for existing ID")
    @Sql(scripts = "classpath:database/cars/add-three-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCarById_whenIdExists_shouldReturnCarDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/cars/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), CarDto.class);

        CarDto expected = createCarDto(
                ID, BRAND, MODEL, Type.SEDAN, 5, BigDecimal.valueOf(40.0)
        );

        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "dailyFee"),
                "Expected and actual CarDto are not equal"
        );
    }

    @WithMockUser(username = "customer", roles = "CUSTOMER")
    @Test
    @DisplayName("Should throw 404 status for invalid ID")
    @Sql(scripts = "classpath:database/cars/add-three-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBookById_whenInvalidId_shouldReturnNotFoundStatus() throws Exception {
        mockMvc.perform(get("/cars/995")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
