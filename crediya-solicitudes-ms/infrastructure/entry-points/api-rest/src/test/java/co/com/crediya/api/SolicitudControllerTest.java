package co.com.crediya.api;

import co.com.crediya.api.exception.GlobalExceptionHandler;
import co.com.crediya.model.exceptions.InvalidSolicitudDataException;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
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

class SolicitudControllerTest {

    private static final String URL = "/api/v1/solicitud";
    private static final String VALID_BODY = """
            {
              "monto": 5000000,
              "plazo": 24,
              "email": "cliente@mail.com",
              "id_tipo_prestamo": 1
            }
            """;

    private SolicitudUseCase solicitudUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        solicitudUseCase = mock(SolicitudUseCase.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new SolicitudController(solicitudUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn201WhenSolicitudIsRegistered() throws Exception {
        when(solicitudUseCase.registrarSolicitud(any(Solicitud.class))).thenReturn(Solicitud.builder()
                .idSolicitud(7)
                .monto(new BigDecimal("5000000"))
                .plazo(24)
                .email("cliente@mail.com")
                .idEstado(1)
                .idTipoPrestamo(1)
                .build());

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id_solicitud").value(7))
                .andExpect(jsonPath("$.email").value("cliente@mail.com"))
                .andExpect(jsonPath("$.id_estado").value(1))
                .andExpect(jsonPath("$.id_tipo_prestamo").value(1));
    }

    @Test
    void shouldReturn400WithErrorListWhenDataIsInvalid() throws Exception {
        when(solicitudUseCase.registrarSolicitud(any(Solicitud.class)))
                .thenThrow(new InvalidSolicitudDataException(List.of("El campo monto es obligatorio")));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0]").value("El campo monto es obligatorio"))
                .andExpect(jsonPath("$.path").value(URL));
    }

    @Test
    void shouldReturn400WhenMontoIsNotNumeric() throws Exception {
        String body = VALID_BODY.replace("5000000", "\"mucho\"");

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn400WhenJsonIsMalformed() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("{ monto: "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500WithGenericMessageOnUnexpectedError() throws Exception {
        when(solicitudUseCase.registrarSolicitud(any(Solicitud.class)))
                .thenThrow(new IllegalStateException("db caída"));

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
