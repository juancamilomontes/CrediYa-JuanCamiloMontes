package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UserEntity;
import co.com.crediya.jpa.helper.AdapterOperations;
import co.com.crediya.jpa.mapper.UserEntityMapper;
import co.com.crediya.model.exceptions.DocumentAlreadyRegisteredException;
import co.com.crediya.model.exceptions.EmailAlreadyRegisteredException;
import co.com.crediya.model.exceptions.InvalidUserDataException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Driven adapter: implementa el gateway UserRepository del dominio usando JPA (tabla "usuario").
 */
@Slf4j
@Repository
public class JPARepositoryAdapter extends AdapterOperations<User, UserEntity, Integer, JPARepository>
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
            log.debug("Usuario guardado con id {}", saved.getIdUsuario());
            return saved;
        } catch (DataIntegrityViolationException e) {
            // Las restricciones de la base protegen el caso de dos registros simultáneos.
            String message = String.valueOf(e.getMostSpecificCause().getMessage()).toLowerCase(Locale.ROOT);
            if (message.contains(UserEntity.EMAIL_UNIQUE_CONSTRAINT)) {
                throw new EmailAlreadyRegisteredException(user.getEmail());
            }
            if (message.contains(UserEntity.DOCUMENTO_UNIQUE_CONSTRAINT)) {
                throw new DocumentAlreadyRegisteredException(user.getDocumentoIdentidad());
            }
            if (message.contains(UserEntity.ROL_FOREIGN_KEY)) {
                throw new InvalidUserDataException(List.of("El rol indicado no existe"));
            }
            log.error("Violación de integridad al guardar el usuario: {}", e.getMostSpecificCause().getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDocumentoIdentidad(String documentoIdentidad) {
        return repository.existsByDocumentoIdentidad(documentoIdentidad);
    }

    @Override
    protected UserEntity saveData(UserEntity data) {
        // saveAndFlush fuerza el INSERT dentro del try, así las violaciones se capturan aquí.
        return repository.saveAndFlush(data);
    }
}
