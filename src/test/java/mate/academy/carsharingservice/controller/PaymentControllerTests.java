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
import java.math.BigDecimal;
import mate.academy.carsharingservice.config.TestExternalServicesConfig;
import mate.academy.carsharingservice.dto.payment.CreatePaymentRequestDto;
import mate.academy.carsharingservice.dto.payment.CreatePaymentResponseDto;
import mate.academy.carsharingservice.dto.payment.PaymentDto;
import mate.academy.carsharingservice.model.payment.PaymentType;
import mate.academy.carsharingservice.model.payment.Status;
import org.apache.commons.lang3.builder.EqualsBuilder;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestExternalServicesConfig.class)
@ActiveProfiles("test")
public class PaymentControllerTests {
    protected static MockMvc mockMvc;

    private static final Long USER_ID = 10L;
    private static final Long PAYMENT_ID_1 = 1L;
    private static final String EMAIL_CUSTOMER = "user1@example.com";
    private static final String EMAIL_MANAGER = "user2@example.com";
    private static final BigDecimal PAYMENT_AMOUNT = BigDecimal.valueOf(50.0);
    private static final String SESSION_ID = "session_11111";

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

    @WithUserDetails(value = EMAIL_CUSTOMER)
    @Test
    @DisplayName("Get payments for current customer")
    @Sql(scripts = {
            "classpath:database/cars/add-three-cars.sql",
            "classpath:database/users/add-three-users-with-roles.sql",
            "classpath:database/rentals/add-rentals-for-payments.sql",
            "classpath:database/payments/add-payments.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getPayments_whenCustomer_shouldReturnPayments() throws Exception {
        MvcResult result = mockMvc.perform(get("/payments")
                        .param("userId", USER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode content = root.get("content");

        PaymentDto[] payments = objectMapper.treeToValue(content, PaymentDto[].class);
        assertEquals(1, payments.length);

        PaymentDto expected = new PaymentDto(
                PAYMENT_ID_1, Status.PAID, SESSION_ID, PAYMENT_AMOUNT, USER_ID
        );

        assertTrue(
                EqualsBuilder.reflectionEquals(expected, payments[0], "id"),
                "Payment doesn't match expected"
        );
    }

    @WithUserDetails(value = EMAIL_MANAGER)
    @Test
    @DisplayName("Get payments by user ID for manager")
    @Sql(scripts = {
            "classpath:database/cars/add-three-cars.sql",
            "classpath:database/users/add-three-users-with-roles.sql",
            "classpath:database/rentals/add-rentals-for-payments.sql",
            "classpath:database/payments/add-payments.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getPayments_whenManagerWithUserId_shouldReturnPayments() throws Exception {
        MvcResult result = mockMvc.perform(get("/payments")
                        .param("userId", USER_ID.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode content = root.get("content");

        PaymentDto[] payments = objectMapper.treeToValue(content, PaymentDto[].class);
        assertEquals(1, payments.length);
    }

    @WithUserDetails(value = EMAIL_CUSTOMER)
    @Test
    @DisplayName("Create payment session")
    @Sql(scripts = {
            "classpath:database/cars/add-three-cars.sql",
            "classpath:database/users/add-three-users-with-roles.sql",
            "classpath:database/rentals/add-rentals-for-payments.sql",
            "classpath:database/payments/add-payments.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void makePayment_whenValidRequest_shouldCreatePaymentSession() throws Exception {
        CreatePaymentRequestDto requestDto = new CreatePaymentRequestDto(
                10L, PaymentType.PAYMENT);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/payments")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        CreatePaymentResponseDto responseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CreatePaymentResponseDto.class
        );

        assertNotNull(responseDto.sessionId());
        assertNotNull(responseDto.url());
    }

    @Test
    @DisplayName("Get payments without authentication")
    void getPayments_whenUnauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/payments"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
