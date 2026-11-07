package co.com.crediya.jpa.role;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RoleRepositoryAdapterTest {

    private final RoleJpaRepository roleJpaRepository = mock(RoleJpaRepository.class);
    private final RoleRepositoryAdapter adapter = new RoleRepositoryAdapter(roleJpaRepository);

    @Test
    void shouldReturnTrueWhenRoleExists() {
        when(roleJpaRepository.existsById(3)).thenReturn(true);

        assertTrue(adapter.existsById(3));
    }

    @Test
    void shouldReturnFalseWhenRoleDoesNotExist() {
        when(roleJpaRepository.existsById(99)).thenReturn(false);

        assertFalse(adapter.existsById(99));
    }

    @Test
    void shouldReturnFalseForNullRole() {
        assertFalse(adapter.existsById(null));
    }
}
