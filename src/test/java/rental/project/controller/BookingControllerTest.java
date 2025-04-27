package rental.project.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import rental.project.dto.booking.BookingDto;
import rental.project.dto.booking.BookingWithAccommodationInfoDto;
import rental.project.dto.booking.CreateBookingDto;
import rental.project.dto.booking.UpdateBookingDto;
import rental.project.dto.booking.UpdateBookingStatusDto;
import rental.project.model.User;
import rental.project.supplier.BookingSupplier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingControllerTest {
    protected static MockMvc mockMvc;

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
        tearDown(dataSource);
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
                    new ClassPathResource("database/booking/delete-all-bookings.sql")
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
    @Sql(
            scripts = "classpath:database/booking/delete-test-booking.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Create a new booking")
    void createBooking_ValidRequestDto_Success() throws Exception {
        //Given (Arrange)
        CreateBookingDto requestDto = BookingSupplier.getNewBookingCreateDto();

        BookingDto bookingDto = BookingSupplier.getCreatedBookingDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When (Act)
        MvcResult result = BookingControllerTest.mockMvc
                .perform(post("/bookings")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //Then (Assert)
        BookingDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookingDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(bookingDto, actual, "id"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    @DisplayName("Get bookings by parameters")
    void searchByUserIdAndStatus_WithValidInput_ReturnsSelectedList() throws Exception {
        //Given (Arrange)
        List<BookingDto> list = BookingSupplier.getBookingDtoList();
        Long userId = 1L;

        List<BookingDto> expected = list.stream()
                .filter(b -> b.getUserId().equals(userId))
                .toList();

        //When (Act)
        MvcResult result = mockMvc.perform(get("/bookings/search")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then (Assert)
        BookingDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookingDto[].class);

        assertNotNull(actual);
        assertEquals(expected, Arrays.asList(actual));
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    @DisplayName("Get bookings for user")
    void getAllMyBookings_WithValidInput_ReturnsSelectedList() throws Exception {
        //Given (Arrange)
        List<BookingDto> list = BookingSupplier.getBookingDtoList();
        Long userId = 1L;

        List<BookingDto> expected = list.stream()
                .filter(b -> b.getUserId().equals(userId))
                .toList();

        //When (Act)
        MvcResult result = mockMvc.perform(get("/bookings/my"))
                .andExpect(status().isOk())
                .andReturn();

        //Then (Assert)
        BookingDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookingDto[].class);

        assertNotNull(actual);
        assertEquals(expected, Arrays.asList(actual));
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    @DisplayName("Get detailed booking by id")
    void getById_ValidId_ShouldReturnBookDto() throws Exception {
        //Given (Arrange)
        BookingWithAccommodationInfoDto expected = BookingSupplier
                .getModifiedBookingWithAccommodationInfoDto();

        //When (Act)
        MvcResult result = mockMvc.perform(get("/bookings/" + expected.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then (Assert)
        BookingWithAccommodationInfoDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), BookingWithAccommodationInfoDto.class);
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    @DisplayName("Update booking by id")
    void updateBookById_WithValidInput_ReturnsUpdatedBook() throws Exception {
        //Given (Arrange)
        UpdateBookingDto updateRequestDto = BookingSupplier.getUpdateBookingDto();

        BookingDto expected = BookingSupplier.getBookingDto();

        String jsonRequest = objectMapper.writeValueAsString(updateRequestDto);

        //When (Act)
        MvcResult result = mockMvc.perform(put("/bookings/" + expected.getId())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then (Assert)
        BookingDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookingDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    @DisplayName("Update booking status by id")
    void updateBookingStatus_WithValidInput_ReturnsUpdatedBook() throws Exception {
        //Given (Arrange)
        UpdateBookingStatusDto updateRequestDto = BookingSupplier.getUpdateBookingStatusDto();

        BookingDto expected = BookingSupplier.getBookingDto();

        String jsonRequest = objectMapper.writeValueAsString(updateRequestDto);

        //When (Act)
        MvcResult result = mockMvc.perform(patch("/bookings/update-status/"
                        + expected.getId())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then (Assert)
        BookingDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookingDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Sql(
            scripts = "classpath:database/booking/add-booking-todelete.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/booking/delete-cancelled-booking.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithMockUser(username = "user", roles = {"ADMIN"})
    @DisplayName("Set booking as canceled by id")
    void cancelBooking_WithValidId_NoContent() throws Exception {
        //Given (Arrange)
        long bookId = 4L;
        BookingDto expected = BookingSupplier.getCancelledBookingDto();

        //When (Act)
        MvcResult result = mockMvc.perform(delete("/bookings/" + bookId))
                .andExpect(status().isOk())
                .andReturn();

        //Then (Assert)
        BookingDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookingDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}
