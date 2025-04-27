package rental.project.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import rental.project.dto.payment.CreatePaymentDto;
import rental.project.dto.payment.PaymentDto;
import rental.project.model.User;
import rental.project.stripe.StripeUtil;
import rental.project.supplier.PaymentSupplier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentControllerTest {
    protected static MockMvc mockMvc;
    @MockBean
    private StripeUtil stripeUtil;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext webApplicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/delete-all.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/accommodation/add-three-accommodations.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/user/add-two-users.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/booking/add-two-bookings.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/payment/add-payment.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        tearDown(dataSource);
    }

    @SneakyThrows
    static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/payment/delete-all-payments.sql")
            );
        }
    }

    @BeforeEach
    void setupSecurityContext() {
        User customUser = new User();
        customUser.setId(1L);
        customUser.setEmail("janedoe@gmail.com");

        Authentication auth = new UsernamePasswordAuthenticationToken(customUser,
                null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    @DisplayName("Get all payments")
    void getPaymentsByUserId_ValidId_ShouldReturnPaymentDtoList() throws Exception {
        //Given (Arrange)
        List<PaymentDto> expected = PaymentSupplier.getPaymentDtoList();

        //When (Act)
        MvcResult result = mockMvc.perform(get("/payments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then (Assert)
        PaymentDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), PaymentDto[].class);
        assertNotNull(actual);
        assertEquals(expected, Arrays.asList(actual));
    }

    @Test
    @Sql(
            scripts = "classpath:database/payment/add-paid-payment.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/payment/delete-paid-payment.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Confirm payment")
    void confirmPayment_ValidSession_ShouldReturnPaymentDto() throws Exception {
        //Given (Arrange)
        Session session = PaymentSupplier.getCompleteSession();
        String sessionId = session.getId();
        PaymentDto expected = PaymentSupplier.getPaidPaymentDto();

        when(stripeUtil.receiveSession(sessionId))
                .thenReturn(session);

        //When (Act)
        MvcResult result = mockMvc.perform(get("/payments/success")
                        .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andReturn();

        //Then (Assert)
        PaymentDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), PaymentDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Sql(
            scripts = "classpath:database/payment/add-open-payment.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/payment/delete-open-payment.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Cancel payment")
    void cancelPayment_ValidSession_ShouldReturnPaymentDto() throws Exception {
        //Given (Arrange)
        Session session = PaymentSupplier.getNewOpenSession();
        String sessionId = session.getId();
        PaymentDto expected = PaymentSupplier.getCancelledPaymentDto();

        when(stripeUtil.receiveSession(sessionId))
                .thenReturn(session);

        //When (Act)
        MvcResult result = mockMvc.perform(get("/payments/cancel")
                        .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andReturn();

        //Then (Assert)
        PaymentDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), PaymentDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Sql(
            scripts = "classpath:database/payment/add-canceled-payment.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/payment/delete-canceled-payment.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithMockUser(username = "user", roles = {"ADMIN"})
    @DisplayName("Renew payment")
    void renewPayment_ExistingPayment_ShouldReturnPaymentDto() throws Exception {
        //Given (Arrange)
        Session session = PaymentSupplier.getSession();
        BigDecimal totalAmount = new BigDecimal("125.55");
        PaymentDto expected = PaymentSupplier.getRenewedPaymentDto();
        when(stripeUtil.createSession(totalAmount, "payment"))
                .thenReturn(session);

        //When (Act)
        MvcResult result = mockMvc.perform(patch("/payments/update/"
                         + expected.getId()))
                .andExpect(status().isOk())
                .andReturn();

        //Then (Assert)
        PaymentDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), PaymentDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Sql(
            scripts = "classpath:database/booking/add-third-booking.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/booking/remove-third-booking.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithMockUser(username = "user", roles = {"ADMIN"})
    @DisplayName("Create payment")
    void createPayment_ExistingPayment_ShouldReturnPaymentDto() throws Exception {
        //Given (Arrange)
        CreatePaymentDto createPaymentDto = PaymentSupplier.getNewCreatePaymentDto();
        BigDecimal totalAmount = new BigDecimal("269.97");
        PaymentDto expected = PaymentSupplier.getCreatedPaymentDto();
        Session session = PaymentSupplier.getSession();
        when(stripeUtil.createSession(totalAmount, "payment"))
                .thenReturn(session);

        String jsonRequest = objectMapper.writeValueAsString(createPaymentDto);

        //When (Act)
        MvcResult result = mockMvc.perform(post("/payments")
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then (Assert)
        PaymentDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), PaymentDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}
