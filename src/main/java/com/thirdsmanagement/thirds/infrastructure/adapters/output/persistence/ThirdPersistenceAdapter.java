package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Set;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.application.service.GeographyLoaderService;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
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
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeForeignKeyViolationException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdForeignKeyViolationException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Clase adaptador de persistencia para la entidad Third.
 * Implementa la interfaz {@link ThirdOutputPort}.
 * Utiliza {@link ThirdRepository}, {@link ThirdTypeRepository} y {@link TypeIdRepository} para las operaciones de persistencia.
 * Utiliza {@link ThirdPersistenceMapper} para mapear las entidades y los modelos.
 * Proporciona métodos para guardar y obtener los terceros.
 */
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

        if (third.getTypeId() == null || third.getTypeId().getTypeId() == null) {
            throw new IllegalArgumentException("El tipo de identificación del tercero no puede ser null");
        }

        validateTypeIdExists(third.getTypeId().getTypeId());
        validateThirdTypesExist(third.getThirdTypes());

        // Preparar la entidad principal
        ThirdEntity thirdEntity = thirdPersistenceMapper.toThirdEntity(third);
        thirdEntity.setTenantId(TenantContext.getTenantId());

        // Obtener la referencia del tipo de identificación
        TypeIdEntity typeIdEntity = typeIdRepository.getReferenceById(third.getTypeId().getTypeId());
        thirdEntity.setTypeId(typeIdEntity);

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
                .rutPath(third.getRutPath())
                .personType(third.getPersonType())
                .names(third.getNames())
                .lastNames(third.getLastNames())
                .socialReason(third.getSocialReason())
                .gender(third.getGender())
                .idNumber(third.getIdNumber())
                .verificationNumber(third.getVerificationNumber())
                .state(third.getState())
                .photoPath(third.getPhotoPath())
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
    private void validateTypeIdExists(String typeId) {
        if (!typeIdRepository.existsById(typeId)) {
            throw new TypeIdForeignKeyViolationException(typeId);
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
        boolean existe = false;
        existe = thirdRepository.existThirdBy(id,  entId);

        return existe;
    }

    /**
     * Cambia el estado de un tercero.
     * @param thId Identificador del tercero.
     * @return Verdadero si el estado del tercero cambió, falso en caso contrario.
     */
    @Override
    public boolean changeThirdState(Long thId) {
        System.out.println("\n Entrando a changeThirdState \n");
        Optional<ThirdEntity> thirdEntity = thirdRepository.findById(thId);

        if(thirdEntity.isEmpty()) {
            return false;
        }

        ThirdEntity entity = thirdEntity.get();
        String currentState = entity.getState();
        String newState;

        if("true".equals(currentState)){
            newState = "false";
        } else {
            newState = "true";
        }

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
        System.out.println("\n Entrando a getAllThirdsBy \n");
        Page<ThirdEntity> pageEntities = thirdRepository.getThirdsBy(entId, page);
        Page<Third> pageThirds = pageEntities.map(this::convertToThird);

        return pageThirds;
    }


    /**
     * Obtiene una página de terceros filtrados por tipo de tercero y el identificador de entidad.
     * @param entId El identificador de la entidad por la cual se filtrarán los terceros.
     * @param page El objeto Pageable que contiene la información de paginación.
     * @param thirdType El tipo de tercero (ej: "Proveedor", "Cliente").
     * @return Una página de objetos Third que representan los terceros filtrados por tipo.
     */
    @Override
    public Page<Third> getAllThirdsByType(String entId, Pageable page, String thirdType) {
        System.out.println("\n Entrando a getAllThirdsByType \n");
        Page<ThirdEntity> pageEntities = thirdRepository.getThirdsByType(entId, thirdType, page);
        Page<Third> pageThirds = pageEntities.map(this::convertToThird);

        return pageThirds;
    }


    /**
     * Convierte un objeto ThirdEntity a un objeto Third.
     * @param thirdEntity El objeto ThirdEntity que se va a convertir.
     * @return El objeto Third resultante de la conversión.
     */
    private Third convertToThird(ThirdEntity thirdEntity) {

        System.out.println("\n Entrando a convertToThird \n");

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
        
        System.out.println("Entrando A Actualizar");
        
        ThirdEntity thirdEntity = thirdRepository.findById(third.getThId()).orElse(null);
        System.out.println(thirdEntity.toString());
        System.out.println(thirdEntity.getNames());
        if(thirdEntity != null){
            thirdEntity.setNames(third.getNames());
            thirdEntity.setLastNames(third.getLastNames());
            thirdEntity.setIdNumber(third.getIdNumber());
            thirdEntity.setSocialReason(third.getSocialReason());
            thirdEntity.setAddress(third.getAddress());
            thirdEntity.setCity(third.getCity() != null ? third.getCity().getCityCode() : null);
            thirdEntity.setCountry(third.getCountry() != null ? third.getCountry().getCountryCode() : null);
            thirdEntity.setProvince(third.getProvince() != null ? third.getProvince().getStateCode() : null);
            thirdEntity.setPersonType(third.getPersonType());
            
            if (third.getGender() != null) {
                thirdEntity.setGender(third.getGender().name());
            } else {
                //thirdEntity.setGender("");
            }
                 
            TypeIdEntity typeIdEntity = new TypeIdEntity();
            TypeId typeId = third.getTypeId();

            if (typeId != null) {
                typeIdEntity.setTiId(typeId.getTypeId()); // Asigna typeId a tiId
                typeIdEntity.setTiName(typeId.getTypeIdname()); // Asigna typeIdname a tiName
                String entId = typeId.getEntId();
                if (entId == null || entId.trim().isEmpty()) {
                    entId = "standart";
                }
                typeIdEntity.setTientId(entId); // Asigna entId a tientId

                // Log para verificar la conversión
                System.out.println("Tipo de ID asignado: " + typeId.getTypeIdname());
            }

            // Asigna el resultado al atributo correspondiente
            thirdEntity.setTypeId(typeIdEntity);


            System.out.println("El estado ga guardar es es" + third.getState());
            if(third.getState()){
                thirdEntity.setState("true");
                System.out.println("El estado entro a guardar Activo " + third.getState());
            }else{
                thirdEntity.setState("false");
                System.out.println("El estado entro a guardar Inactivo " + third.getState());
            }
            thirdEntity.setVerificationNumber(thirdEntity.getVerificationNumber());
            thirdEntity.setPhotoPath(third.getPhotoPath());
            thirdEntity.setPhoneNumber(third.getPhoneNumber());
            thirdEntity.setEmail(third.getEmail());
            // Los tipos de tercero se manejan a través de ThirdsAndTypesEntity
            // No se asignan directamente a la entidad Third
            thirdEntity = thirdRepository.save(thirdEntity);
        }
        Third updatedThird = thirdPersistenceMapper.toThird(thirdEntity);
        return loadGeographyData(updatedThird, thirdEntity);
    }

    /**
     * Obtiene todos los terceros.
     * @param entId Id de la empresa.
     * @return Lista de terceros.
     */
    @Override
    public List<Third> getAllThirds(String entId) {
        List<ThirdEntity> thirdEntities = thirdRepository.getAllThirds(entId);
        List<Third> thirds = thirdEntities.stream().map(this::convertToThird).collect(Collectors.toList());
        return thirds;
    }

    /**
     * Obtiene una página de terceros filtrados por estado y el identificador de entidad.
     * @param entId El identificador de la entidad por la cual se filtrarán los terceros.
     * @param page El objeto Pageable que contiene la información de paginación.
     * @param isActive El estado del tercero (true para activos, false para inactivos).
     * @return Una página de objetos Third que representan los terceros filtrados por estado.
     */
    @Override
    public Page<Third> getAllThirdsByStatus(String entId, Pageable page, boolean isActive) {
        System.out.println("\n Entrando a getAllThirdsByStatus \n");
        String state = isActive ? "true" : "false";
        Page<ThirdEntity> pageEntities = thirdRepository.getThirdsByStatus(entId, state, page);
        Page<Third> pageThirds = pageEntities.map(this::convertToThird);

        return pageThirds;
    }
}
