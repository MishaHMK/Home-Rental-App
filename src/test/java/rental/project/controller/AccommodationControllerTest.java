package rental.project.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import rental.project.dto.accommodation.AccommodationDto;
import rental.project.dto.accommodation.CreateAccommodationDto;
import rental.project.dto.accommodation.UpdateAccommodationDto;
import rental.project.supplier.AccommodationSupplier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccommodationControllerTest {
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
                    new ClassPathResource("database/accommodation/delete-all-accommodations.sql")
            );
        }
    }

    @Test
    @Sql(
            scripts = "classpath:database/accommodation/delete-test-accommodation.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Create a new accommodation")
    void createAccommodation_ValidRequestDto_Success() throws Exception {
        //Given (Arrange)
        CreateAccommodationDto requestDto = AccommodationSupplier.getCreateDto();

        AccommodationDto accommodationDto = AccommodationSupplier.getAccommodationDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When (Act)
        MvcResult result = AccommodationControllerTest.mockMvc
                        .perform(post("/accommodations")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //Then (Assert)
        AccommodationDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), AccommodationDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(accommodationDto, actual, "id"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    @DisplayName("Get all accommodations")
    void getAll_GivenAccommodations_ShouldReturnAccommodations() throws Exception {
        //Given (Arrange)

        List<AccommodationDto> expected = AccommodationSupplier
                .getAccommodationDtoList();

        //When (Act)
        MvcResult result = mockMvc.perform(get("/accommodations"))
                .andExpect(status().isOk())
                .andReturn();

        //Then (Assert)
        AccommodationDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), AccommodationDto[].class);
        assertNotNull(actual);
        assertEquals(expected.size(), actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    @DisplayName("Update accommodation by id")
    void updateBookById_WithValidInput_ReturnsUpdatedAccommodation() throws Exception {
        //Given (Arrange)
        Long id = 2L;
        UpdateAccommodationDto updateRequestDto = AccommodationSupplier.getUpdateDto();

        AccommodationDto expected = AccommodationSupplier.getUpdatedDto();

        String jsonRequest = objectMapper.writeValueAsString(updateRequestDto);

        //When (Act)
        MvcResult result = mockMvc.perform(put("/accommodations/"
                        + id)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then (Assert)
        AccommodationDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), AccommodationDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    @DisplayName("Get accommodation by id")
    void getAccommodationById_ValidId_ShouldReturnAccommodationDto() throws Exception {
        //Given (Arrange)
        AccommodationDto expected = AccommodationSupplier.getUpdatedDto();

        //When (Act)
        MvcResult result = mockMvc.perform(get("/accommodations/"
                        + expected.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then (Assert)
        AccommodationDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), AccommodationDto.class);
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected, actual);
    }

    @Test
    @Sql(
            scripts = "classpath:database/accommodation/add-accommodation.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockUser(username = "user", roles = {"ADMIN"})
    @DisplayName("Delete accommodation by id")
    void deleteAccommodationById_WithValidId_NoContent() throws Exception {
        //Given (Arrange)
        long accommodationId = 4L;

        //When (Act)
        MvcResult result = mockMvc.perform(delete("/accommodations/" + accommodationId))
                .andExpect(status().isNoContent())
                .andReturn();

        //Then (Assert)
        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
    }
}


