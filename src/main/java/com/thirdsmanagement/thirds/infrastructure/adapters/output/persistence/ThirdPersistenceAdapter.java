package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Set;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper.ThirdPersistenceMapper;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.TypeIdRepository;

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
    private final ThirdPersistenceMapper thirdPersistenceMapper;

    /**
     * Guarda un tercero.
     * @param third Tercero a guardar.
     * @return Tercero guardado.
     */
    @Override
    public Third saveThird(Third third) {

        ThirdEntity thirdEntity = thirdPersistenceMapper.toThirdEntity(third);

        TypeIdEntity typeIdEntity = typeIdRepository.getReferenceById(third.getTypeId().getTypeIdname());

        List<ThirdTypeEntity> thirdTypeEntities = thirdTypeRepository.findAll();

        if(typeIdEntity != null){
            for(ThirdType tt : third.getThirdTypes()){
                for(ThirdTypeEntity tte : thirdTypeEntities){
                    if(tte.getTtName().equals(tt.getThirdTypeName())){
                        thirdEntity.getThirdTypes().add(tte);
                    }
                }
            }
            thirdEntity = thirdRepository.save(thirdEntity);
        }

        Third result = thirdPersistenceMapper.toThird(thirdEntity);

        return result;
    }

    /**
     * Obtiene un tercero por su identificador.
     * @param id Identificador del tercero.
     * @return Tercero encontrado.
     */
    @Override
    public Optional<Third> getThirdById(Long id) {
        Optional<ThirdEntity> thirdEntity = thirdRepository.findById(id);

        if(thirdEntity.isEmpty()) {
            return Optional.empty();
        }

        Third third = convertToThird(thirdEntity.get());
        return Optional.of(third);
    }
    @Override
    public boolean existThirdById(long id, String entId) {
        System.out.println("Entrando a existThirdByID \n");
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
        String state = "";

        if(entity.getState().equals("true")){
            state="false";
        }else{
            state="true";
        }

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
     * Obtiene una página de terceros inactivos filtrados por el identificador de entidad.
     * @param entId El identificador de la entidad por la cual se filtrarán los terceros inactivos.
     * @param page El objeto Pageable que contiene la información de paginación.
     * @return Una página de objetos Third que representan los terceros inactivos.
     */
    @Override
    public Page<Third> getAllInactiveThirdsBy(String entId, Pageable page) {
        System.out.println("\n Entrando a getAllInactiveThirdsBy \n");
        Page<ThirdEntity> pageEntities = thirdRepository.getInactiveThirdsBy(entId, page);
        Page<Third> pageThirds = pageEntities.map(this::convertToThird);

        return pageThirds;
    }

    /**
     * Obtiene una página de proveedores basada en el ID de la entidad y la información de paginación proporcionada.
     * @param entId El ID de la entidad para la cual se desean obtener los proveedores.
     * @param page Información de paginación que incluye el número de página y el tamaño de la página.
     * @return Una página de objetos Third que representan a los proveedores.
     */
    @Override
    public Page<Third> getAllProvidersBy(String entId, Pageable page) {
        System.out.println("\n Entrando a getAllProvidersBy \n");
        Page<ThirdEntity> pageEntities = thirdRepository.getProvidersBy(entId, page);
        Page<Third> pageThirds = pageEntities.map(this::convertToThird);

        return pageThirds;
    }

    /**
     * Recupera una página de clientes basada en el identificador de la entidad y la información de paginación proporcionada.
     * @param entId El identificador de la entidad para filtrar los clientes.
     * @param page La información de paginación que incluye el número de página y el tamaño de la página.
     * @return Una página de objetos Third que representan a los clientes.
     */
    @Override
    public Page<Third> getAllCustomersBy(String entId, Pageable page) {
        System.out.println("\n Entrando a getAllCustomersBy \n");
        Page<ThirdEntity> pageEntities = thirdRepository.getCustomersBy(entId, page);
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

        for(ThirdTypeEntity tt : thirdEntity.getThirdTypes()){
            ThirdType thirdType = new ThirdType();
            thirdType.setThirdTypeName(tt.getTtName());
            thirdType.setThirdTypeId(tt.getTtId());
            obj.getThirdTypes().add(thirdType);
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
            thirdEntity.setCity(third.getCity());
            thirdEntity.setCountry(third.getCountry());
            thirdEntity.setProvince(third.getProvince());
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
            Set<ThirdTypeEntity> thirdTypeEntities = third.getThirdTypes().stream()
            .map(thirdType -> {
                ThirdTypeEntity entity = new ThirdTypeEntity();
                entity.setTtId(thirdType.getThirdTypeId());
                entity.setTtName(thirdType.getThirdTypeName());
                entity.setTtentId(thirdType.getEntId());
                
                // Log de cada tipo de tercero
                System.out.println("Tercer tipo: " + thirdType.getThirdTypeName());
                return entity;
            })
            .collect(Collectors.toSet());

        // Asignación de los tipos de tercero
        thirdEntity.setThirdTypes(thirdTypeEntities);
            thirdEntity = thirdRepository.save(thirdEntity);
        }
        return thirdPersistenceMapper.toThird(thirdEntity);
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
}
