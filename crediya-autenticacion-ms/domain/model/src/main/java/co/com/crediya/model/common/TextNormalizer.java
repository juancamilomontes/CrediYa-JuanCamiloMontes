package co.com.crediya.model.common;

import java.util.Locale;

/**
 * Reglas de limpieza de texto compartidas por todo el dominio.
 * Una sola responsabilidad: dejar los textos en un formato uniforme antes de validarlos o buscarlos.
 */
public final class TextNormalizer {

    private TextNormalizer() {
    }

    /** Quita espacios al inicio y al final. Devuelve null si recibe null. */
    public static String trim(String value) {
        return value == null ? null : value.trim();
    }

    /** Quita espacios y pasa a minúsculas: "  ANA@Mail.com " -> "ana@mail.com". */
    public static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
