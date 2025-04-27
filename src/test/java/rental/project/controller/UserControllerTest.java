package rental.project.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import rental.project.dto.user.UpdateUserDataDto;
import rental.project.dto.user.UpdateUserRoleDto;
import rental.project.dto.user.UserDto;
import rental.project.model.User;
import rental.project.supplier.UserSupplier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {
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
                    new ClassPathResource("database/user/add-user.sql")
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
                    new ClassPathResource("database/user/delete-user.sql")
            );
        }
    }

    @BeforeEach
    void setupSecurityContext() {
        User customUser = new User();
        customUser.setId(3L);
        customUser.setEmail("janedoe@gmail.com");
        customUser.setFirstName("Jane");
        customUser.setLastName("Doe");
        customUser.setRole(User.Role.ADMIN);

        Authentication auth = new UsernamePasswordAuthenticationToken(customUser,
                null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    @DisplayName("Get authorized user profile data")
    void searchByUserIdAndStatus_WithValidInput_ReturnsSelectedList() throws Exception {
        //Given (Arrange)
        UserDto expected = UserSupplier.getAuthorizedDto();

        //When (Act)
        MvcResult result = mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andReturn();

        //Then (Assert)
        UserDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    @DisplayName("Update user data")
    void updateBookById_WithValidInput_ReturnsUpdatedUser() throws Exception {
        //Given (Arrange)
        UpdateUserDataDto updateRequestDto = UserSupplier.getNewUpdateUserDataDto();

        UserDto expected = UserSupplier.getUpdatedUserDto();

        String jsonRequest = objectMapper.writeValueAsString(updateRequestDto);

        //When (Act)
        MvcResult result = mockMvc.perform(put("/users/update")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then (Assert)
        UserDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    @DisplayName("Update user role")
    void updateUserRole_WithValidInput_ReturnsUpdatedUser() throws Exception {
        //Given (Arrange)
        UpdateUserRoleDto updateUserRoleDto = UserSupplier.getUpdateUserRoleDto();
        UserDto expected = UserSupplier.getNewUserDto();
        Long userId = expected.id();

        String jsonRequest = objectMapper.writeValueAsString(updateUserRoleDto);

        //When (Act)
        MvcResult result = mockMvc.perform(patch("/users/"
                        + userId + "/role")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then (Assert)
        UserDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}
