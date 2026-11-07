package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UserEntity;
import co.com.crediya.model.exceptions.EmailAlreadyRegisteredException;
import co.com.crediya.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class JPARepositoryAdapterTest {

    @Mock
    private JPARepository repository;

    @Mock
    private ObjectMapper objectMapper;

    private JPARepositoryAdapter adapter;

    private final User user = User.builder()
            .nombres("Ana")
            .apellidos("Gomez")
            .fechaNacimiento(LocalDate.of(1995, 5, 10))
            .direccion("Calle 1")
            .telefono("3001234567")
            .correoElectronico("ana@mail.com")
            .salarioBase(new BigDecimal("2500000"))
            .build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new JPARepositoryAdapter(repository, objectMapper);
    }

    @Test
    void shouldSaveUserAndMapAllFields() {
        when(repository.saveAndFlush(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setId("generated-id");
            return entity;
        });

        User saved = adapter.save(user);

        assertEquals("generated-id", saved.getId());
        assertEquals("Ana", saved.getNombres());
        assertEquals("Gomez", saved.getApellidos());
        assertEquals(LocalDate.of(1995, 5, 10), saved.getFechaNacimiento());
        assertEquals("Calle 1", saved.getDireccion());
        assertEquals("3001234567", saved.getTelefono());
        assertEquals("ana@mail.com", saved.getCorreoElectronico());
        assertEquals(new BigDecimal("2500000"), saved.getSalarioBase());
    }

    @Test
    void shouldTranslateEmailUniqueViolationToBusinessException() {
        when(repository.saveAndFlush(any(UserEntity.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "duplicate key value violates unique constraint \"uk_usuarios_correo_electronico\""));

        assertThrows(EmailAlreadyRegisteredException.class, () -> adapter.save(user));
    }

    @Test
    void shouldNotReportDuplicatedEmailForOtherIntegrityErrors() {
        DataIntegrityViolationException otherError =
                new DataIntegrityViolationException("value too long for type character varying(30)");
        when(repository.saveAndFlush(any(UserEntity.class))).thenThrow(otherError);

        DataIntegrityViolationException thrown =
                assertThrows(DataIntegrityViolationException.class, () -> adapter.save(user));
        assertSame(otherError, thrown);
    }

    @Test
    void shouldCheckIfEmailExists() {
        when(repository.existsByCorreoElectronico("ana@mail.com")).thenReturn(true);

        assertTrue(adapter.existsByCorreoElectronico("ana@mail.com"));
    }
}
