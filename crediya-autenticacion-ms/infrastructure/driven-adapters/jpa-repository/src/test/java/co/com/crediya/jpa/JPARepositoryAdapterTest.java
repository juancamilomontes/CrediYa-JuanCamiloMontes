package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UserEntity;
import co.com.crediya.model.exceptions.DocumentAlreadyRegisteredException;
import co.com.crediya.model.exceptions.EmailAlreadyRegisteredException;
import co.com.crediya.model.exceptions.InvalidUserDataException;
import co.com.crediya.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

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
            .nombre("Ana")
            .apellido("Gomez")
            .email("ana@mail.com")
            .documentoIdentidad("1020304050")
            .telefono("3001234567")
            .idRol(3)
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
            entity.setIdUsuario(7);
            return entity;
        });

        User saved = adapter.save(user);

        assertEquals(7, saved.getIdUsuario());
        assertEquals("Ana", saved.getNombre());
        assertEquals("Gomez", saved.getApellido());
        assertEquals("ana@mail.com", saved.getEmail());
        assertEquals("1020304050", saved.getDocumentoIdentidad());
        assertEquals("3001234567", saved.getTelefono());
        assertEquals(3, saved.getIdRol());
        assertEquals(new BigDecimal("2500000"), saved.getSalarioBase());
    }

    @Test
    void shouldTranslateEmailUniqueViolation() {
        givenSaveFailsWith("duplicate key value violates unique constraint \"usuario_email_key\"");

        assertThrows(EmailAlreadyRegisteredException.class, () -> adapter.save(user));
    }

    @Test
    void shouldTranslateDocumentUniqueViolation() {
        givenSaveFailsWith("duplicate key value violates unique constraint \"usuario_documento_identidad_key\"");

        assertThrows(DocumentAlreadyRegisteredException.class, () -> adapter.save(user));
    }

    @Test
    void shouldTranslateRoleForeignKeyViolation() {
        givenSaveFailsWith("insert or update on table \"usuario\" violates foreign key constraint \"fk_usuario_rol\"");

        assertThrows(InvalidUserDataException.class, () -> adapter.save(user));
    }

    @Test
    void shouldRethrowOtherIntegrityErrors() {
        DataIntegrityViolationException otherError =
                new DataIntegrityViolationException("value too long for type character varying(20)");
        when(repository.saveAndFlush(any(UserEntity.class))).thenThrow(otherError);

        DataIntegrityViolationException thrown =
                assertThrows(DataIntegrityViolationException.class, () -> adapter.save(user));
        assertSame(otherError, thrown);
    }

    @Test
    void shouldCheckIfEmailExists() {
        when(repository.existsByEmail("ana@mail.com")).thenReturn(true);

        assertTrue(adapter.existsByEmail("ana@mail.com"));
    }

    @Test
    void shouldCheckIfDocumentExists() {
        when(repository.existsByDocumentoIdentidad("1020304050")).thenReturn(true);

        assertTrue(adapter.existsByDocumentoIdentidad("1020304050"));
    }

    private void givenSaveFailsWith(String message) {
        when(repository.saveAndFlush(any(UserEntity.class)))
                .thenThrow(new DataIntegrityViolationException(message));
    }
}
