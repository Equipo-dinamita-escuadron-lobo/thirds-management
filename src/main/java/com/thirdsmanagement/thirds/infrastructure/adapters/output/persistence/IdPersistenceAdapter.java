package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdAlreadyExists;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.PersonClassificationInvalidException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdInvalidDataException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdNameAlreadyExistsException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdNotFound;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeInvalidDataException;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeNameAlreadyExistsException;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeNotFound;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.domain.utils.StringNormalizer;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper.IdPersistenceMapper;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdsAndTypesRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.TypeIdRepository;
import com.thirdsmanagement.thirds.infrastructure.multitenancy.utils.TenantContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

/**
 * Clase adaptador de persistencia para la entidad Id.
 * Implementa la interfaz {@link IdOutputPort}.
 * Utiliza {@link ThirdRepository}, {@link ThirdTypeRepository} y {@link TypeIdRepository} para las operaciones de persistencia.
 * Utiliza {@link IdPersistenceMapper} para mapear las entidades y los modelos.
 * Proporciona métodos para guardar y obtener los tipos de terceros y los tipos de identificación.
 */
@Component
@RequiredArgsConstructor
public class IdPersistenceAdapter implements IdOutputPort {
    
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final ThirdRepository thirdRepository;
    private final ThirdTypeRepository thirdTypeRepository;
    private final ThirdsAndTypesRepository thirdsAndTypesRepository;
    private final TypeIdRepository typeIdRepository;
    private final IdPersistenceMapper idPersistenceMapper;
    

    /**
     * Guarda un tipo de tercero.
     * @param thirdType Tipo de tercero a guardar.
     * @return Tipo de tercero guardado.
     */
    @Override
    public ThirdType saveThirdType(ThirdType thirdType) {
        ThirdTypeEntity thirdTypeEntity = idPersistenceMapper.toThirdTypeEntity(thirdType);

        // Asignar tenant ID del contexto actual
        thirdTypeEntity.setTenantId(TenantContext.getTenantId());

        thirdTypeRepository.save(thirdTypeEntity);
        ThirdType result = idPersistenceMapper.toThirdType(thirdTypeEntity);
        return result;
    }

    /**
     * Obtiene todos los tipos de terceros.
     * @param entId Id de la entidad.
     * @return Lista de tipos de terceros.
     */
    @Override
    public List<ThirdType> getALLThirdTypes(String entId) {
        return idPersistenceMapper.toThirdTypeList(thirdTypeRepository.findAllByTtentId(entId));
    }

    /**
     * Guarda un tipo de identificación.
     * @param typeId Tipo de identificación a guardar.
     * @return Tipo de identificación guardado.
     */
    @Override
    public TypeId saveTypeId(TypeId typeId) {
        if (typeId == null) {
            throw new TypeIdInvalidDataException("El tipo de identificación no puede ser null");
        }

        if (typeId.getTypeId() == null || typeId.getTypeId().trim().isEmpty()) {
            throw new TypeIdInvalidDataException("El código del tipo de identificación no puede estar vacío");
        }

        if (typeId.getTypeIdname() == null || typeId.getTypeIdname().trim().isEmpty()) {
            throw new TypeIdInvalidDataException("El nombre del tipo de identificación no puede estar vacío");
        }

        if (typeId.getClassification() == null) {
            throw PersonClassificationInvalidException.forNullClassification();
        }

        // Normalizar valores para validaciones
        String normalizedTypeId = StringNormalizer.normalizeCode(typeId.getTypeId());
        String normalizedTypeIdName = StringNormalizer.normalizePreservingCase(typeId.getTypeIdname());
        
        if (typeIdRepository.existsByTiIdAndTientId(normalizedTypeId, typeId.getEntId())) {
            throw new TypeIdAlreadyExists("Ya existe un tipo de identificación con el código '" + normalizedTypeId + "'");
        }

        // Validar que no exista un typeIdname similar (case-insensitive) usando el nombre normalizado
        if (typeIdRepository.existsByTiNameIgnoreCaseAndTientId(normalizedTypeIdName, typeId.getEntId())) {
            throw new TypeIdNameAlreadyExistsException(typeId.getTypeIdname());
        }
        
        // Crear el modelo normalizado para guardar
        TypeId normalizedTypeIdModel = TypeId.builder()
                .typeId(normalizedTypeId)
                .typeIdname(normalizedTypeIdName)
                .entId(typeId.getEntId())
                .status(typeId.getStatus())
                .classification(typeId.getClassification())
                .build();

        TypeIdEntity typeIdEntity = idPersistenceMapper.toTypeIdEntity(normalizedTypeIdModel);

        // Asignar tenant ID del contexto actual
        typeIdEntity.setTenantId(TenantContext.getTenantId());

        if (typeIdEntity.getTiId() == null) {
            typeIdEntity.setTiId(normalizedTypeId);
        }

        typeIdRepository.save(typeIdEntity);
        TypeId result = idPersistenceMapper.toTypeId(typeIdEntity);
        return result;
    }

    /**
     * Obtiene todos los tipos de identificación.
     * @param entId Id de la entidad.
     * @return Lista de tipos de identificación.
     */
    @Override
    public List<TypeId> getAllTypeIds(String entId) {
        return idPersistenceMapper.toTypeIdList(typeIdRepository.findAllByTientId(entId));
    }

    /**
     * Actualiza un tipo de identificación.
     * @param typeId Tipo de identificación a actualizar.
     * @return Tipo de identificación actualizado.
     */
    @Override
    public TypeId updateTypeId(TypeId typeId) {
        if (typeId == null) {
            throw new TypeIdInvalidDataException("El tipo de identificación no puede ser null");
        }

        if (typeId.getTypeId() == null || typeId.getTypeId().trim().isEmpty()) {
            throw new TypeIdInvalidDataException("El código del tipo de identificación no puede estar vacío");
        }

        if (typeId.getTypeIdname() == null || typeId.getTypeIdname().trim().isEmpty()) {
            throw new TypeIdInvalidDataException("El nombre del tipo de identificación no puede estar vacío");
        }

        if (typeId.getClassification() == null) {
            throw PersonClassificationInvalidException.forNullClassification();
        }

        // Normalizar valores para validaciones
        String normalizedTypeId = StringNormalizer.normalizeCode(typeId.getTypeId());
        String normalizedTypeIdName = StringNormalizer.normalizePreservingCase(typeId.getTypeIdname());
        
        // Verificar que el tipo de identificación existe
        Optional<TypeIdEntity> existingEntity = typeIdRepository.findByTiIdAndTientId(normalizedTypeId, typeId.getEntId());
        if (existingEntity.isEmpty()) {
            throw new TypeIdNotFound("No se encontró el tipo de identificación con código '" + normalizedTypeId + "'");
        }

        TypeIdEntity currentEntity = existingEntity.get();
        
        // Validar que pertenece a la misma entidad
        if (!currentEntity.getTientId().equals(typeId.getEntId())) {
            throw new TypeIdInvalidDataException("El tipo de identificación no pertenece a la entidad especificada");
        }

        // Validar que no exista otro typeIdname similar (case-insensitive) excluyendo el actual
        if (!currentEntity.getTiName().equalsIgnoreCase(normalizedTypeIdName) &&
            typeIdRepository.existsByTiNameIgnoreCaseAndTientId(normalizedTypeIdName, typeId.getEntId())) {
            throw new TypeIdNameAlreadyExistsException(typeId.getTypeIdname());
        }

        // Crear el modelo normalizado para actualizar
        TypeId normalizedTypeIdModel = TypeId.builder()
                .id(currentEntity.getId()) 
                .typeId(normalizedTypeId)
                .typeIdname(normalizedTypeIdName)
                .entId(typeId.getEntId())
                .status(typeId.getStatus())
                .classification(typeId.getClassification())
                .build();

        TypeIdEntity typeIdEntity = idPersistenceMapper.toTypeIdEntity(normalizedTypeIdModel);
        
        // Preservar datos de auditoría
        typeIdEntity.setTenantId(currentEntity.getTenantId());
        typeIdEntity.setCreationDate(currentEntity.getCreationDate());

        typeIdRepository.save(typeIdEntity);
        return idPersistenceMapper.toTypeId(typeIdEntity);
    }

    /**
     * Actualiza un tipo de tercero.
     * @param thirdType Tipo de tercero a actualizar.
     * @return Tipo de tercero actualizado.
     */
    @Override
    public ThirdType updateThirdType(ThirdType thirdType) {
        if (thirdType == null) {
            throw new ThirdTypeInvalidDataException("El tipo de tercero no puede ser null");
        }

        if (thirdType.getThirdTypeId() == null) {
            throw new ThirdTypeInvalidDataException("El ID del tipo de tercero no puede ser null");
        }

        if (thirdType.getThirdTypeName() == null || thirdType.getThirdTypeName().trim().isEmpty()) {
            throw new ThirdTypeInvalidDataException("El nombre del tipo de tercero no puede estar vacío");
        }

        // Normalizar el nombre para validaciones
        String normalizedThirdTypeName = StringNormalizer.normalizePreservingCase(thirdType.getThirdTypeName());
        
        // Verificar que el tipo de tercero existe
        Optional<ThirdTypeEntity> existingEntity = thirdTypeRepository.findById(thirdType.getThirdTypeId());
        if (existingEntity.isEmpty()) {
            throw new ThirdTypeNotFound("No se encontró el tipo de tercero con ID '" + thirdType.getThirdTypeId() + "'");
        }

        ThirdTypeEntity currentEntity = existingEntity.get();
        
        // Validar que pertenece a la misma entidad
        if (!currentEntity.getTtentId().equals(thirdType.getEntId())) {
            throw new ThirdTypeInvalidDataException("El tipo de tercero no pertenece a la entidad especificada");
        }

        // Validar que no exista otro thirdTypeName similar (case-insensitive) excluyendo el actual
        if (!currentEntity.getTtName().equalsIgnoreCase(normalizedThirdTypeName) &&
            thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId(normalizedThirdTypeName, thirdType.getEntId())) {
            throw new ThirdTypeNameAlreadyExistsException(thirdType.getThirdTypeName());
        }

        // Crear el modelo normalizado para actualizar
        ThirdType normalizedThirdTypeModel = ThirdType.builder()
                .thirdTypeId(thirdType.getThirdTypeId())
                .thirdTypeName(normalizedThirdTypeName)
                .entId(thirdType.getEntId())
                .status(thirdType.getStatus())
                .build();

        ThirdTypeEntity thirdTypeEntity = idPersistenceMapper.toThirdTypeEntity(normalizedThirdTypeModel);

        thirdTypeEntity.setTenantId(currentEntity.getTenantId());
        thirdTypeEntity.setCreationDate(currentEntity.getCreationDate());

        thirdTypeRepository.save(thirdTypeEntity);
        return idPersistenceMapper.toThirdType(thirdTypeEntity);
    }
    
    /**
     * Verifica si existe un tipo de identificación por su ID.
     * @param typeIdId El ID del tipo de identificación
     * @return true si existe, false en caso contrario
     */
    @Override
    public boolean existsTypeIdById(Long typeIdId) {
        if (typeIdId == null) {
            return false;
        }
        return typeIdRepository.existsById(typeIdId);
    }
    
    /**
     * Verifica si existe un tipo de tercero por su ID.
     * @param thirdTypeId El ID del tipo de tercero
     * @return true si existe, false en caso contrario
     */
    @Override
    public boolean existsThirdTypeById(Long thirdTypeId) {
        if (thirdTypeId == null) {
            return false;
        }
        return thirdTypeRepository.existsById(thirdTypeId);
    }
    
    /**
     * Obtiene un tipo de identificación completo por su ID.
     * @param typeIdId El ID del tipo de identificación
     * @return El tipo de identificación completo o null si no existe
     */
    @Override
    public TypeId getTypeIdById(Long typeIdId) {
        if (typeIdId == null) {
            return null;
        }
        
        return typeIdRepository.findById(typeIdId)
                .map(idPersistenceMapper::toTypeId)
                .orElse(null);
    }
    
    /**
     * Obtiene un tipo de tercero completo por su ID.
     * @param thirdTypeId El ID del tipo de tercero
     * @return El tipo de tercero completo o null si no existe
     */
    @Override
    public ThirdType getThirdTypeById(Long thirdTypeId) {
        if (thirdTypeId == null) {
            return null;
        }
        
        return thirdTypeRepository.findById(thirdTypeId)
                .map(idPersistenceMapper::toThirdType)
                .orElse(null);
    }
    
    /**
     * Elimina un tipo de tercero del sistema.
     * @param thirdTypeId El ID del tipo de tercero a eliminar
     * @param entId El ID de la empresa
     * @return true si se eliminó correctamente, false en caso contrario
     */
    @Override
    public boolean deleteThirdType(Long thirdTypeId, String entId) {
        if (thirdTypeId == null || entId == null || entId.trim().isEmpty()) {
            return false;
        }
        
        String currentTenant = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId(currentTenant);
            
            Optional<ThirdTypeEntity> thirdTypeEntity = thirdTypeRepository.findByTtIdAndTtentId(thirdTypeId, entId);
            
            if (thirdTypeEntity.isPresent()) {
                thirdTypeRepository.delete(thirdTypeEntity.get());
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            // Log del error pero no lanzar excepción para mantener el contrato del método
            return false;
        } finally {
            TenantContext.setTenantId(currentTenant);
        }
    }
    
    /**
     * Verifica si un tipo de tercero está siendo utilizado por terceros existentes.
     * @param thirdTypeId El ID del tipo de tercero
     * @param entId El ID de la empresa
     * @return true si está en uso, false en caso contrario
     */
    @Override
    public boolean isThirdTypeInUse(Long thirdTypeId, String entId) {
        if (thirdTypeId == null || entId == null || entId.trim().isEmpty()) {
            return false;
        }
        
        String currentTenant = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId(currentTenant);
            
            // Verificar si existe algún tercero que use este tipo de tercero
            return thirdsAndTypesRepository.existsByTtId(thirdTypeId);
            
        } finally {
            TenantContext.setTenantId(currentTenant);
        }
    }
    
    /**
     * Elimina un tipo de identificación del sistema.
     * @param typeIdId El ID del tipo de identificación a eliminar
     * @param entId El ID de la empresa
     * @return true si se eliminó correctamente, false en caso contrario
     */
    @Override
    public boolean deleteTypeId(Long typeIdId, String entId) {
        if (typeIdId == null || entId == null || entId.trim().isEmpty()) {
            return false;
        }
        
        String currentTenant = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId(currentTenant);
            
            Optional<TypeIdEntity> typeIdEntity = typeIdRepository.findById(typeIdId);
            
            if (typeIdEntity.isPresent() && entId.equals(typeIdEntity.get().getTientId())) {
                typeIdRepository.delete(typeIdEntity.get());
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            // Log del error pero no lanzar excepción para mantener el contrato del método
            return false;
        } finally {
            TenantContext.setTenantId(currentTenant);
        }
    }
    
    /**
     * Verifica si un tipo de identificación está siendo utilizado por terceros existentes.
     * @param typeIdId El ID del tipo de identificación
     * @param entId El ID de la empresa
     * @return true si está en uso, false en caso contrario
     */
    @Override
    public boolean isTypeIdInUse(Long typeIdId, String entId) {
        if (typeIdId == null || entId == null || entId.trim().isEmpty()) {
            return false;
        }
        
        String currentTenant = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId(currentTenant);
            
            // Primero obtener el código del tipo de identificación
            Optional<TypeIdEntity> typeIdEntity = typeIdRepository.findById(typeIdId);
            if (typeIdEntity.isEmpty()) {
                return false;
            }
            
            String typeIdCode = typeIdEntity.get().getTiId();
            
            // Verificar si existe algún tercero que use este tipo de identificación
            return thirdRepository.existsByTypeIdTiIdAndEntId(typeIdCode, entId);
            
        } finally {
            TenantContext.setTenantId(currentTenant);
        }
    }

    /**
     * Obtiene todos los tipos de identificación con paginación y ordenamiento.
     * @param entId El id de la empresa
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de tipos de identificación
     */
    @Override
    public Page<TypeId> getAllTypeIdsWithSort(String entId, int page, int size, String sortField, String sortOrder) {
        Sort sort = "desc".equalsIgnoreCase(sortOrder)
            ? Sort.by(sortField).descending()
            : Sort.by(sortField).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TypeIdEntity> entityPage = typeIdRepository.findAllByTientIdPageable(entId, pageable);
        
        return entityPage.map(idPersistenceMapper::toTypeId);
    }

    /**
     * Busca tipos de identificación por empresa y término de búsqueda.
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de tipos de identificación que coinciden
     */
    @Override
    public Page<TypeId> findByEntIdAndSearch(String entId, String search, int page, int size, String sortField, String sortOrder) {
        Sort sort = "desc".equalsIgnoreCase(sortOrder)
            ? Sort.by(sortField).descending()
            : Sort.by(sortField).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TypeIdEntity> entityPage = typeIdRepository.findByTientIdAndSearch(entId, search, pageable);
        
        return entityPage.map(idPersistenceMapper::toTypeId);
    }

    /**
     * Cuenta tipos de identificación por empresa.
     * @param entId El id de la empresa
     * @return Cantidad de tipos de identificación
     */
    @Override
    public long countByEntId(String entId) {
        return typeIdRepository.countByTientId(entId);
    }

    /**
     * Cuenta tipos de identificación por empresa y término de búsqueda.
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @return Cantidad de tipos de identificación que coinciden
     */
    @Override
    public long countByEntIdAndSearch(String entId, String search) {
        return typeIdRepository.countByTientIdAndSearch(entId, search);
    }
}
