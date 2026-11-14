package co.com.crediya.usecase.user;

import co.com.crediya.model.common.TextNormalizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TextNormalizerTest {

    @Test
    void shouldTrimText() {
        assertEquals("Ana", TextNormalizer.trim("  Ana  "));
        assertNull(TextNormalizer.trim(null));
    }

    @Test
    void shouldNormalizeEmail() {
        assertEquals("ana@mail.com", TextNormalizer.normalizeEmail("  ANA@Mail.com "));
        assertNull(TextNormalizer.normalizeEmail(null));
    }
}
