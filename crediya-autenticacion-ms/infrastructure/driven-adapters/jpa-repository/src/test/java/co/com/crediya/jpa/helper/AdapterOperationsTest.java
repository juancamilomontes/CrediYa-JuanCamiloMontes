package co.com.crediya.jpa.helper;

import co.com.crediya.jpa.JPARepository;
import co.com.crediya.jpa.JPARepositoryAdapter;
import co.com.crediya.jpa.entity.UserEntity;
import co.com.crediya.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AdapterOperationsTest {

    @Mock
    private JPARepository repository;

    @Mock
    private ObjectMapper objectMapper;

    private JPARepositoryAdapter adapter;

    private final UserEntity entity = UserEntity.builder()
            .idUsuario(1).nombre("Ana").apellido("Gomez").email("ana@mail.com").build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new JPARepositoryAdapter(repository, objectMapper);
    }

    @Test
    void testSaveAllEntities() {
        when(repository.saveAll(any())).thenReturn(List.of(entity, entity));

        List<User> result = adapter.saveAllEntities(List.of(User.builder().build(), User.builder().build()));

        assertEquals(2, result.size());
        assertEquals("ana@mail.com", result.get(0).getEmail());
    }

    @Test
    void testFindById() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));

        assertEquals("Ana", adapter.findById(1).getNombre());
    }

    @Test
    void testFindByIdNotFound() {
        when(repository.findById(2)).thenReturn(Optional.empty());

        assertNull(adapter.findById(2));
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(List.of(entity));

        assertEquals(1, adapter.findAll().size());
    }

    @Test
    void testFindByExample() {
        when(repository.findAll(any(Example.class))).thenReturn(List.of(entity));

        assertEquals(1, adapter.findByExample(User.builder().nombre("Ana").build()).size());
    }
}
