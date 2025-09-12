package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.CityEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.CountryEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.StateEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.CityRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.CountryRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.StateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Inicializador de datos geográficos.
 * Carga los datos de países, estados y ciudades al iniciar el microservicio.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GeographyDataInitializer implements CommandLineRunner {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;

    @Override
    public void run(String... args) throws Exception {
        if (countryRepository.count() == 0) {
            loadCountries();
            loadAllStates();
            loadAllCities();
        }
    }

    private void loadCountries() {
        List<CountryEntity> countries = Arrays.asList(
            CountryEntity.builder()
                .countryCode("COL")
                .countryName("Colombia")
                .build(),
            CountryEntity.builder()
                .countryCode("USA")
                .countryName("Estados Unidos")
                .build(),
            CountryEntity.builder()
                .countryCode("MEX")
                .countryName("México")
                .build()
        );
        
        countryRepository.saveAll(countries);
    }

    private void loadAllStates() {
        loadColombianStates();
        loadUSAStates();
        loadMexicanStates();
    }

    private void loadColombianStates() {
        List<StateEntity> states = Arrays.asList(
            // Colombia - Todos los 32 departamentos + Distrito Capital
            StateEntity.builder()
                .stateCode("05")
                .countryCode("COL")
                .stateName("Antioquia")
                .build(),
            StateEntity.builder()
                .stateCode("08")
                .countryCode("COL")
                .stateName("Atlántico")
                .build(),
            StateEntity.builder()
                .stateCode("11")
                .countryCode("COL")
                .stateName("Bogotá D.C.")
                .build(),
            StateEntity.builder()
                .stateCode("13")
                .countryCode("COL")
                .stateName("Bolívar")
                .build(),
            StateEntity.builder()
                .stateCode("15")
                .countryCode("COL")
                .stateName("Boyacá")
                .build(),
            StateEntity.builder()
                .stateCode("17")
                .countryCode("COL")
                .stateName("Caldas")
                .build(),
            StateEntity.builder()
                .stateCode("18")
                .countryCode("COL")
                .stateName("Caquetá")
                .build(),
            StateEntity.builder()
                .stateCode("19")
                .countryCode("COL")
                .stateName("Cauca")
                .build(),
            StateEntity.builder()
                .stateCode("20")
                .countryCode("COL")
                .stateName("Cesar")
                .build(),
            StateEntity.builder()
                .stateCode("23")
                .countryCode("COL")
                .stateName("Córdoba")
                .build(),
            StateEntity.builder()
                .stateCode("25")
                .countryCode("COL")
                .stateName("Cundinamarca")
                .build(),
            StateEntity.builder()
                .stateCode("27")
                .countryCode("COL")
                .stateName("Chocó")
                .build(),
            StateEntity.builder()
                .stateCode("41")
                .countryCode("COL")
                .stateName("Huila")
                .build(),
            StateEntity.builder()
                .stateCode("44")
                .countryCode("COL")
                .stateName("La Guajira")
                .build(),
            StateEntity.builder()
                .stateCode("47")
                .countryCode("COL")
                .stateName("Magdalena")
                .build(),
            StateEntity.builder()
                .stateCode("50")
                .countryCode("COL")
                .stateName("Meta")
                .build(),
            StateEntity.builder()
                .stateCode("52")
                .countryCode("COL")
                .stateName("Nariño")
                .build(),
            StateEntity.builder()
                .stateCode("54")
                .countryCode("COL")
                .stateName("Norte de Santander")
                .build(),
            StateEntity.builder()
                .stateCode("63")
                .countryCode("COL")
                .stateName("Quindío")
                .build(),
            StateEntity.builder()
                .stateCode("66")
                .countryCode("COL")
                .stateName("Risaralda")
                .build(),
            StateEntity.builder()
                .stateCode("68")
                .countryCode("COL")
                .stateName("Santander")
                .build(),
            StateEntity.builder()
                .stateCode("70")
                .countryCode("COL")
                .stateName("Sucre")
                .build(),
            StateEntity.builder()
                .stateCode("73")
                .countryCode("COL")
                .stateName("Tolima")
                .build(),
            StateEntity.builder()
                .stateCode("76")
                .countryCode("COL")
                .stateName("Valle del Cauca")
                .build(),
            StateEntity.builder()
                .stateCode("81")
                .countryCode("COL")
                .stateName("Arauca")
                .build(),
            StateEntity.builder()
                .stateCode("85")
                .countryCode("COL")
                .stateName("Casanare")
                .build(),
            StateEntity.builder()
                .stateCode("86")
                .countryCode("COL")
                .stateName("Putumayo")
                .build(),
            StateEntity.builder()
                .stateCode("88")
                .countryCode("COL")
                .stateName("Archipiélago de San Andrés, Providencia y Santa Catalina")
                .build(),
            StateEntity.builder()
                .stateCode("91")
                .countryCode("COL")
                .stateName("Amazonas")
                .build(),
            StateEntity.builder()
                .stateCode("94")
                .countryCode("COL")
                .stateName("Guainía")
                .build(),
            StateEntity.builder()
                .stateCode("95")
                .countryCode("COL")
                .stateName("Guaviare")
                .build(),
            StateEntity.builder()
                .stateCode("97")
                .countryCode("COL")
                .stateName("Vaupés")
                .build(),
            StateEntity.builder()
                .stateCode("99")
                .countryCode("COL")
                .stateName("Vichada")
                .build()
        );
        
        stateRepository.saveAll(states);
    }

    private void loadUSAStates() {
        List<StateEntity> states = Arrays.asList(
            StateEntity.builder()
                .stateCode("CA")
                .countryCode("USA")
                .stateName("California")
                .build(),
            StateEntity.builder()
                .stateCode("NY")
                .countryCode("USA")
                .stateName("New York")
                .build(),
            StateEntity.builder()
                .stateCode("TX")
                .countryCode("USA")
                .stateName("Texas")
                .build(),
            StateEntity.builder()
                .stateCode("FL")
                .countryCode("USA")
                .stateName("Florida")
                .build(),
            StateEntity.builder()
                .stateCode("IL")
                .countryCode("USA")
                .stateName("Illinois")
                .build()
        );
        
        stateRepository.saveAll(states);
    }

    private void loadMexicanStates() {
        List<StateEntity> states = Arrays.asList(
            StateEntity.builder()
                .stateCode("CDMX")
                .countryCode("MEX")
                .stateName("Ciudad de México")
                .build(),
            StateEntity.builder()
                .stateCode("JAL")
                .countryCode("MEX")
                .stateName("Jalisco")
                .build(),
            StateEntity.builder()
                .stateCode("NL")
                .countryCode("MEX")
                .stateName("Nuevo León")
                .build(),
            StateEntity.builder()
                .stateCode("BC")
                .countryCode("MEX")
                .stateName("Baja California")
                .build()
        );
        
        stateRepository.saveAll(states);
    }

    private void loadAllCities() {
        loadColombianCities();
        loadUSACities();
        loadMexicanCities();
    }

    private void loadColombianCities() {
        List<CityEntity> cities = Arrays.asList(
            // Antioquia - Municipios principales
            CityEntity.builder()
                .cityCode("05001")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Medellín")
                .build(),
            CityEntity.builder()
                .cityCode("05002")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Abejorral")
                .build(),
            CityEntity.builder()
                .cityCode("05004")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Abriaquí")
                .build(),
            CityEntity.builder()
                .cityCode("05021")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Alejandría")
                .build(),
            CityEntity.builder()
                .cityCode("05030")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Amagá")
                .build(),
            CityEntity.builder()
                .cityCode("05031")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Amalfi")
                .build(),
            CityEntity.builder()
                .cityCode("05034")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Andes")
                .build(),
            CityEntity.builder()
                .cityCode("05036")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Angelópolis")
                .build(),
            CityEntity.builder()
                .cityCode("05038")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Angostura")
                .build(),
            CityEntity.builder()
                .cityCode("05040")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Anorí")
                .build(),
            CityEntity.builder()
                .cityCode("05042")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Santafé de Antioquia")
                .build(),
            CityEntity.builder()
                .cityCode("05044")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Anza")
                .build(),
            CityEntity.builder()
                .cityCode("05045")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Apartadó")
                .build(),
            CityEntity.builder()
                .cityCode("05051")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Arboletes")
                .build(),
            CityEntity.builder()
                .cityCode("05055")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Argelia")
                .build(),
            CityEntity.builder()
                .cityCode("05059")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Armenia")
                .build(),
            CityEntity.builder()
                .cityCode("05079")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Barbosa")
                .build(),
            CityEntity.builder()
                .cityCode("05086")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Belmira")
                .build(),
            CityEntity.builder()
                .cityCode("05088")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Bello")
                .build(),
            CityEntity.builder()
                .cityCode("05091")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Betania")
                .build(),
            CityEntity.builder()
                .cityCode("05093")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Betulia")
                .build(),
            CityEntity.builder()
                .cityCode("05101")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Ciudad Bolívar")
                .build(),
            CityEntity.builder()
                .cityCode("05107")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Briceño")
                .build(),
            CityEntity.builder()
                .cityCode("05113")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Buriticá")
                .build(),
            CityEntity.builder()
                .cityCode("05120")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Cáceres")
                .build(),
            CityEntity.builder()
                .cityCode("05125")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Caicedo")
                .build(),
            CityEntity.builder()
                .cityCode("05129")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Caldas")
                .build(),
            CityEntity.builder()
                .cityCode("05134")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Campamento")
                .build(),
            CityEntity.builder()
                .cityCode("05138")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Cañasgordas")
                .build(),
            CityEntity.builder()
                .cityCode("05142")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Caracolí")
                .build(),
            CityEntity.builder()
                .cityCode("05145")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Caramanta")
                .build(),
            CityEntity.builder()
                .cityCode("05147")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Carepa")
                .build(),
            CityEntity.builder()
                .cityCode("05148")
                .stateCode("05")
                .countryCode("COL")
                .cityName("El Carmen de Viboral")
                .build(),
            CityEntity.builder()
                .cityCode("05150")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Carolina")
                .build(),
            CityEntity.builder()
                .cityCode("05154")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Caucasia")
                .build(),
            CityEntity.builder()
                .cityCode("05172")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Chigorodó")
                .build(),
            CityEntity.builder()
                .cityCode("05190")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Cisneros")
                .build(),
            CityEntity.builder()
                .cityCode("05197")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Cocorná")
                .build(),
            CityEntity.builder()
                .cityCode("05206")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Concepción")
                .build(),
            CityEntity.builder()
                .cityCode("05209")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Concordia")
                .build(),
            CityEntity.builder()
                .cityCode("05212")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Copacabana")
                .build(),
            CityEntity.builder()
                .cityCode("05234")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Dabeiba")
                .build(),
            CityEntity.builder()
                .cityCode("05237")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Don Matías")
                .build(),
            CityEntity.builder()
                .cityCode("05240")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Ebéjico")
                .build(),
            CityEntity.builder()
                .cityCode("05250")
                .stateCode("05")
                .countryCode("COL")
                .cityName("El Bagre")
                .build(),
            CityEntity.builder()
                .cityCode("05264")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Entrerríos")
                .build(),
            CityEntity.builder()
                .cityCode("05266")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Envigado")
                .build(),
            CityEntity.builder()
                .cityCode("05282")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Fredonia")
                .build(),
            CityEntity.builder()
                .cityCode("05284")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Frontino")
                .build(),
            CityEntity.builder()
                .cityCode("05306")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Giraldo")
                .build(),
            CityEntity.builder()
                .cityCode("05308")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Girardota")
                .build(),
            CityEntity.builder()
                .cityCode("05310")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Gómez Plata")
                .build(),
            CityEntity.builder()
                .cityCode("05313")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Granada")
                .build(),
            CityEntity.builder()
                .cityCode("05315")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Guadalupe")
                .build(),
            CityEntity.builder()
                .cityCode("05318")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Guarne")
                .build(),
            CityEntity.builder()
                .cityCode("05321")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Guatapé")
                .build(),
            CityEntity.builder()
                .cityCode("05347")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Heliconia")
                .build(),
            CityEntity.builder()
                .cityCode("05353")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Hispania")
                .build(),
            CityEntity.builder()
                .cityCode("05360")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Itagüí")
                .build(),
            CityEntity.builder()
                .cityCode("05361")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Ituango")
                .build(),
            CityEntity.builder()
                .cityCode("05364")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Jardín")
                .build(),
            CityEntity.builder()
                .cityCode("05368")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Jericó")
                .build(),
            CityEntity.builder()
                .cityCode("05376")
                .stateCode("05")
                .countryCode("COL")
                .cityName("La Ceja")
                .build(),
            CityEntity.builder()
                .cityCode("05380")
                .stateCode("05")
                .countryCode("COL")
                .cityName("La Estrella")
                .build(),
            CityEntity.builder()
                .cityCode("05390")
                .stateCode("05")
                .countryCode("COL")
                .cityName("La Pintada")
                .build(),
            CityEntity.builder()
                .cityCode("05400")
                .stateCode("05")
                .countryCode("COL")
                .cityName("La Unión")
                .build(),
            CityEntity.builder()
                .cityCode("05411")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Liborina")
                .build(),
            CityEntity.builder()
                .cityCode("05425")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Maceo")
                .build(),
            CityEntity.builder()
                .cityCode("05440")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Marinilla")
                .build(),
            CityEntity.builder()
                .cityCode("05467")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Montebello")
                .build(),
            CityEntity.builder()
                .cityCode("05475")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Murindó")
                .build(),
            CityEntity.builder()
                .cityCode("05480")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Mutatá")
                .build(),
            CityEntity.builder()
                .cityCode("05483")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Nariño")
                .build(),
            CityEntity.builder()
                .cityCode("05490")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Necoclí")
                .build(),
            CityEntity.builder()
                .cityCode("05495")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Nechí")
                .build(),
            CityEntity.builder()
                .cityCode("05501")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Olaya")
                .build(),
            CityEntity.builder()
                .cityCode("05541")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Peñol")
                .build(),
            CityEntity.builder()
                .cityCode("05543")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Peque")
                .build(),
            CityEntity.builder()
                .cityCode("05576")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Pueblorrico")
                .build(),
            CityEntity.builder()
                .cityCode("05579")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Puerto Berrío")
                .build(),
            CityEntity.builder()
                .cityCode("05585")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Puerto Nare")
                .build(),
            CityEntity.builder()
                .cityCode("05591")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Puerto Triunfo")
                .build(),
            CityEntity.builder()
                .cityCode("05604")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Remedios")
                .build(),
            CityEntity.builder()
                .cityCode("05607")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Retiro")
                .build(),
            CityEntity.builder()
                .cityCode("05615")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Rionegro")
                .build(),
            CityEntity.builder()
                .cityCode("05628")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Sabanalarga")
                .build(),
            CityEntity.builder()
                .cityCode("05631")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Sabaneta")
                .build(),
            CityEntity.builder()
                .cityCode("05642")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Salgar")
                .build(),
            CityEntity.builder()
                .cityCode("05647")
                .stateCode("05")
                .countryCode("COL")
                .cityName("San Andrés de Cuerquia")
                .build(),
            CityEntity.builder()
                .cityCode("05649")
                .stateCode("05")
                .countryCode("COL")
                .cityName("San Carlos")
                .build(),
            CityEntity.builder()
                .cityCode("05652")
                .stateCode("05")
                .countryCode("COL")
                .cityName("San Francisco")
                .build(),
            CityEntity.builder()
                .cityCode("05656")
                .stateCode("05")
                .countryCode("COL")
                .cityName("San Jerónimo")
                .build(),
            CityEntity.builder()
                .cityCode("05658")
                .stateCode("05")
                .countryCode("COL")
                .cityName("San José de la Montaña")
                .build(),
            CityEntity.builder()
                .cityCode("05659")
                .stateCode("05")
                .countryCode("COL")
                .cityName("San Juan de Urabá")
                .build(),
            CityEntity.builder()
                .cityCode("05660")
                .stateCode("05")
                .countryCode("COL")
                .cityName("San Luis")
                .build(),
            CityEntity.builder()
                .cityCode("05664")
                .stateCode("05")
                .countryCode("COL")
                .cityName("San Pedro")
                .build(),
            CityEntity.builder()
                .cityCode("05665")
                .stateCode("05")
                .countryCode("COL")
                .cityName("San Pedro de Urabá")
                .build(),
            CityEntity.builder()
                .cityCode("05667")
                .stateCode("05")
                .countryCode("COL")
                .cityName("San Rafael")
                .build(),
            CityEntity.builder()
                .cityCode("05670")
                .stateCode("05")
                .countryCode("COL")
                .cityName("San Roque")
                .build(),
            CityEntity.builder()
                .cityCode("05674")
                .stateCode("05")
                .countryCode("COL")
                .cityName("San Vicente")
                .build(),
            CityEntity.builder()
                .cityCode("05679")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Santa Bárbara")
                .build(),
            CityEntity.builder()
                .cityCode("05686")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Santa Rosa de Osos")
                .build(),
            CityEntity.builder()
                .cityCode("05690")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Santo Domingo")
                .build(),
            CityEntity.builder()
                .cityCode("05697")
                .stateCode("05")
                .countryCode("COL")
                .cityName("El Santuario")
                .build(),
            CityEntity.builder()
                .cityCode("05736")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Segovia")
                .build(),
            CityEntity.builder()
                .cityCode("05756")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Sonsón")
                .build(),
            CityEntity.builder()
                .cityCode("05761")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Sopetrán")
                .build(),
            CityEntity.builder()
                .cityCode("05789")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Támesis")
                .build(),
            CityEntity.builder()
                .cityCode("05790")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Tarazá")
                .build(),
            CityEntity.builder()
                .cityCode("05792")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Tarso")
                .build(),
            CityEntity.builder()
                .cityCode("05809")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Titiribí")
                .build(),
            CityEntity.builder()
                .cityCode("05819")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Toledo")
                .build(),
            CityEntity.builder()
                .cityCode("05837")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Turbo")
                .build(),
            CityEntity.builder()
                .cityCode("05842")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Uramita")
                .build(),
            CityEntity.builder()
                .cityCode("05847")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Urrao")
                .build(),
            CityEntity.builder()
                .cityCode("05854")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Valdivia")
                .build(),
            CityEntity.builder()
                .cityCode("05856")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Valparaíso")
                .build(),
            CityEntity.builder()
                .cityCode("05858")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Vegachí")
                .build(),
            CityEntity.builder()
                .cityCode("05861")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Venecia")
                .build(),
            CityEntity.builder()
                .cityCode("05873")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Vigía del Fuerte")
                .build(),
            CityEntity.builder()
                .cityCode("05885")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Yalí")
                .build(),
            CityEntity.builder()
                .cityCode("05887")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Yarumal")
                .build(),
            CityEntity.builder()
                .cityCode("05890")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Yolombó")
                .build(),
            CityEntity.builder()
                .cityCode("05893")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Yondó")
                .build(),
            CityEntity.builder()
                .cityCode("05895")
                .stateCode("05")
                .countryCode("COL")
                .cityName("Zaragoza")
                .build(),
            
            // Atlántico - Todos los municipios
            CityEntity.builder()
                .cityCode("08001")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Barranquilla")
                .build(),
            CityEntity.builder()
                .cityCode("08078")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Baranoa")
                .build(),
            CityEntity.builder()
                .cityCode("08137")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Campo de la Cruz")
                .build(),
            CityEntity.builder()
                .cityCode("08141")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Candelaria")
                .build(),
            CityEntity.builder()
                .cityCode("08296")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Galapa")
                .build(),
            CityEntity.builder()
                .cityCode("08372")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Juan de Acosta")
                .build(),
            CityEntity.builder()
                .cityCode("08421")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Luruaco")
                .build(),
            CityEntity.builder()
                .cityCode("08433")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Malambo")
                .build(),
            CityEntity.builder()
                .cityCode("08436")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Manatí")
                .build(),
            CityEntity.builder()
                .cityCode("08520")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Palmar de Varela")
                .build(),
            CityEntity.builder()
                .cityCode("08549")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Piojó")
                .build(),
            CityEntity.builder()
                .cityCode("08558")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Polonuevo")
                .build(),
            CityEntity.builder()
                .cityCode("08560")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Ponedera")
                .build(),
            CityEntity.builder()
                .cityCode("08573")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Puerto Colombia")
                .build(),
            CityEntity.builder()
                .cityCode("08606")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Repelón")
                .build(),
            CityEntity.builder()
                .cityCode("08634")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Sabanagrande")
                .build(),
            CityEntity.builder()
                .cityCode("08638")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Sabanalarga")
                .build(),
            CityEntity.builder()
                .cityCode("08675")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Santa Lucía")
                .build(),
            CityEntity.builder()
                .cityCode("08685")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Santo Tomás")
                .build(),
            CityEntity.builder()
                .cityCode("08758")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Soledad")
                .build(),
            CityEntity.builder()
                .cityCode("08770")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Suan")
                .build(),
            CityEntity.builder()
                .cityCode("08832")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Tubará")
                .build(),
            CityEntity.builder()
                .cityCode("08849")
                .stateCode("08")
                .countryCode("COL")
                .cityName("Usiacurí")
                .build(),
            
            // Bogotá D.C.
            CityEntity.builder()
                .cityCode("11001")
                .stateCode("11")
                .countryCode("COL")
                .cityName("Bogotá")
                .build(),
            
            // Cauca
            CityEntity.builder()
                .cityCode("19001")
                .stateCode("19")
                .countryCode("COL")
                .cityName("Popayán")
                .build(),
            CityEntity.builder()
                .cityCode("19318")
                .stateCode("19")
                .countryCode("COL")
                .cityName("Guapi")
                .build(),
            
            // Valle del Cauca
            CityEntity.builder()
                .cityCode("76001")
                .stateCode("76")
                .countryCode("COL")
                .cityName("Cali")
                .build(),
            CityEntity.builder()
                .cityCode("76892")
                .stateCode("76")
                .countryCode("COL")
                .cityName("Yumbo")
                .build()
        );
        
        cityRepository.saveAll(cities);
    }

    private void loadUSACities() {
        List<CityEntity> cities = Arrays.asList(
            // California
            CityEntity.builder()
                .cityCode("CA001")
                .stateCode("CA")
                .countryCode("USA")
                .cityName("Los Angeles")
                .build(),
            CityEntity.builder()
                .cityCode("SF")
                .stateCode("CA")
                .countryCode("USA")
                .cityName("San Francisco")
                .build(),
            
            // New York
            CityEntity.builder()
                .cityCode("NY001")
                .stateCode("NY")
                .countryCode("USA")
                .cityName("New York City")
                .build(),
            
            // Texas
            CityEntity.builder()
                .cityCode("TX001")
                .stateCode("TX")
                .countryCode("USA")
                .cityName("Houston")
                .build()
        );
        
        cityRepository.saveAll(cities);
    }

    private void loadMexicanCities() {
        List<CityEntity> cities = Arrays.asList(
            // Ciudad de México
            CityEntity.builder()
                .cityCode("CDMX001")
                .stateCode("CDMX")
                .countryCode("MEX")
                .cityName("Ciudad de México")
                .build(),
            
            // Jalisco
            CityEntity.builder()
                .cityCode("JAL001")
                .stateCode("JAL")
                .countryCode("MEX")
                .cityName("Guadalajara")
                .build(),
            
            // Nuevo León
            CityEntity.builder()
                .cityCode("NL001")
                .stateCode("NL")
                .countryCode("MEX")
                .cityName("Monterrey")
                .build()
        );
        
        cityRepository.saveAll(cities);
    }
}
