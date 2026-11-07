package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UserEntity;
import co.com.crediya.jpa.helper.AdapterOperations;
import co.com.crediya.jpa.mapper.UserEntityMapper;
import co.com.crediya.model.exceptions.EmailAlreadyRegisteredException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Driven adapter: implementa el gateway UserRepository del dominio usando JPA.
 */
@Slf4j
@Repository
public class JPARepositoryAdapter extends AdapterOperations<User, UserEntity, String, JPARepository>
        implements UserRepository {

    public JPARepositoryAdapter(JPARepository repository, ObjectMapper mapper) {
        super(repository, mapper, UserEntityMapper::toDomain);
    }

    @Override
    protected UserEntity toData(User user) {
        return UserEntityMapper.toEntity(user);
    }

    @Override
    @Transactional
    public User save(User user) {
        log.debug("Guardando usuario en base de datos");
        try {
            User saved = super.save(user);
            log.debug("Usuario guardado con id {}", saved.getId());
            return saved;
        } catch (DataIntegrityViolationException e) {
            if (isEmailUniqueViolation(e)) {
                // La restricción UNIQUE del correo protege el caso de dos registros simultáneos.
                log.warn("Correo duplicado detectado por la base de datos");
                throw new EmailAlreadyRegisteredException(user.getCorreoElectronico());
            }
            log.error("Violación de integridad al guardar el usuario: {}", e.getMostSpecificCause().getMessage());
            throw e;
        }
    }

    private static boolean isEmailUniqueViolation(DataIntegrityViolationException e) {
        String message = String.valueOf(e.getMostSpecificCause().getMessage()).toLowerCase(Locale.ROOT);
        boolean isUniqueError = message.contains("unique") || message.contains("duplicate");
        return isUniqueError && (message.contains(UserEntity.EMAIL_UNIQUE_CONSTRAINT)
                || message.contains("correo_electronico"));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCorreoElectronico(String correoElectronico) {
        return repository.existsByCorreoElectronico(correoElectronico);
    }

    @Override
    protected UserEntity saveData(UserEntity data) {
        // saveAndFlush fuerza el INSERT dentro del try, así la violación del UNIQUE se captura aquí.
        return repository.saveAndFlush(data);
    }
}
