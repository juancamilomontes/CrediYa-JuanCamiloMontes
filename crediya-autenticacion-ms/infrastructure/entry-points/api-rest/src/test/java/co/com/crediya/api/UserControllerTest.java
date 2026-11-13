package co.com.crediya.api;

import co.com.crediya.api.exception.GlobalExceptionHandler;
import co.com.crediya.model.exceptions.DocumentAlreadyRegisteredException;
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
              "nombre": "Ana",
              "apellido": "Gomez",
              "email": "ana@mail.com",
              "documento_identidad": "1020304050",
              "telefono": "3001234567",
              "id_rol": 3,
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
                .idUsuario(7)
                .nombre("Ana")
                .apellido("Gomez")
                .email("ana@mail.com")
                .documentoIdentidad("1020304050")
                .idRol(3)
                .salarioBase(new BigDecimal("2500000"))
                .build());

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id_usuario").value(7))
                .andExpect(jsonPath("$.email").value("ana@mail.com"))
                .andExpect(jsonPath("$.documento_identidad").value("1020304050"))
                .andExpect(jsonPath("$.id_rol").value(3));
    }

    @Test
    void shouldReturn409WhenDocumentAlreadyExists() throws Exception {
        when(userUseCase.registerUser(any(User.class)))
                .thenThrow(new DocumentAlreadyRegisteredException("1020304050"));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
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
