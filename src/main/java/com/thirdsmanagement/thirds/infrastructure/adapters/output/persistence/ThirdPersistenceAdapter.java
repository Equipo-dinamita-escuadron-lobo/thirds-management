package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence;

import java.util.*;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Set;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.application.service.GeographyLoaderService;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdsAndTypesEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper.ThirdPersistenceMapper;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.TypeIdRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdsAndTypesRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.multitenancy.utils.TenantContext;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdNotFound;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeForeignKeyViolationException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdForeignKeyViolationException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Component;

/**
 * Clase adaptador de persistencia para la entidad Third.
 * Implementa la interfaz {@link ThirdOutputPort}.
 * Utiliza {@link ThirdRepository}, {@link ThirdTypeRepository} y {@link TypeIdRepository} para las operaciones de persistencia.
 * Utiliza {@link ThirdPersistenceMapper} para mapear las entidades y los modelos.
 * Proporciona métodos para guardar y obtener los terceros.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ThirdPersistenceAdapter implements ThirdOutputPort{
    @PersistenceContext
    private EntityManager entityManager;

    private final ThirdRepository thirdRepository;
    private final ThirdTypeRepository thirdTypeRepository;
    private final TypeIdRepository typeIdRepository;
    private final ThirdsAndTypesRepository thirdsAndTypesRepository;
    private final ThirdPersistenceMapper thirdPersistenceMapper;
    private final GeographyLoaderService geographyLoaderService;

    /**
     * Guarda un tercero.
     * @param third Tercero a guardar.
     * @return Tercero guardado.
     */
    @Override
    public Third saveThird(Third third) {
        if (third == null) {
            throw new IllegalArgumentException("El tercero no puede ser null");
        }

        if (third.getTypeId() == null || third.getTypeId().getId() == null) {
            throw new IllegalArgumentException("El tipo de identificación del tercero no puede ser null");
        }

        validateTypeIdExists(third.getTypeId().getId());
        validateThirdTypesExist(third.getThirdTypes());

        // Preparar la entidad principal
        ThirdEntity thirdEntity = thirdPersistenceMapper.toThirdEntity(third);
        thirdEntity.setTenantId(TenantContext.getTenantId());

        // Obtener la referencia del tipo de identificación
        Optional<TypeIdEntity> typeIdEntityOpt = typeIdRepository.findById(third.getTypeId().getId());
        if (typeIdEntityOpt.isEmpty()) {
            throw new TypeIdForeignKeyViolationException(third.getTypeId().getId().toString());
        }
        thirdEntity.setTypeId(typeIdEntityOpt.get());

        // Guardar la entidad del tercero
        try {
            thirdEntity = thirdRepository.save(thirdEntity);
        } catch (DataIntegrityViolationException e) {
            throw e;
        }

        // Crear las relaciones con tipos de tercero
        if (third.getThirdTypes() != null && !third.getThirdTypes().isEmpty()) {
            for (ThirdType tt : third.getThirdTypes()) {
                ThirdsAndTypesEntity relationEntity = ThirdsAndTypesEntity.builder()
                        .thId(thirdEntity.getThId())
                        .ttId(tt.getThirdTypeId())
                        .tenantId(TenantContext.getTenantId())
                        .build();

                try {
                    thirdsAndTypesRepository.save(relationEntity);
                } catch (DataIntegrityViolationException e) {
                    throw e;
                }
            }
        }

        // Convertir de vuelta al dominio
        Third domainThird = thirdPersistenceMapper.toThird(thirdEntity);
        return loadGeographyData(domainThird, thirdEntity);
    }

    /**
     * Carga los datos geográficos completos para un objeto Third.
     * @param third el objeto Third del dominio
     * @param thirdEntity la entidad de persistencia con los códigos geográficos
     * @return el objeto Third con datos geográficos completos
     */
    private Third loadGeographyData(Third third, ThirdEntity thirdEntity) {
        return Third.builder()
                .thId(third.getThId())
                .entId(third.getEntId())
                .typeId(third.getTypeId())
                .thirdTypes(third.getThirdTypes())
                .personType(third.getPersonType())
                .names(third.getNames())
                .lastNames(third.getLastNames())
                .socialReason(third.getSocialReason())
                .gender(third.getGender())
                .idNumber(third.getIdNumber())
                .verificationNumber(third.getVerificationNumber())
                .state(third.getState())
                .country(geographyLoaderService.loadCountryByCode(thirdEntity.getCountry()))
                .province(geographyLoaderService.loadStateByCode(thirdEntity.getProvince(), thirdEntity.getCountry()))
                .city(geographyLoaderService.loadCityByCode(thirdEntity.getCity(), thirdEntity.getProvince(), thirdEntity.getCountry()))
                .address(third.getAddress())
                .phoneNumber(third.getPhoneNumber())
                .email(third.getEmail())
                .creationDate(third.getCreationDate())
                .updateDate(third.getUpdateDate())
                .build();
    }

    /**
     * Valida que el tipo de identificación existe antes de proceder con el guardado.
     */
    private void validateTypeIdExists(Long typeId) {
        if (!typeIdRepository.existsById(typeId)) {
            throw new TypeIdForeignKeyViolationException(typeId.toString());
        }
    }

    /**
     * Valida que todos los tipos de tercero existen antes de proceder con el guardado.
     */
    private void validateThirdTypesExist(Set<ThirdType> thirdTypes) {
        if (thirdTypes != null && !thirdTypes.isEmpty()) {
            for (ThirdType tt : thirdTypes) {
                if (!thirdTypeRepository.existsById(tt.getThirdTypeId())) {
                    throw new ThirdTypeForeignKeyViolationException(String.valueOf(tt.getThirdTypeId()));
                }
            }
        }
    }

    /**
     * Obtiene un tercero por su identificador y empresa.
     * @param id Identificador del tercero.
     * @param entId Identificador de la empresa.
     * @return Tercero encontrado.
     */
    @Override
    public Optional<Third> getThirdById(Long id, String entId) {
        Optional<ThirdEntity> thirdEntity = thirdRepository.findByThIdAndEntId(id, entId);

        if(thirdEntity.isEmpty()) {
            return Optional.empty();
        }

        Third third = convertToThird(thirdEntity.get());
        return Optional.of(third);
    }
    @Override
    public boolean existThirdById(long id, String entId) {
        return thirdRepository.existThirdByThIdAndEntId(id, entId);
    }

    /**
     * Cambia el estado de un tercero.
     * @param thId Identificador del tercero.
     * @param entId Identificador de la empresa.
     * @return Verdadero si el estado del tercero cambió, falso en caso contrario.
     */
    @Override
    public boolean changeThirdState(Long thId, String entId) {
        Optional<ThirdEntity> thirdEntity = thirdRepository.findByThIdAndEntId(thId, entId);

        if(thirdEntity.isEmpty()) {
            return false;
        }

        ThirdEntity entity = thirdEntity.get();
        Boolean currentState = entity.getState();
        Boolean newState = !Boolean.TRUE.equals(currentState);

        entity.setState(newState);
        thirdRepository.save(entity);

        return true;
    }

    /**
     * Obtiene una página de terceros filtrados por el identificador de entidad.
     * @param entId El identificador de la entidad por la cual se filtrarán los terceros.
     * @param page El objeto Pageable que contiene la información de paginación.
     * @return Una página de objetos Third que representan los terceros.
     */
    @Override
    public Page<Third> getAllThirdsBy(String entId, Pageable page) {
        Page<ThirdEntity> pageEntities = thirdRepository.getThirdsBy(entId, page);
        Page<Third> pageThirds = pageEntities.map(this::convertToThird);

        return pageThirds;
    }



    /**
     * Convierte un objeto ThirdEntity a un objeto Third.
     * @param thirdEntity El objeto ThirdEntity que se va a convertir.
     * @return El objeto Third resultante de la conversión.
     */
    private Third convertToThird(ThirdEntity thirdEntity) {


        Third obj = this.thirdPersistenceMapper.toThird(thirdEntity);
        obj = loadGeographyData(obj, thirdEntity);

        // Cargar los tipos de tercero desde la tabla de relación
        List<ThirdsAndTypesEntity> relations = thirdsAndTypesRepository.findByThId(thirdEntity.getThId());
        
        for(ThirdsAndTypesEntity relation : relations) {
            Optional<ThirdTypeEntity> thirdTypeEntity = thirdTypeRepository.findById(relation.getTtId());
            if (thirdTypeEntity.isPresent()) {
                ThirdTypeEntity tt = thirdTypeEntity.get();
                ThirdType thirdType = ThirdType.builder()
                    .thirdTypeId(tt.getTtId())
                    .thirdTypeName(tt.getTtName())
                    .entId(tt.getTtentId())
                    .status(tt.getStatus())
                    .build();
                obj.getThirdTypes().add(thirdType);
            }
        }

        return obj;
    }

    /**
    * Actualiza un tercero en el sistema.
    * Este método permite actualizar la información de un tercero existente,
    * incluyendo su nombre, apellidos, número de identificación, razón social,
    * dirección, ubicación geográfica, tipo de persona, género, estado, tipo de
    * identificación y otros datos relacionados.
    *
    * @param third el objeto {@link Third} que contiene la información actualizada del tercero.
    *              Debe incluir el identificador único del tercero (ThId) para localizarlo en la base de datos.
    * @return el objeto {@link Third} actualizado después de persistir los cambios en la base de datos.
    *         Contiene los nuevos valores de las propiedades del tercero.
    *
    * @throws IllegalArgumentException si el tercero proporcionado es nulo.
    * @throws EntityNotFoundException si no se encuentra un tercero con el identificador proporcionado (ThId).
    * @throws DataIntegrityViolationException si se viola alguna restricción de la base de datos durante la actualización.
    */
    @Override
    public Third updateThird(Third third) {
        if (third == null) {
            throw new IllegalArgumentException("El tercero no puede ser null");
        }

        if (third.getThId() == null) {
            throw new IllegalArgumentException("El ID del tercero no puede ser null para actualizar");
        }

        // Buscar la entidad existente
        Optional<ThirdEntity> existingEntityOpt = thirdRepository.findById(third.getThId());
        if (existingEntityOpt.isEmpty()) {
            throw new ThirdNotFound("No se encontró el tercero con ID: " + third.getThId());
        }

        ThirdEntity thirdEntity = existingEntityOpt.get();

        // Validar que el TypeId existe antes de actualizar
        if (third.getTypeId() != null && third.getTypeId().getId() != null) {
            validateTypeIdExists(third.getTypeId().getId());
            Optional<TypeIdEntity> typeIdEntityOpt = typeIdRepository.findById(third.getTypeId().getId());
            if (typeIdEntityOpt.isPresent()) {
                thirdEntity.setTypeId(typeIdEntityOpt.get());
            }
        }

        // Validar que los ThirdTypes existen antes de actualizar
        if (third.getThirdTypes() != null && !third.getThirdTypes().isEmpty()) {
            validateThirdTypesExist(third.getThirdTypes());
        }

        // Actualizar los campos de la entidad principal
        thirdEntity.setNames(third.getNames());
        thirdEntity.setLastNames(third.getLastNames());
        thirdEntity.setIdNumber(third.getIdNumber());
        thirdEntity.setSocialReason(third.getSocialReason());
        thirdEntity.setAddress(third.getAddress());
        thirdEntity.setCity(third.getCity() != null ? third.getCity().getCityCode() : null);
        thirdEntity.setCountry(third.getCountry() != null ? third.getCountry().getCountryCode() : null);
        thirdEntity.setProvince(third.getProvince() != null ? third.getProvince().getStateCode() : null);
        thirdEntity.setPersonType(third.getPersonType());
        thirdEntity.setGender(third.getGender() != null ? third.getGender().name() : null);
        thirdEntity.setState(third.getState() != null ? third.getState() : true);
        thirdEntity.setPhoneNumber(third.getPhoneNumber());
        thirdEntity.setEmail(third.getEmail());

        // Guardar la entidad principal actualizada
        try {
            thirdEntity = thirdRepository.save(thirdEntity);
        } catch (DataIntegrityViolationException e) {
            throw e;
        }

        // Actualizar las relaciones de tipos de tercero
        updateThirdTypeRelations(thirdEntity.getThId(), third.getThirdTypes());

        // Convertir de vuelta al dominio
        Third updatedThird = thirdPersistenceMapper.toThird(thirdEntity);
        return loadGeographyData(updatedThird, thirdEntity);
    }


    /**
     * Actualiza las relaciones de tipos de tercero para un tercero específico.
     * Elimina todas las relaciones existentes y crea las nuevas.
     * @param thirdId ID del tercero
     * @param thirdTypes Nuevos tipos de tercero a asociar
     */
    private void updateThirdTypeRelations(Long thirdId, Set<ThirdType> thirdTypes) {
        // Eliminar todas las relaciones existentes
        List<ThirdsAndTypesEntity> existingRelations = thirdsAndTypesRepository.findByThId(thirdId);
        if (!existingRelations.isEmpty()) {
            thirdsAndTypesRepository.deleteAll(existingRelations);
        }
        
        // Crear las nuevas relaciones si hay tipos de tercero
        if (thirdTypes != null && !thirdTypes.isEmpty()) {
            for (ThirdType thirdType : thirdTypes) {
                ThirdsAndTypesEntity relationEntity = ThirdsAndTypesEntity.builder()
                        .thId(thirdId)
                        .ttId(thirdType.getThirdTypeId())
                        .tenantId(TenantContext.getTenantId())
                        .build();

                try {
                    thirdsAndTypesRepository.save(relationEntity);
                } catch (DataIntegrityViolationException e) {
                    throw e;
                }
            }
        }
    }
    
    /**
     * Elimina un tercero del sistema junto con sus asociaciones.
     * El tercero es la entidad raíz, por lo que sus asociaciones se eliminan automáticamente.
     * @param thirdId El ID del tercero a eliminar
     * @param entId El ID de la empresa
     * @return true si se eliminó correctamente, false en caso contrario
     */
    @Override
    @Transactional
    public boolean deleteThird(Long thirdId, String entId) {
        if (thirdId == null || entId == null || entId.trim().isEmpty()) {
            return false;
        }
        
        String currentTenant = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId(currentTenant);
            
            // Buscar el tercero por ID y empresa
            Optional<ThirdEntity> thirdEntity = thirdRepository.findByThIdAndEntId(thirdId, entId);
            
            if (thirdEntity.isPresent()) {
                // Eliminar las asociaciones del tercero
                List<ThirdsAndTypesEntity> relations = thirdsAndTypesRepository.findByThId(thirdId);
                if (!relations.isEmpty()) {
                    thirdsAndTypesRepository.deleteAll(relations);
                }
                
                // Eliminar el tercero
                thirdRepository.delete(thirdEntity.get());
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            return false;
        } finally {
            TenantContext.setTenantId(currentTenant);
        }
    }

    /**
     * Cuenta el total de terceros por empresa.
     * @param entId El id de la empresa
     * @return El número total de terceros
     */
    @Override
    public long countAllThirdsByEntId(String entId) {
        if (entId == null || entId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la empresa no puede ser null o vacío");
        }
        return thirdRepository.countByEntId(entId);
    }

    /**
     * Actualiza el estado de todos los terceros de una empresa de forma masiva.
     * Utiliza una query nativa optimizada para mejor performance.
     * Bean Validation en el controlador garantiza que los parámetros no son null.
     * 
     * @param entId El id de la empresa
     * @param newState El nuevo estado (true para activo, false para inactivo)
     * @return La cantidad de terceros actualizados
     */
    @Override
    @Transactional
    public int bulkUpdateThirdState(String entId, Boolean newState) {
        String currentTenant = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId(currentTenant);
            return thirdRepository.bulkUpdateStateByEntId(entId, newState);
        } finally {
            TenantContext.setTenantId(currentTenant);
        }
    }

    /**
     * Encuentra qué números de identificación ya existen en la base de datos.
     * 
     * @param idNumbers conjunto de números de identificación a verificar
     * @param entId el id de la empresa
     * @return conjunto de números de identificación que ya existen
     */
    @Override
    public Set<Long> findExistingIdNumbers(Set<Long> idNumbers, String entId) {
        if (idNumbers == null || idNumbers.isEmpty()) {
            return Collections.emptySet();
        }
        
        if (entId == null || entId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la empresa no puede ser null o vacío");
        }

        // Usar consulta batch optimizada del repositorio
        List<Long> existingList = thirdRepository.findExistingIdNumbers(idNumbers, entId);
        return new HashSet<>(existingList);
    }
    
}
