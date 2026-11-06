package co.com.crediya.api;

import co.com.crediya.api.exception.GlobalExceptionHandler;
import co.com.crediya.model.exceptions.EmailAlreadyRegisteredException;
import co.com.crediya.model.exceptions.InvalidUserDataException;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private static final String URL = "/api/v1/usuarios";
    private static final String VALID_BODY = """
            {
              "nombres": "Ana",
              "apellidos": "Gomez",
              "fecha_nacimiento": "1995-05-10",
              "direccion": "Calle 1",
              "telefono": "3001234567",
              "correo_electronico": "ana@mail.com",
              "salario_base": 2500000
            }
            """;

    private UserUseCase userUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userUseCase = mock(UserUseCase.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn201WhenUserIsRegistered() throws Exception {
        when(userUseCase.registerUser(any(User.class))).thenReturn(User.builder()
                .id("abc-123")
                .nombres("Ana")
                .apellidos("Gomez")
                .fechaNacimiento(LocalDate.of(1995, 5, 10))
                .correoElectronico("ana@mail.com")
                .salarioBase(new BigDecimal("2500000"))
                .build());

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("abc-123"))
                .andExpect(jsonPath("$.correo_electronico").value("ana@mail.com"))
                .andExpect(jsonPath("$.fecha_nacimiento").value("1995-05-10"));
    }

    @Test
    void shouldReturn400WithErrorListWhenDataIsInvalid() throws Exception {
        when(userUseCase.registerUser(any(User.class)))
                .thenThrow(new InvalidUserDataException(List.of("El campo nombres es obligatorio")));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0]").value("El campo nombres es obligatorio"))
                .andExpect(jsonPath("$.path").value(URL));
    }

    @Test
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        when(userUseCase.registerUser(any(User.class)))
                .thenThrow(new EmailAlreadyRegisteredException("ana@mail.com"));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void shouldReturn400WhenSalaryIsNotNumeric() throws Exception {
        String body = VALID_BODY.replace("2500000", "\"mucho\"");

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn400WhenJsonIsMalformed() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("{ nombres: "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500WithGenericMessageOnUnexpectedError() throws Exception {
        when(userUseCase.registerUser(any(User.class))).thenThrow(new IllegalStateException("db caída"));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Ocurrió un error inesperado. Intenta más tarde"));
    }

    @Test
    void shouldReturn405ForUnsupportedMethod() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void shouldReturn415WhenContentTypeIsNotJson() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.TEXT_PLAIN).content("hola"))
                .andExpect(status().isUnsupportedMediaType());
    }
}
